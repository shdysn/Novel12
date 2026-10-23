package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.DownloadStatus
import com.example.data.local.entities.ChapterEntity
import com.example.data.local.entities.NovelEntity
import com.example.data.network.SupabaseApiService
import com.example.data.repository.NovelRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicReference

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SupabaseSyncTest {

    private lateinit var context: Context
    private lateinit var repository: NovelRepository

    private val fakeResponseHandler = AtomicReference<(okhttp3.Request) -> Response>()

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        repository = NovelRepository(context)

        // Clear existing prefs & set valid test Supabase config
        repository.saveSupabaseConfig(
            url = "https://mockproject.supabase.co",
            key = "mock_anon_key_test_12345"
        )

        // Build OkHttpClient with custom mock interceptor
        val mockInterceptor = Interceptor { chain ->
            val request = chain.request()
            val handler = fakeResponseHandler.get()
            if (handler != null) {
                handler(request)
            } else {
                Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .body("[]".toResponseBody("application/json".toMediaTypeOrNull()))
                    .build()
            }
        }

        val testOkHttpClient = OkHttpClient.Builder()
            .addInterceptor(mockInterceptor)
            .build()

        val mockApiService = SupabaseApiService.createClient("https://mockproject.supabase.co", testOkHttpClient)
        repository.customApiService = mockApiService
    }

    private fun jsonResponse(request: okhttp3.Request, code: Int, json: String): Response {
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message(if (code in 200..299) "OK" else "Error")
            .body(json.toResponseBody("application/json".toMediaTypeOrNull()))
            .build()
    }

    @Test
    fun testSupabaseConnection_success() = runBlocking {
        fakeResponseHandler.set { request ->
            assertTrue(request.url.encodedPath.contains("novels"))
            assertEquals("mock_anon_key_test_12345", request.header("apikey"))
            jsonResponse(request, 200, "[]")
        }

        val (success, message) = repository.testSupabaseConnection()
        assertTrue("Connection should succeed", success)
        assertTrue(message.contains("کامیابی"))
        assertTrue(repository.supabaseConfig.value.isConnected)
    }

    @Test
    fun testSupabaseConnection_authError401() = runBlocking {
        fakeResponseHandler.set { request ->
            jsonResponse(request, 401, """{"message":"Invalid API key"}""")
        }

        val (success, message) = repository.testSupabaseConnection()
        assertFalse("Connection should fail on 401", success)
        assertTrue(message.contains("401"))
        assertFalse(repository.supabaseConfig.value.isConnected)
    }

    @Test
    fun testSupabaseSync_pullsRemoteNovelsAndSavesToRoom() = runBlocking {
        val remoteNovelsJson = """
            [
                {
                    "id": "cloud_novel_aab_e_hayat",
                    "title_urdu": "آبِ حیات",
                    "title_english": "Aab e Hayat",
                    "author": "عمیرہ احمد",
                    "category": "روحانی",
                    "description": "پیرِ کامل کا تسلسل اور ایک عہد ساز روحانی داستان۔",
                    "publication_status": "published",
                    "is_featured": true,
                    "views_count": 52000,
                    "rating": 4.9,
                    "total_chapters": 12,
                    "created_at": "2026-03-01T12:00:00Z",
                    "updated_at": "2026-03-01T12:00:00Z"
                }
            ]
        """.trimIndent()

        val remoteAuthorsJson = """
            [
                {
                    "id": "author_umera",
                    "name_urdu": "عمیرہ احمد",
                    "name_english": "Umera Ahmed",
                    "biography": "پاکستان کی معروف ناول نگار",
                    "created_at": "2026-01-01T00:00:00Z"
                }
            ]
        """.trimIndent()

        val remoteCategoriesJson = """
            [
                {
                    "id": "cat_spiritual",
                    "name_urdu": "روحانی",
                    "name_english": "Spiritual",
                    "created_at": "2026-01-01T00:00:00Z"
                }
            ]
        """.trimIndent()

        fakeResponseHandler.set { request ->
            val path = request.url.encodedPath
            when {
                path.contains("novels") -> jsonResponse(request, 200, remoteNovelsJson)
                path.contains("authors") -> jsonResponse(request, 200, remoteAuthorsJson)
                path.contains("categories") -> jsonResponse(request, 200, remoteCategoriesJson)
                else -> jsonResponse(request, 200, "[]")
            }
        }

        val (success, message) = repository.syncWithSupabase()
        assertTrue("Sync should succeed", success)
        assertTrue(message.contains("سنک ہو گئے"))

        // Verify the synced novel exists in Room / repository
        val novel = repository.getNovelByIdAsync("cloud_novel_aab_e_hayat")
        assertNotNull("Synced novel should exist in local storage", novel)
        assertEquals("آبِ حیات", novel?.title)
        assertEquals("عمیرہ احمد", novel?.author)
        assertEquals(DownloadStatus.NOT_DOWNLOADED, novel?.downloadStatus)
        assertFalse(novel?.isDownloaded ?: true)
    }

    @Test
    fun testSupabaseSync_conflictResolution_preservesLocalReadingProgressAndFavorites() = runBlocking {
        // 1. First add a novel locally with user progress
        val novelId = "novel_conflict_test"
        val initialEntity = NovelEntity(
            id = novelId,
            titleUrdu = "پرانا عنوان",
            authorName = "نمرہ احمد",
            categoryName = "سسپنس",
            description = "پرانی تفصیل",
            readingProgress = 0.82f,
            lastReadChapterId = "ch_chap_4",
            lastReadOffset = 1500,
            isFavorite = true,
            isDownloaded = true,
            downloadStatus = DownloadStatus.AVAILABLE_OFFLINE.name,
            createdAt = "2026-01-01T00:00:00Z",
            updatedAt = "2026-01-01T00:00:00Z"
        )
        val database = com.example.data.local.AppDatabase.getInstance(context)
        database.novelDao().insertNovel(initialEntity)

        // 2. Remote novel has NEWER updatedAt and updated title/description
        val newerRemoteNovelsJson = """
            [
                {
                    "id": "$novelId",
                    "title_urdu": "نیا اپڈیٹ شدہ عنوان",
                    "title_english": "Updated Novel Title",
                    "author": "نمرہ احمد",
                    "category": "سسپنس",
                    "description": "کلاؤڈ سے نئی اور تفصیلی معلومات۔",
                    "publication_status": "published",
                    "created_at": "2026-01-01T00:00:00Z",
                    "updated_at": "2026-10-22T10:00:00Z"
                }
            ]
        """.trimIndent()

        fakeResponseHandler.set { request ->
            val path = request.url.encodedPath
            when {
                path.contains("novels") -> jsonResponse(request, 200, newerRemoteNovelsJson)
                else -> jsonResponse(request, 200, "[]")
            }
        }

        // 3. Perform sync
        val (syncSuccess, _) = repository.syncWithSupabase()
        assertTrue(syncSuccess)

        // 4. Verify conflict resolution rules:
        val updatedLocal = database.novelDao().getNovelById(novelId)
        assertNotNull(updatedLocal)

        // Remote metadata updated:
        assertEquals("نیا اپڈیٹ شدہ عنوان", updatedLocal?.titleUrdu)
        assertEquals("کلاؤڈ سے نئی اور تفصیلی معلومات۔", updatedLocal?.description)

        // Local reading progress & user favorites strictly preserved:
        assertEquals(0.82f, updatedLocal!!.readingProgress, 0.001f)
        assertEquals("ch_chap_4", updatedLocal.lastReadChapterId)
        assertEquals(1500, updatedLocal.lastReadOffset)
        assertTrue("User favorite state must not be overwritten by cloud sync", updatedLocal.isFavorite)

        // Download status indicates update is available because remote has newer content
        assertEquals(DownloadStatus.UPDATE_AVAILABLE.name, updatedLocal.downloadStatus)
    }

    @Test
    fun testSupabasePushNovel_payloadAndHeaders() = runBlocking {
        val novelId = "novel_push_test"
        val database = com.example.data.local.AppDatabase.getInstance(context)
        database.novelDao().insertNovel(
            NovelEntity(
                id = novelId,
                titleUrdu = "شاہین کی پرواز",
                authorName = "نسیم حجازی",
                categoryName = "تاریخی",
                description = "ایک ولولہ انگیز تاریخی ناول"
            )
        )
        database.chapterDao().insertChapter(
            ChapterEntity(
                id = "ch_push_1",
                novelId = novelId,
                chapterNumber = 1,
                chapterTitle = "پہلا باب",
                content = "قرطبہ کی فضاؤں میں شام کے سائے گہرے ہو رہے تھے۔"
            )
        )

        val capturedRequests = CopyOnWriteArrayList<okhttp3.Request>()

        val validNovelResponseJson = """
            [
                {
                    "id": "$novelId",
                    "title_urdu": "شاہین کی پرواز",
                    "author": "نسیم حجازی",
                    "publication_status": "published"
                }
            ]
        """.trimIndent()

        val validChapterResponseJson = """
            [
                {
                    "id": "ch_push_1",
                    "novel_id": "$novelId",
                    "chapter_number": 1,
                    "chapter_title": "پہلا باب",
                    "content": "قرطبہ کی فضاؤں میں شام کے سائے گہرے ہو رہے تھے۔"
                }
            ]
        """.trimIndent()

        fakeResponseHandler.set { request ->
            capturedRequests.add(request)
            val path = request.url.encodedPath
            when {
                path.contains("novels") -> jsonResponse(request, 201, validNovelResponseJson)
                path.contains("chapters") -> jsonResponse(request, 201, validChapterResponseJson)
                else -> jsonResponse(request, 200, "[]")
            }
        }

        val (pushSuccess, pushMsg) = repository.pushNovelToSupabase(novelId)
        assertTrue("Push should succeed: $pushMsg", pushSuccess)
        assertTrue("Message should contain success text: $pushMsg", pushMsg.contains("پش ہو گئے"))
        assertTrue("Captured requests should not be empty", capturedRequests.isNotEmpty())
        assertTrue(
            "POST /novels should have been invoked",
            capturedRequests.any { it.url.encodedPath.contains("novels") && it.method == "POST" }
        )
    }

    @Test
    fun testSupabaseDownloadNovel_offlineStorageAndRetrieval() = runBlocking {
        val novelId = "novel_download_test"
        val database = com.example.data.local.AppDatabase.getInstance(context)
        database.novelDao().insertNovel(
            NovelEntity(
                id = novelId,
                titleUrdu = "آنگن",
                authorName = "خدیجہ مستور",
                categoryName = "معاشرتی",
                description = "تقسیمِ ہند کے پس منظر میں عورتوں کے آنگن کی کہانی",
                downloadStatus = DownloadStatus.NOT_DOWNLOADED.name,
                isDownloaded = false
            )
        )

        val remoteChaptersJson = """
            [
                {
                    "id": "ch_aangan_1",
                    "novel_id": "$novelId",
                    "chapter_number": 1,
                    "chapter_title": "باب اول: آنگن کی اداسی",
                    "content": "عالیہ نے دالان میں بیٹھی اماں کو دیکھا جن کی نگاہیں مٹکے کے پاس بیٹھی چھمی پر جمی ہوئی تھیں۔",
                    "word_count": 350,
                    "read_time_minutes": 3,
                    "publication_status": "published"
                }
            ]
        """.trimIndent()

        fakeResponseHandler.set { request ->
            val path = request.url.encodedPath
            if (path.contains("chapters")) {
                jsonResponse(request, 200, remoteChaptersJson)
            } else {
                jsonResponse(request, 200, "[]")
            }
        }

        val (downloadSuccess, downloadMsg) = repository.downloadNovel(novelId)
        assertTrue("Download should succeed: $downloadMsg", downloadSuccess)
        assertTrue(downloadMsg.contains("محفوظ ہو گئے"))

        // Verify status in DB is now AVAILABLE_OFFLINE
        val downloadedNovel = database.novelDao().getNovelById(novelId)
        assertEquals(DownloadStatus.AVAILABLE_OFFLINE.name, downloadedNovel?.downloadStatus)
        assertTrue(downloadedNovel?.isDownloaded ?: false)

        // Verify chapters are stored offline in Room
        val offlineChapters = database.chapterDao().getChaptersForNovel(novelId)
        assertEquals(1, offlineChapters.size)
        assertEquals("باب اول: آنگن کی اداسی", offlineChapters.first().chapterTitle)
        assertTrue(offlineChapters.first().content.contains("عالیہ نے دالان میں بیٹھی اماں کو دیکھا"))

        // Simulate complete offline mode: Set handler to fail if any network is attempted
        fakeResponseHandler.set { request ->
            throw java.io.IOException("No internet connection (offline test)")
        }

        // Reading chapters when offline should succeed directly from Room
        val loadedOfflineChapters = repository.getChaptersAsync(novelId)
        assertEquals(1, loadedOfflineChapters.size)
        assertEquals("باب اول: آنگن کی اداسی", loadedOfflineChapters.first().title)
        assertTrue(loadedOfflineChapters.first().content.contains("عالیہ نے دالان میں بیٹھی اماں کو دیکھا"))
    }
}
