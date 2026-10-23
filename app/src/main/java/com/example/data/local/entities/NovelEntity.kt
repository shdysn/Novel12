package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.local.DownloadStatus
import com.example.data.model.Novel

@Entity(tableName = "novels")
data class NovelEntity(
    @PrimaryKey val id: String,
    val titleUrdu: String,
    val titleEnglish: String = "",
    val authorId: String = "",
    val authorName: String,
    val categoryId: String = "",
    val categoryName: String,
    val description: String,
    val coverImageUrl: String? = null,
    val coverGradientIndex: Int = 0,
    val publicationStatus: String = "published", // "published" or "draft"
    val isFeatured: Boolean = false,
    val isFavorite: Boolean = false,
    val readingProgress: Float = 0f,
    val viewsCount: Int = 1200,
    val rating: Float = 4.8f,
    val totalChapters: Int = 1,
    val isTrending: Boolean = false,
    val isNew: Boolean = false,
    val isRecommended: Boolean = false,
    val language: String = "اردو (Urdu)",
    val createdAt: String = "",
    val updatedAt: String = "",
    val downloadStatus: String = DownloadStatus.AVAILABLE_OFFLINE.name,
    val isDownloaded: Boolean = true,
    val lastReadChapterId: String? = null,
    val lastReadOffset: Int = 0
) {
    fun toDomainModel(): Novel {
        return Novel(
            id = id,
            title = titleUrdu,
            author = authorName,
            category = categoryName,
            synopsis = description,
            rating = rating,
            viewsCount = viewsCount,
            coverGradientIndex = coverGradientIndex,
            totalChapters = totalChapters,
            isFavorite = isFavorite,
            isDownloaded = isDownloaded,
            isTrending = isTrending,
            isNew = isNew,
            isRecommended = isRecommended,
            readingProgress = readingProgress,
            language = language,
            createdAt = createdAt,
            updatedAt = updatedAt,
            isCustomPublished = id.startsWith("custom_"),
            publicationStatus = publicationStatus,
            coverImageUrl = coverImageUrl,
            authorId = authorId.ifBlank { null },
            categoryId = categoryId.ifBlank { null },
            downloadStatus = DownloadStatus.fromString(downloadStatus)
        )
    }

    companion object {
        fun fromDomainModel(novel: Novel, status: DownloadStatus = DownloadStatus.AVAILABLE_OFFLINE): NovelEntity {
            return NovelEntity(
                id = novel.id,
                titleUrdu = novel.title,
                authorName = novel.author,
                authorId = novel.authorId ?: "",
                categoryId = novel.categoryId ?: "",
                categoryName = novel.category,
                description = novel.synopsis,
                coverImageUrl = novel.coverImageUrl,
                coverGradientIndex = novel.coverGradientIndex,
                publicationStatus = novel.publicationStatus,
                isFeatured = novel.isRecommended,
                isFavorite = novel.isFavorite,
                readingProgress = novel.readingProgress,
                viewsCount = novel.viewsCount,
                rating = novel.rating,
                totalChapters = novel.totalChapters,
                isTrending = novel.isTrending,
                isNew = novel.isNew,
                isRecommended = novel.isRecommended,
                language = novel.language,
                createdAt = novel.createdAt,
                updatedAt = novel.updatedAt,
                downloadStatus = status.name,
                isDownloaded = status == DownloadStatus.AVAILABLE_OFFLINE
            )
        }
    }
}
