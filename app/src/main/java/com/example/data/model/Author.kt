package com.example.data.model

data class Author(
    val id: String,
    val name: String,
    val bio: String,
    val totalNovelsCount: Int,
    val followersCount: String,
    val avatarColorIndex: Int = 0,
    val nameEnglish: String? = null,
    val portraitUrl: String? = null,
    val createdAt: String = "",
    val updatedAt: String = ""
)
