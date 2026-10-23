package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.NovelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NovelDao {

    @Query("SELECT * FROM novels ORDER BY createdAt DESC, viewsCount DESC")
    fun getAllNovelsFlow(): Flow<List<NovelEntity>>

    @Query("SELECT * FROM novels WHERE isDownloaded = 1 ORDER BY titleUrdu ASC")
    fun getDownloadedNovelsFlow(): Flow<List<NovelEntity>>

    @Query("SELECT * FROM novels WHERE isFavorite = 1 OR readingProgress > 0 ORDER BY readingProgress DESC")
    fun getMyBooksFlow(): Flow<List<NovelEntity>>

    @Query("SELECT * FROM novels WHERE id = :id LIMIT 1")
    suspend fun getNovelById(id: String): NovelEntity?

    @Query("SELECT * FROM novels WHERE id = :id LIMIT 1")
    fun getNovelByIdFlow(id: String): Flow<NovelEntity?>

    @Query("""
        SELECT * FROM novels 
        WHERE titleUrdu LIKE '%' || :query || '%' 
           OR authorName LIKE '%' || :query || '%' 
           OR categoryName LIKE '%' || :query || '%' 
           OR description LIKE '%' || :query || '%'
        ORDER BY viewsCount DESC
    """)
    suspend fun searchNovels(query: String): List<NovelEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNovel(novel: NovelEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNovels(novels: List<NovelEntity>)

    @Update
    suspend fun updateNovel(novel: NovelEntity)

    @Query("UPDATE novels SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean)

    @Query("UPDATE novels SET readingProgress = :progress, lastReadChapterId = :lastChapterId, lastReadOffset = :lastOffset WHERE id = :id")
    suspend fun updateReadingProgress(id: String, progress: Float, lastChapterId: String?, lastOffset: Int)

    @Query("UPDATE novels SET downloadStatus = :status, isDownloaded = :isDownloaded WHERE id = :id")
    suspend fun updateDownloadStatus(id: String, status: String, isDownloaded: Boolean)

    @Query("""
        UPDATE novels 
        SET titleUrdu = :title, 
            authorName = :author, 
            categoryName = :category, 
            description = :synopsis, 
            publicationStatus = :publicationStatus, 
            coverImageUrl = :coverImageUrl,
            updatedAt = :updatedAt
        WHERE id = :id
    """)
    suspend fun updateNovelMetadata(
        id: String,
        title: String,
        author: String,
        category: String,
        synopsis: String,
        publicationStatus: String,
        coverImageUrl: String?,
        updatedAt: String
    )

    @Query("DELETE FROM novels WHERE id = :id")
    suspend fun deleteNovelById(id: String)
}
