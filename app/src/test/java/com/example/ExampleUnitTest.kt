package com.example

import com.example.data.local.SampleUrduNovels
import com.example.data.model.NovelCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testSampleUrduNovels_loadedSuccessfully() {
        val novels = SampleUrduNovels.novelsList
        assertTrue("Novels list should not be empty", novels.isNotEmpty())
        assertTrue("Novels list should have at least 5 novels", novels.size >= 5)

        // Check Peer-e-Kamil
        val peerEKamil = novels.firstOrNull { it.id == "novel_peer_e_kamil" }
        assertNotNull(peerEKamil)
        assertEquals("پیرِ کامل (ص)", peerEKamil?.title)
        assertEquals("عمیرہ احمد", peerEKamil?.author)

        // Check chapters
        val peerChapters = SampleUrduNovels.chaptersMap["novel_peer_e_kamil"]
        assertNotNull(peerChapters)
        assertTrue(peerChapters!!.isNotEmpty())
        assertTrue(peerChapters.first().content.contains("امامہ ہاشم"))
    }

    @Test
    fun testNovelCategories_mapping() {
        assertEquals(NovelCategory.ROMANCE, NovelCategory.fromUrdu("رومانوی"))
        assertEquals(NovelCategory.SPIRITUAL, NovelCategory.fromUrdu("روحانی"))
        assertEquals(NovelCategory.SOCIAL, NovelCategory.fromUrdu("معاشرتی"))
        assertEquals(NovelCategory.ISLAHI, NovelCategory.fromUrdu("اصلاحی"))
    }

    @Test
    fun testTextPaginator_findPageIndexForCharOffset() {
        val samplePages = listOf(
            com.example.data.model.BookPage(
                pageIndex = 0,
                pageNumber = 1,
                startCharOffset = 0,
                endCharOffset = 100,
                text = "صفحہ 1",
                isFirstPageOfChapter = true,
                isLastPageOfChapter = false
            ),
            com.example.data.model.BookPage(
                pageIndex = 1,
                pageNumber = 2,
                startCharOffset = 100,
                endCharOffset = 250,
                text = "صفحہ 2",
                isFirstPageOfChapter = false,
                isLastPageOfChapter = true
            )
        )

        assertEquals(0, com.example.util.TextPaginator.findPageIndexForCharOffset(samplePages, 0))
        assertEquals(0, com.example.util.TextPaginator.findPageIndexForCharOffset(samplePages, 50))
        assertEquals(1, com.example.util.TextPaginator.findPageIndexForCharOffset(samplePages, 100))
        assertEquals(1, com.example.util.TextPaginator.findPageIndexForCharOffset(samplePages, 200))
        assertEquals(1, com.example.util.TextPaginator.findPageIndexForCharOffset(samplePages, 500)) // clamps to last page
    }

    @Test
    fun testDownloadStatus_enumValues() {
        assertEquals(com.example.data.local.DownloadStatus.AVAILABLE_OFFLINE, com.example.data.local.DownloadStatus.fromString("AVAILABLE_OFFLINE"))
        assertEquals(com.example.data.local.DownloadStatus.DOWNLOADING, com.example.data.local.DownloadStatus.fromString("DOWNLOADING"))
        assertEquals(com.example.data.local.DownloadStatus.UPDATE_AVAILABLE, com.example.data.local.DownloadStatus.fromString("UPDATE_AVAILABLE"))
        assertEquals(com.example.data.local.DownloadStatus.FAILED, com.example.data.local.DownloadStatus.fromString("FAILED"))
        assertEquals(com.example.data.local.DownloadStatus.NOT_DOWNLOADED, com.example.data.local.DownloadStatus.fromString("NOT_DOWNLOADED"))
        assertEquals(com.example.data.local.DownloadStatus.NOT_DOWNLOADED, com.example.data.local.DownloadStatus.fromString(null))
    }

    @Test
    fun testNovelEntity_domainMapping() {
        val novel = com.example.data.model.Novel(
            id = "test_novel_1",
            title = "خواب نگر",
            author = "نمرہ احمد",
            category = "رومانوی",
            synopsis = "ایک دلکش رومانوی داستان",
            publicationStatus = "published",
            coverImageUrl = "https://example.com/cover.jpg",
            totalChapters = 5,
            downloadStatus = com.example.data.local.DownloadStatus.AVAILABLE_OFFLINE
        )

        val entity = com.example.data.local.entities.NovelEntity.fromDomainModel(novel, com.example.data.local.DownloadStatus.AVAILABLE_OFFLINE)
        assertEquals("test_novel_1", entity.id)
        assertEquals("خواب نگر", entity.titleUrdu)
        assertEquals("published", entity.publicationStatus)
        assertEquals("https://example.com/cover.jpg", entity.coverImageUrl)
        assertTrue(entity.isDownloaded)

        val convertedBack = entity.toDomainModel()
        assertEquals(novel.id, convertedBack.id)
        assertEquals(novel.title, convertedBack.title)
        assertEquals(novel.author, convertedBack.author)
        assertEquals(novel.publicationStatus, convertedBack.publicationStatus)
    }

    @Test
    fun testSupabaseDto_compatibility() {
        val novelDto = com.example.data.supabase.NovelDto(
            id = "uuid-1234",
            titleUrdu = "آبِ حیات",
            author = "عمیرہ احمد",
            publicationStatus = "published"
        )
        assertEquals("آبِ حیات", novelDto.displayTitle)
        assertTrue(novelDto.isPublished)

        val draftDto = com.example.data.supabase.NovelDto(
            id = "uuid-5678",
            titleUrdu = "مسودہ",
            publicationStatus = "draft"
        )
        org.junit.Assert.assertFalse(draftDto.isPublished)
    }
}
