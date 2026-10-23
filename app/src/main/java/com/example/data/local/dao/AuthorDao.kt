package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.AuthorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthorDao {

    @Query("SELECT * FROM authors ORDER BY nameUrdu ASC")
    fun getAllAuthorsFlow(): Flow<List<AuthorEntity>>

    @Query("SELECT * FROM authors WHERE id = :id LIMIT 1")
    suspend fun getAuthorById(id: String): AuthorEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthors(authors: List<AuthorEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthor(author: AuthorEntity)

    @Query("DELETE FROM authors WHERE id = :id")
    suspend fun deleteAuthorById(id: String)
}
