package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.AuthorDao
import com.example.data.local.dao.BookmarkDao
import com.example.data.local.dao.CategoryDao
import com.example.data.local.dao.ChapterDao
import com.example.data.local.dao.NovelDao
import com.example.data.local.entities.AuthorEntity
import com.example.data.local.entities.BookmarkEntity
import com.example.data.local.entities.CategoryEntity
import com.example.data.local.entities.ChapterEntity
import com.example.data.local.entities.NovelEntity

@Database(
    entities = [
        NovelEntity::class,
        ChapterEntity::class,
        AuthorEntity::class,
        CategoryEntity::class,
        BookmarkEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun novelDao(): NovelDao
    abstract fun chapterDao(): ChapterDao
    abstract fun authorDao(): AuthorDao
    abstract fun categoryDao(): CategoryDao
    abstract fun bookmarkDao(): BookmarkDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "urdu_novels_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
