package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.ChapterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterDao {

    @Query("SELECT * FROM chapters ORDER BY chapterNumber ASC")
    fun getAllChaptersFlow(): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM chapters WHERE novelId = :novelId ORDER BY chapterNumber ASC")
    suspend fun getChaptersForNovel(novelId: String): List<ChapterEntity>

    @Query("SELECT * FROM chapters WHERE novelId = :novelId ORDER BY chapterNumber ASC")
    fun getChaptersForNovelFlow(novelId: String): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM chapters WHERE id = :id LIMIT 1")
    suspend fun getChapterById(id: String): ChapterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<ChapterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: ChapterEntity)

    @Update
    suspend fun updateChapter(chapter: ChapterEntity)

    @Query("UPDATE chapters SET downloadStatus = :status, isDownloaded = :isDownloaded WHERE id = :id")
    suspend fun updateDownloadStatus(id: String, status: String, isDownloaded: Boolean)

    @Query("UPDATE chapters SET downloadStatus = :status, isDownloaded = :isDownloaded WHERE novelId = :novelId")
    suspend fun updateNovelChaptersDownloadStatus(novelId: String, status: String, isDownloaded: Boolean)

    @Query("UPDATE chapters SET content = '', downloadStatus = 'NOT_DOWNLOADED', isDownloaded = 0 WHERE novelId = :novelId")
    suspend fun clearChapterContentForNovel(novelId: String)

    @Query("DELETE FROM chapters WHERE novelId = :novelId")
    suspend fun deleteChaptersForNovel(novelId: String)

    @Query("DELETE FROM chapters WHERE id = :id")
    suspend fun deleteChapterById(id: String)
}
