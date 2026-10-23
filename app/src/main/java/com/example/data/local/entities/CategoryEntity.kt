package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val nameUrdu: String,
    val nameEnglish: String = "",
    val createdAt: String = ""
)
