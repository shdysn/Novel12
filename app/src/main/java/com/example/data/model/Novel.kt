package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Novel(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "author") val author: String,
    @Json(name = "category") val category: String,
    @Json(name = "synopsis") val synopsis: String,
    @Json(name = "rating") val rating: Float = 4.8f,
    @Json(name = "views_count") val viewsCount: Int = 1200,
    @Json(name = "cover_gradient_index") val coverGradientIndex: Int = 0,
    @Json(name = "total_chapters") val totalChapters: Int = 1,
    @Json(name = "is_favorite") val isFavorite: Boolean = false,
    @Json(name = "is_downloaded") val isDownloaded: Boolean = true,
    @Json(name = "is_trending") val isTrending: Boolean = false,
    @Json(name = "is_new") val isNew: Boolean = false,
    @Json(name = "is_recommended") val isRecommended: Boolean = false,
    @Json(name = "reading_progress") val readingProgress: Float = 0f, // 0.0 to 1.0
    @Json(name = "language") val language: String = "اردو (Urdu)",
    @Json(name = "created_at") val createdAt: String = "",
    @Json(name = "updated_at") val updatedAt: String = "",
    @Json(name = "is_custom_published") val isCustomPublished: Boolean = false,
    @Json(name = "publication_status") val publicationStatus: String = "published", // "published" or "draft"
    @Json(name = "cover_image_url") val coverImageUrl: String? = null,
    @Json(name = "author_id") val authorId: String? = null,
    @Json(name = "category_id") val categoryId: String? = null,
    val downloadStatus: com.example.data.local.DownloadStatus = com.example.data.local.DownloadStatus.AVAILABLE_OFFLINE
) {
    val isDraft: Boolean get() = publicationStatus.equals("draft", ignoreCase = true)
    val isPublished: Boolean get() = publicationStatus.equals("published", ignoreCase = true)
}

enum class NovelCategory(val urduName: String, val englishName: String, val iconDescription: String) {
    ALL("تمام", "All", "تمام ناولز"),
    MOHABBAT("محبت", "Love", "محبت اور الفت"),
    ROMANCE("رومانوی", "Romance", "رومانوی داستانیں"),
    SUSPENSE("سسپنس", "Suspense", "سسپنس اور تجسس"),
    SPY("جاسوسی", "Detective", "پراسرار و جاسوسی"),
    HISTORICAL("تاریخی", "Historical", "تاریخی پس منظر"),
    ISLAHI("اصلاحی", "Reformist", "اخلاقی و اصلاحی"),
    SOCIAL("معاشرتی", "Social", "خاندانی و معاشرتی"),
    SPIRITUAL("روحانی", "Spiritual", "تصوف اور معرفت"),
    CLASSIC("کلاسیک", "Classic", "کلاسیک ادب");

    companion object {
        fun fromUrdu(urdu: String): NovelCategory {
            return entries.firstOrNull { it.urduName == urdu || urdu.contains(it.urduName) } ?: ALL
        }
    }
}
