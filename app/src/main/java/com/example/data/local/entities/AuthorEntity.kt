package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Author

@Entity(tableName = "authors")
data class AuthorEntity(
    @PrimaryKey val id: String,
    val nameUrdu: String,
    val nameEnglish: String = "",
    val biography: String = "",
    val totalNovelsCount: Int = 1,
    val followersCount: String = "10k+",
    val avatarColorIndex: Int = 0,
    val portraitUrl: String? = null,
    val createdAt: String = "",
    val updatedAt: String = ""
) {
    fun toDomainModel(): Author {
        return Author(
            id = id,
            name = nameUrdu,
            bio = biography,
            totalNovelsCount = totalNovelsCount,
            followersCount = followersCount,
            avatarColorIndex = avatarColorIndex,
            nameEnglish = nameEnglish.ifBlank { null },
            portraitUrl = portraitUrl,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomainModel(author: Author): AuthorEntity {
            return AuthorEntity(
                id = author.id,
                nameUrdu = author.name,
                nameEnglish = author.nameEnglish ?: "",
                biography = author.bio,
                totalNovelsCount = author.totalNovelsCount,
                followersCount = author.followersCount,
                avatarColorIndex = author.avatarColorIndex,
                portraitUrl = author.portraitUrl,
                createdAt = author.createdAt,
                updatedAt = author.updatedAt
            )
        }
    }
}
