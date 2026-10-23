package com.example.data.network

import com.example.data.supabase.AuthorDto
import com.example.data.supabase.CategoryDto
import com.example.data.supabase.ChapterDto
import com.example.data.supabase.NovelDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface SupabaseApiService {

    // Novels
    @GET("novels?select=*&order=created_at.desc")
    suspend fun getNovels(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String
    ): Response<List<NovelDto>>

    @GET("novels?select=*&publication_status=eq.published&order=created_at.desc")
    suspend fun getPublishedNovels(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String
    ): Response<List<NovelDto>>

    @GET("novels?select=*")
    suspend fun getNovelById(
        @Query("id") idFilter: String, // e.g. "eq.xxxx"
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String
    ): Response<List<NovelDto>>

    @Headers("Prefer: return=representation")
    @POST("novels")
    suspend fun pushNovel(
        @Body novel: NovelDto,
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String
    ): Response<List<NovelDto>>

    @Headers("Prefer: return=representation")
    @PATCH("novels")
    suspend fun updateNovel(
        @Query("id") idFilter: String, // e.g. "eq.xxxx"
        @Body novel: Map<String, Any?>,
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String
    ): Response<List<NovelDto>>

    @DELETE("novels")
    suspend fun deleteNovel(
        @Query("id") idFilter: String, // e.g. "eq.xxxx"
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String
    ): Response<ResponseBody>

    // Chapters
    @GET("chapters?select=*")
    suspend fun getChapters(
        @Query("novel_id") novelIdFilter: String, // e.g. "eq.xxxx"
        @Query("order") order: String = "chapter_number.asc",
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String
    ): Response<List<ChapterDto>>

    @Headers("Prefer: return=representation")
    @POST("chapters")
    suspend fun pushChapters(
        @Body chapters: List<ChapterDto>,
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String
    ): Response<List<ChapterDto>>

    @Headers("Prefer: return=representation")
    @POST("chapters")
    suspend fun pushChapter(
        @Body chapter: ChapterDto,
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String
    ): Response<List<ChapterDto>>

    @Headers("Prefer: return=representation")
    @PATCH("chapters")
    suspend fun updateChapter(
        @Query("id") idFilter: String,
        @Body fields: Map<String, Any?>,
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String
    ): Response<List<ChapterDto>>

    @DELETE("chapters")
    suspend fun deleteChapter(
        @Query("id") idFilter: String,
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String
    ): Response<ResponseBody>

    // Authors
    @GET("authors?select=*&order=name_urdu.asc")
    suspend fun getAuthors(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String
    ): Response<List<AuthorDto>>

    @Headers("Prefer: return=representation")
    @POST("authors")
    suspend fun pushAuthor(
        @Body author: AuthorDto,
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String
    ): Response<List<AuthorDto>>

    @DELETE("authors")
    suspend fun deleteAuthor(
        @Query("id") idFilter: String,
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String
    ): Response<ResponseBody>

    // Categories
    @GET("categories?select=*&order=name_urdu.asc")
    suspend fun getCategories(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String
    ): Response<List<CategoryDto>>

    @Headers("Prefer: return=representation")
    @POST("categories")
    suspend fun pushCategory(
        @Body category: CategoryDto,
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String
    ): Response<List<CategoryDto>>

    // Storage: Upload Cover Image
    @POST("../storage/v1/object/novel-covers/{filename}")
    suspend fun uploadCoverImage(
        @Path("filename") filename: String,
        @Body imageBytes: RequestBody,
        @Header("Content-Type") contentType: String,
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String
    ): Response<ResponseBody>

    companion object {
        fun createClient(baseUrl: String, customOkHttpClient: OkHttpClient? = null): SupabaseApiService? {
            return try {
                var cleanUrl = baseUrl.trim()
                if (!cleanUrl.endsWith("/")) cleanUrl += "/"
                if (!cleanUrl.contains("/rest/v1/")) {
                    cleanUrl += "rest/v1/"
                }

                val client = customOkHttpClient ?: run {
                    val logging = HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BASIC
                    }
                    OkHttpClient.Builder()
                        .addInterceptor(logging)
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(20, TimeUnit.SECONDS)
                        .writeTimeout(20, TimeUnit.SECONDS)
                        .build()
                }

                Retrofit.Builder()
                    .baseUrl(cleanUrl)
                    .client(client)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()
                    .create(SupabaseApiService::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
}
