package com.example.data.supabase

object TypeScriptGuide {

    const val SUPABASE_SQL_SCHEMA = """-- ====================================================================
-- URDU NOVELS APP - COMPLETE SUPABASE CLOUD SCHEMA & RLS MIGRATIONS
-- ====================================================================
-- Run this entire script in your Supabase SQL Editor (SQL Editor -> New query)

-- 1. Enable UUID Extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. Authors Table
CREATE TABLE IF NOT EXISTS public.authors (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name_urdu TEXT NOT NULL,
    name_english TEXT,
    biography TEXT,
    portrait_url TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 3. Categories Table
CREATE TABLE IF NOT EXISTS public.categories (
    id TEXT PRIMARY KEY,
    name_urdu TEXT NOT NULL,
    name_english TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 4. Novels Table
CREATE TABLE IF NOT EXISTS public.novels (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title_urdu TEXT NOT NULL,
    title_english TEXT,
    author_id UUID REFERENCES public.authors(id) ON DELETE SET NULL,
    author TEXT, -- Denormalized for rapid listing
    category_id TEXT REFERENCES public.categories(id) ON DELETE SET NULL,
    category TEXT, -- Denormalized for rapid listing
    description TEXT NOT NULL,
    cover_image_url TEXT,
    cover_gradient_index INTEGER DEFAULT 0,
    publication_status TEXT NOT NULL DEFAULT 'published', -- 'published' or 'draft'
    is_featured BOOLEAN DEFAULT false,
    rating NUMERIC(3, 2) DEFAULT 4.8,
    views_count INTEGER DEFAULT 0,
    total_chapters INTEGER DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 5. Chapters Table (Keeps heavy novel text separated from novel metadata queries)
CREATE TABLE IF NOT EXISTS public.chapters (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    novel_id UUID NOT NULL REFERENCES public.novels(id) ON DELETE CASCADE,
    chapter_number INTEGER NOT NULL,
    chapter_title TEXT NOT NULL,
    content TEXT NOT NULL, -- مکمل یونیکوڈ اردو متن (Unicode Urdu Content)
    word_count INTEGER DEFAULT 0,
    read_time_minutes INTEGER DEFAULT 5,
    publication_status TEXT NOT NULL DEFAULT 'published', -- 'published' or 'draft'
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 6. Performance Indexes
CREATE INDEX IF NOT EXISTS idx_novels_status ON public.novels (publication_status);
CREATE INDEX IF NOT EXISTS idx_novels_author ON public.novels (author_id);
CREATE INDEX IF NOT EXISTS idx_novels_category ON public.novels (category_id);
CREATE INDEX IF NOT EXISTS idx_chapters_novel_number ON public.chapters (novel_id, chapter_number);
CREATE INDEX IF NOT EXISTS idx_chapters_status ON public.chapters (publication_status);

-- 7. Row Level Security (RLS) Configuration
ALTER TABLE public.authors ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.categories ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.novels ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.chapters ENABLE ROW LEVEL SECURITY;

-- Public can read all published novels and their chapters
CREATE POLICY "Public Read Authors" ON public.authors 
    FOR SELECT USING (true);

CREATE POLICY "Public Read Categories" ON public.categories 
    FOR SELECT USING (true);

CREATE POLICY "Public Read Published Novels" ON public.novels 
    FOR SELECT USING (publication_status = 'published');

CREATE POLICY "Public Read Published Chapters" ON public.chapters 
    FOR SELECT USING (publication_status = 'published');

-- Authorized/Admin policies (Allow insert, update, delete with anon/authenticated client)
CREATE POLICY "Admin Insert Authors" ON public.authors 
    FOR INSERT WITH CHECK (true);
CREATE POLICY "Admin Update Authors" ON public.authors 
    FOR UPDATE USING (true);

CREATE POLICY "Admin Insert Categories" ON public.categories 
    FOR INSERT WITH CHECK (true);
CREATE POLICY "Admin Update Categories" ON public.categories 
    FOR UPDATE USING (true);

CREATE POLICY "Admin All Novels" ON public.novels 
    FOR ALL USING (true);

CREATE POLICY "Admin All Chapters" ON public.chapters 
    FOR ALL USING (true);

-- 8. Storage Bucket for Novel Covers
INSERT INTO storage.buckets (id, name, public) 
VALUES ('novel-covers', 'novel-covers', true)
ON CONFLICT (id) DO NOTHING;

CREATE POLICY "Public Access Covers" ON storage.objects 
    FOR SELECT USING (bucket_id = 'novel-covers');

CREATE POLICY "Admin Upload Covers" ON storage.objects 
    FOR INSERT WITH CHECK (bucket_id = 'novel-covers');
"""

    const val TYPESCRIPT_PUBLISH_SCRIPT = """// push_novels.ts
// npm install @supabase/supabase-js dotenv
// Run: npx ts-node push_novels.ts

import { createClient } from '@supabase/supabase-js';
import * as dotenv from 'dotenv';
dotenv.config();

const SUPABASE_URL = process.env.SUPABASE_URL || 'https://YOUR_PROJECT.supabase.co';
const SUPABASE_KEY = process.env.SUPABASE_ANON_KEY || 'YOUR_ANON_KEY';
const supabase = createClient(SUPABASE_URL, SUPABASE_KEY);

interface NovelData {
  title_urdu: string;
  author: string;
  category: string;
  description: string;
  publication_status: 'published' | 'draft';
  chapters: {
    chapter_number: number;
    chapter_title: string;
    content: string; // خالص یونیکوڈ اردو متن
  }[];
}

const sampleNovel: NovelData = {
  title_urdu: "پیرِ کامل (ص)",
  author: "عمیرہ احمد",
  category: "روحانی و تصوف",
  description: "یہ کہانی ہے زندگی کے گمبھیر اندھیروں سے نکل کر نور کی تلاش کرنے والے دو متضاد نفوس کی...",
  publication_status: "published",
  chapters: [
    {
      chapter_number: 1,
      chapter_title: "باب اول: سفر کا آغاز",
      content: "زندگی میں بعض فیصلے ایسے ہوتے ہیں جنہیں وقت نہیں انسان خود منتخب کرتا ہے۔ امامہ ہاشم نے جب پہلی بار اپنی ذات کے دائرے سے باہر جھانکا تو اسے ہر سو ایک خاموش سناٹا دکھائی دیا..."
    }
  ]
};

async function publish() {
  console.log("اشاعت کا آغاز...");
  const { data: novel, error: nErr } = await supabase
    .from('novels')
    .insert({
      title_urdu: sampleNovel.title_urdu,
      author: sampleNovel.author,
      category: sampleNovel.category,
      description: sampleNovel.description,
      publication_status: sampleNovel.publication_status,
      total_chapters: sampleNovel.chapters.length
    })
    .select()
    .single();

  if (nErr) {
    console.error("ناول محفوظ کرتے ہوئے خرابی:", nErr);
    return;
  }

  for (const ch of sampleNovel.chapters) {
    const words = ch.content.trim().split(/\s+/).length;
    await supabase.from('chapters').insert({
      novel_id: novel.id,
      chapter_number: ch.chapter_number,
      chapter_title: ch.chapter_title,
      content: ch.content,
      word_count: words,
      read_time_minutes: Math.max(1, Math.ceil(words / 100)),
      publication_status: 'published'
    });
  }
  console.log("ناول اور ابواب کامیابی سے کلاؤڈ پر منتقل ہو گئے!");
}

publish();
"""

    const val TYPESCRIPT_FETCH_SCRIPT = """// fetch_novels.ts
// Supabase سے شائع شدہ اردو ناولز حاصل کرنے کا اسکرپٹ
import { createClient } from '@supabase/supabase-js';

const SUPABASE_URL = process.env.SUPABASE_URL || 'https://YOUR_PROJECT.supabase.co';
const SUPABASE_KEY = process.env.SUPABASE_ANON_KEY || 'YOUR_ANON_KEY';
const supabase = createClient(SUPABASE_URL, SUPABASE_KEY);

async function fetchLibrary() {
  const { data: novels, error } = await supabase
    .from('novels')
    .select('*')
    .eq('publication_status', 'published')
    .order('created_at', { ascending: false });

  if (error) {
    console.error("خرابی:", error);
    return;
  }

  console.log(`کل دستیاب کلاؤڈ ناولز: ${'$'}{novels.length}`);
  for (const n of novels) {
    console.log(`- ${'$'}{n.title_urdu} (از ${'$'}{n.author}) [${'$'}{n.category}]`);
  }
}

fetchLibrary();
"""
}
