package com.example.data.supabase

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NovelDto(
    @Json(name = "id") val id: String,
    @Json(name = "title_urdu") val titleUrdu: String? = null,
    @Json(name = "title_english") val titleEnglish: String? = null,
    @Json(name = "title") val title: String? = null, // fallback compatibility
    @Json(name = "author_id") val authorId: String? = null,
    @Json(name = "author") val author: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "synopsis") val synopsis: String? = null, // fallback compatibility
    @Json(name = "cover_image_url") val coverImageUrl: String? = null,
    @Json(name = "category_id") val categoryId: String? = null,
    @Json(name = "category") val category: String? = null,
    @Json(name = "publication_status") val publicationStatus: String = "published", // "published" or "draft"
    @Json(name = "is_featured") val isFeatured: Boolean = false,
    @Json(name = "rating") val rating: Float = 4.8f,
    @Json(name = "views_count") val viewsCount: Int = 1200,
    @Json(name = "cover_gradient_index") val coverGradientIndex: Int = 0,
    @Json(name = "total_chapters") val totalChapters: Int = 1,
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "updated_at") val updatedAt: String? = null
) {
    val displayTitle: String get() = titleUrdu?.ifBlank { null } ?: title ?: "اردو ناول"
    val displayDescription: String get() = description?.ifBlank { null } ?: synopsis ?: ""
    val isPublished: Boolean get() = publicationStatus.equals("published", ignoreCase = true)
}

@JsonClass(generateAdapter = true)
data class ChapterDto(
    @Json(name = "id") val id: String,
    @Json(name = "novel_id") val novelId: String,
    @Json(name = "chapter_number") val chapterNumber: Int,
    @Json(name = "chapter_title") val chapterTitle: String? = null,
    @Json(name = "title") val title: String? = null, // fallback compatibility
    @Json(name = "content") val content: String,
    @Json(name = "word_count") val wordCount: Int = 0,
    @Json(name = "read_time_minutes") val readTimeMinutes: Int = 5,
    @Json(name = "publication_status") val publicationStatus: String = "published",
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "updated_at") val updatedAt: String? = null
) {
    val displayTitle: String get() = chapterTitle?.ifBlank { null } ?: title ?: "باب $chapterNumber"
    val isPublished: Boolean get() = publicationStatus.equals("published", ignoreCase = true)
}

@JsonClass(generateAdapter = true)
data class AuthorDto(
    @Json(name = "id") val id: String,
    @Json(name = "name_urdu") val nameUrdu: String,
    @Json(name = "name_english") val nameEnglish: String? = null,
    @Json(name = "biography") val biography: String? = null,
    @Json(name = "portrait_url") val portraitUrl: String? = null,
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "updated_at") val updatedAt: String? = null
)

@JsonClass(generateAdapter = true)
data class CategoryDto(
    @Json(name = "id") val id: String,
    @Json(name = "name_urdu") val nameUrdu: String,
    @Json(name = "name_english") val nameEnglish: String? = null,
    @Json(name = "created_at") val createdAt: String? = null
)
