package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.local.DownloadStatus
import com.example.data.model.Chapter

@Entity(
    tableName = "chapters",
    indices = [Index(value = ["novelId", "chapterNumber"])]
)
data class ChapterEntity(
    @PrimaryKey val id: String,
    val novelId: String,
    val chapterNumber: Int,
    val chapterTitle: String,
    val content: String,
    val wordCount: Int = 0,
    val readTimeMinutes: Int = 5,
    val publicationStatus: String = "published",
    val createdAt: String = "",
    val updatedAt: String = "",
    val downloadStatus: String = DownloadStatus.AVAILABLE_OFFLINE.name,
    val isDownloaded: Boolean = true
) {
    fun toDomainModel(): Chapter {
        return Chapter(
            id = id,
            novelId = novelId,
            chapterNumber = chapterNumber,
            title = chapterTitle,
            content = content,
            wordCount = wordCount,
            readTimeMinutes = readTimeMinutes,
            publicationStatus = publicationStatus,
            createdAt = createdAt,
            updatedAt = updatedAt,
            downloadStatus = DownloadStatus.fromString(downloadStatus)
        )
    }

    companion object {
        fun fromDomainModel(chapter: Chapter, status: DownloadStatus = DownloadStatus.AVAILABLE_OFFLINE): ChapterEntity {
            return ChapterEntity(
                id = chapter.id,
                novelId = chapter.novelId,
                chapterNumber = chapter.chapterNumber,
                chapterTitle = chapter.title,
                content = chapter.content,
                wordCount = chapter.wordCount,
                readTimeMinutes = chapter.readTimeMinutes,
                publicationStatus = chapter.publicationStatus,
                createdAt = chapter.createdAt,
                updatedAt = chapter.updatedAt,
                downloadStatus = status.name,
                isDownloaded = status == DownloadStatus.AVAILABLE_OFFLINE
            )
        }
    }
}
