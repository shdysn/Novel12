package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Bookmark

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val id: String,
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
    fun toDomainModel(): Bookmark {
        return Bookmark(
            id = id,
            novelId = novelId,
            novelTitle = novelTitle,
            author = author,
            chapterId = chapterId,
            chapterNumber = chapterNumber,
            chapterTitle = chapterTitle,
            charOffset = charOffset,
            pageNumber = pageNumber,
            snippet = snippet,
            note = note,
            progressPercent = progressPercent,
            timestamp = timestamp
        )
    }

    companion object {
        fun fromDomainModel(b: Bookmark): BookmarkEntity {
            return BookmarkEntity(
                id = b.id,
                novelId = b.novelId,
                novelTitle = b.novelTitle,
                author = b.author,
                chapterId = b.chapterId,
                chapterNumber = b.chapterNumber,
                chapterTitle = b.chapterTitle,
                charOffset = b.charOffset,
                pageNumber = b.pageNumber,
                snippet = b.snippet,
                note = b.note,
                progressPercent = b.progressPercent,
                timestamp = b.timestamp
            )
        }
    }
}
