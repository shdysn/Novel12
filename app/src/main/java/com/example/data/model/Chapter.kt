package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.UUID

@JsonClass(generateAdapter = true)
data class Chapter(
    @Json(name = "id") val id: String,
    @Json(name = "novel_id") val novelId: String,
    @Json(name = "chapter_number") val chapterNumber: Int,
    @Json(name = "title") val title: String,
    @Json(name = "content") val content: String,
    @Json(name = "word_count") val wordCount: Int = 0,
    @Json(name = "read_time_minutes") val readTimeMinutes: Int = 5,
    @Json(name = "publication_status") val publicationStatus: String = "published",
    @Json(name = "created_at") val createdAt: String = "",
    @Json(name = "updated_at") val updatedAt: String = "",
    val downloadStatus: com.example.data.local.DownloadStatus = com.example.data.local.DownloadStatus.AVAILABLE_OFFLINE
)

data class BookPage(
    val pageIndex: Int,          // 0-based
    val pageNumber: Int,         // 1-based (pageIndex + 1)
    val startCharOffset: Int,
    val endCharOffset: Int,
    val text: String,
    val isFirstPageOfChapter: Boolean = false,
    val isLastPageOfChapter: Boolean = false
)

data class Bookmark(
    val id: String = UUID.randomUUID().toString(),
    val novelId: String,
    val novelTitle: String,
    val author: String,
    val chapterId: String,
    val chapterNumber: Int,
    val chapterTitle: String,
    val charOffset: Int = 0,
    val pageNumber: Int = 1,
    val snippet: String = "",
    val note: String = "",
    val progressPercent: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
) {
    val scrollProgressPercent: Float get() = progressPercent
}
