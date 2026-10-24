package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.local.AppDatabase
import com.example.data.local.DownloadStatus
import com.example.data.local.SampleUrduNovels
import com.example.data.local.entities.AuthorEntity
import com.example.data.local.entities.BookmarkEntity
import com.example.data.local.entities.CategoryEntity
import com.example.data.local.entities.ChapterEntity
import com.example.data.local.entities.NovelEntity
import com.example.data.model.Author
import com.example.data.model.Bookmark
import com.example.data.model.Chapter
import com.example.data.model.FontStyleType
import com.example.data.model.Novel
import com.example.data.model.NovelCategory
import com.example.data.model.ReaderSettings
import com.example.data.model.ReaderTheme
import com.example.data.model.SupabaseConfig
import com.example.data.model.TextAlignmentType
import com.example.data.network.SupabaseApiService
import com.example.data.supabase.AuthorDto
import com.example.data.supabase.CategoryDto
import com.example.data.supabase.ChapterDto
import com.example.data.supabase.NovelDto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class NovelRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("urdu_novel_prefs", Context.MODE_PRIVATE)

    private val database = AppDatabase.getInstance(context)
    private val novelDao = database.novelDao()
    private val chapterDao = database.chapterDao()
    private val authorDao = database.authorDao()
    private val categoryDao = database.categoryDao()
    private val bookmarkDao = database.bookmarkDao()

    private val repositoryScope = CoroutineScope(Dispatchers.IO + Job())

    // Custom API Service for tests or custom client injection
    @Volatile
    var customApiService: SupabaseApiService? = null

    private fun getApiService(config: SupabaseConfig): SupabaseApiService? {
        return customApiService ?: SupabaseApiService.createClient(config.url)
    }

    // In-memory active download jobs for cancellation
    private val activeDownloadJobs = ConcurrentHashMap<String, Job>()

    // In-memory custom chapters
    private val inMemoryCustomChapters = ConcurrentHashMap<String, MutableList<Chapter>>()

    // Download status per novel ID
    private val _downloadStatuses = MutableStateFlow<Map<String, DownloadStatus>>(emptyMap())
    val downloadStatuses: StateFlow<Map<String, DownloadStatus>> = _downloadStatuses.asStateFlow()

    // Novels Flow from Room (Offline-First)
    val novels: StateFlow<List<Novel>> = MutableStateFlow<List<Novel>>(emptyList())
    private val _novels = novels as MutableStateFlow<List<Novel>>

    // Authors Flow
    val authors: StateFlow<List<Author>> = MutableStateFlow<List<Author>>(SampleUrduNovels.sampleAuthors)
    private val _authors = authors as MutableStateFlow<List<Author>>

    // Bookmarks Flow
    val bookmarks: StateFlow<List<Bookmark>> = MutableStateFlow<List<Bookmark>>(emptyList())
    private val _bookmarks = bookmarks as MutableStateFlow<List<Bookmark>>

    private val _recentSearches = MutableStateFlow<List<String>>(
        listOf("پیرِ کامل", "عمیرہ احمد", "جنت کے پتے", "جاسوسی ناولز", "عمران سیریز", "محبت")
    )
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    val popularSearches: List<String> = listOf(
        "پیرِ کامل", "راجہ گدھ", "نمل", "جنت کے پتے", "خدا اور محبت", "ابنِ صفی", "بانو قدسیہ", "رومانوی", "سسپنس"
    )

    private val _readerSettings = MutableStateFlow(ReaderSettings())
    val readerSettings: StateFlow<ReaderSettings> = _readerSettings.asStateFlow()

    private val _supabaseConfig = MutableStateFlow(SupabaseConfig())
    val supabaseConfig: StateFlow<SupabaseConfig> = _supabaseConfig.asStateFlow()

    private val _syncingState = MutableStateFlow(false)
    val syncingState: StateFlow<Boolean> = _syncingState.asStateFlow()

    // Profile settings
    val notificationsEnabled = MutableStateFlow(prefs.getBoolean("pref_notif", true))
    val wifiOnlyDownload = MutableStateFlow(prefs.getBoolean("pref_wifi", true))
    val userName = MutableStateFlow(prefs.getString("user_name", "شاہد ثاقب") ?: "شاہد ثاقب")
    val userTitle = MutableStateFlow(prefs.getString("user_title", "عاشقِ اردو ادب") ?: "عاشقِ اردو ادب")
    val userEmail = MutableStateFlow(prefs.getString("user_email", "shahidsaaqib@gmail.com") ?: "shahidsaaqib@gmail.com")
    val dailyGoalMinutes = MutableStateFlow(prefs.getInt("daily_goal_minutes", 30))
    val isVipMember = MutableStateFlow(prefs.getBoolean("is_vip_member", true))

    fun updateProfile(name: String, title: String, email: String) {
        userName.value = name.trim().ifEmpty { "شاہد ثاقب" }
        userTitle.value = title.trim().ifEmpty { "عاشقِ اردو ادب" }
        userEmail.value = email.trim().ifEmpty { "shahidsaaqib@gmail.com" }
        prefs.edit()
            .putString("user_name", userName.value)
            .putString("user_title", userTitle.value)
            .putString("user_email", userEmail.value)
            .apply()
    }

    fun setDailyGoalMinutes(minutes: Int) {
        val clamped = minutes.coerceIn(10, 180)
        dailyGoalMinutes.value = clamped
        prefs.edit().putInt("daily_goal_minutes", clamped).apply()
    }

    fun toggleNotifications() {
        val newVal = !notificationsEnabled.value
        notificationsEnabled.value = newVal
        prefs.edit().putBoolean("pref_notif", newVal).apply()
    }

    init {
        loadSettings()
        loadSupabaseConfig()
        loadRecentSearches()

        // Initialize Room Database and seed data asynchronously
        repositoryScope.launch {
            seedDatabaseIfEmpty()
            observeRoomDatabase()
        }
    }

    private fun getDeletedNovelIds(): Set<String> {
        return prefs.getStringSet("deleted_novel_ids", emptySet()) ?: emptySet()
    }

    private fun markNovelAsDeleted(novelId: String) {
        val current = getDeletedNovelIds().toMutableSet()
        current.add(novelId)
        prefs.edit().putStringSet("deleted_novel_ids", current).apply()
    }

    private fun unmarkNovelAsDeleted(novelId: String) {
        val current = getDeletedNovelIds().toMutableSet()
        current.remove(novelId)
        prefs.edit().putStringSet("deleted_novel_ids", current).apply()
    }

    private suspend fun seedDatabaseIfEmpty() {
        val hasSeeded = prefs.getBoolean("has_seeded_database", false)
        if (hasSeeded) return

        val existingNovels = novelDao.getAllNovelsFlow().first()
        if (existingNovels.isEmpty()) {
            val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date())

            // Seed sample authors
            val authorEntities = SampleUrduNovels.sampleAuthors.map { AuthorEntity.fromDomainModel(it) }
            authorDao.insertAuthors(authorEntities)

            // Seed sample categories
            val categories = NovelCategory.entries.map {
                CategoryEntity(id = it.name.lowercase(), nameUrdu = it.urduName, nameEnglish = it.englishName, createdAt = now)
            }
            categoryDao.insertCategories(categories)

            // Seed sample novels
            val novelEntities = SampleUrduNovels.novelsList.map { novel ->
                NovelEntity.fromDomainModel(novel, DownloadStatus.AVAILABLE_OFFLINE).copy(
                    createdAt = now,
                    updatedAt = now,
                    isDownloaded = true
                )
            }
            novelDao.insertNovels(novelEntities)

            // Seed sample chapters
            val chapterEntities = mutableListOf<ChapterEntity>()
            SampleUrduNovels.chaptersMap.forEach { (novelId, chapList) ->
                chapList.forEach { ch ->
                    chapterEntities.add(
                        ChapterEntity.fromDomainModel(ch, DownloadStatus.AVAILABLE_OFFLINE).copy(
                            createdAt = now,
                            updatedAt = now,
                            isDownloaded = true
                        )
                    )
                }
            }
            chapterDao.insertChapters(chapterEntities)
            prefs.edit().putBoolean("has_seeded_database", true).apply()
        }
    }

    // Cached chapters from Room for fast synchronous lookups in Composables
    private val cachedRoomChaptersMap = ConcurrentHashMap<String, List<Chapter>>()

    private fun observeRoomDatabase() {
        repositoryScope.launch {
            novelDao.getAllNovelsFlow().collect { entities ->
                val deletedIds = getDeletedNovelIds()
                val activeEntities = entities.filterNot { deletedIds.contains(it.id) }
                _novels.value = activeEntities.map { it.toDomainModel() }
                // Update download status map
                val statusMap = activeEntities.associate { it.id to DownloadStatus.fromString(it.downloadStatus) }
                _downloadStatuses.value = statusMap
            }
        }

        repositoryScope.launch {
            chapterDao.getAllChaptersFlow().collect { entities ->
                val deletedIds = getDeletedNovelIds()
                val grouped = entities
                    .filterNot { deletedIds.contains(it.novelId) }
                    .map { it.toDomainModel() }
                    .groupBy { it.novelId }
                cachedRoomChaptersMap.clear()
                cachedRoomChaptersMap.putAll(grouped)
            }
        }

        repositoryScope.launch {
            authorDao.getAllAuthorsFlow().collect { entities ->
                if (entities.isNotEmpty()) {
                    _authors.value = entities.map { it.toDomainModel() }
                }
            }
        }

        repositoryScope.launch {
            bookmarkDao.getAllBookmarksFlow().collect { entities ->
                val deletedIds = getDeletedNovelIds()
                _bookmarks.value = entities.map { it.toDomainModel() }.filterNot { deletedIds.contains(it.novelId) }
            }
        }
    }

    private fun loadSettings() {
        val fontSize = prefs.getFloat("font_size", 22f)
        val lineSpacing = prefs.getFloat("line_spacing", 1.2f)
        val paragraphSpacing = prefs.getInt("paragraph_spacing", 4)
        val horizMargin = prefs.getInt("horiz_margin", 12)
        val vertMargin = prefs.getInt("vert_margin", 8)
        val savedAlignment = prefs.getString("text_alignment", null)
        val alignment = if (savedAlignment == null || savedAlignment == "JUSTIFIED") {
            TextAlignmentType.RIGHT_ALIGNED
        } else {
            try { TextAlignmentType.valueOf(savedAlignment) } catch (e: Exception) { TextAlignmentType.RIGHT_ALIGNED }
        }
        val savedTheme = prefs.getString("reader_theme", null)
        val theme = if (savedTheme == null || savedTheme == "WARM_PAPER") {
            ReaderTheme.PURE_WHITE
        } else {
            try { ReaderTheme.valueOf(savedTheme) } catch (e: Exception) { ReaderTheme.PURE_WHITE }
        }
        val fontStyleName = prefs.getString("font_style", FontStyleType.PDMS_JAUHAR.name) ?: FontStyleType.PDMS_JAUHAR.name
        val fontStyle = try { FontStyleType.valueOf(fontStyleName) } catch (e: Exception) { FontStyleType.PDMS_JAUHAR }
        val brightness = prefs.getFloat("screen_brightness", 0.85f)
        val showBookFrame = prefs.getBoolean("show_book_frame", false)
        val useUrduNumerals = prefs.getBoolean("use_urdu_numerals", true)

        _readerSettings.value = ReaderSettings(
            fontSizeSp = fontSize,
            lineHeightMultiplier = lineSpacing,
            paragraphSpacingDp = paragraphSpacing,
            horizontalMarginDp = horizMargin,
            verticalMarginDp = vertMargin,
            alignment = alignment,
            theme = theme,
            fontStyle = fontStyle,
            brightness = brightness,
            showBookFrame = showBookFrame,
            useUrduNumerals = useUrduNumerals
        )
    }

    fun updateReaderSettings(settings: ReaderSettings) {
        _readerSettings.value = settings
        prefs.edit()
            .putFloat("font_size", settings.fontSizeSp)
            .putFloat("line_spacing", settings.lineHeightMultiplier)
            .putInt("paragraph_spacing", settings.paragraphSpacingDp)
            .putInt("horiz_margin", settings.horizontalMarginDp)
            .putInt("vert_margin", settings.verticalMarginDp)
            .putString("text_alignment", settings.alignment.name)
            .putString("reader_theme", settings.theme.name)
            .putString("font_style", settings.fontStyle.name)
            .putFloat("screen_brightness", settings.brightness)
            .putBoolean("show_book_frame", settings.showBookFrame)
            .putBoolean("use_urdu_numerals", settings.useUrduNumerals)
            .apply()
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        notificationsEnabled.value = enabled
        prefs.edit().putBoolean("pref_notif", enabled).apply()
    }

    fun setWifiOnlyDownload(enabled: Boolean) {
        wifiOnlyDownload.value = enabled
        prefs.edit().putBoolean("pref_wifi", enabled).apply()
    }

    private fun loadSupabaseConfig() {
        val url = prefs.getString("sb_url", "") ?: ""
        val key = prefs.getString("sb_key", "") ?: ""
        val isConnected = prefs.getBoolean("sb_connected", false)
        val lastMsg = prefs.getString("sb_msg", "کنکشن کا انتظار ہے") ?: "کنکشن کا انتظار ہے"
        val totalSynced = prefs.getInt("sb_synced_count", 0)

        _supabaseConfig.value = SupabaseConfig(
            url = url,
            anonKey = key,
            isConnected = isConnected,
            lastSyncMessage = lastMsg,
            totalSyncedNovels = totalSynced
        )
    }

    fun saveSupabaseConfig(url: String, key: String) {
        val cleanUrl = url.trim()
        val cleanKey = key.trim()
        _supabaseConfig.value = _supabaseConfig.value.copy(
            url = cleanUrl,
            anonKey = cleanKey,
            lastSyncMessage = "ترتیبات محفوظ ہو گئیں"
        )
        prefs.edit()
            .putString("sb_url", cleanUrl)
            .putString("sb_key", cleanKey)
            .putString("sb_msg", "ترتیبات محفوظ ہو گئیں")
            .apply()
    }

    fun getNovelById(id: String): Novel? {
        if (getDeletedNovelIds().contains(id)) return null
        return _novels.value.firstOrNull { it.id == id }
            ?: SampleUrduNovels.novelsList.firstOrNull { it.id == id && !getDeletedNovelIds().contains(id) }
    }

    suspend fun getNovelByIdAsync(id: String): Novel? = withContext(Dispatchers.IO) {
        if (getDeletedNovelIds().contains(id)) return@withContext null
        _novels.value.firstOrNull { it.id == id }
            ?: novelDao.getNovelById(id)?.takeIf { !getDeletedNovelIds().contains(it.id) }?.toDomainModel()
            ?: SampleUrduNovels.novelsList.firstOrNull { it.id == id && !getDeletedNovelIds().contains(id) }
    }

    fun getChapters(novelId: String): List<Chapter> {
        if (getDeletedNovelIds().contains(novelId)) return emptyList()
        val novel = getNovelById(novelId) ?: return emptyList()

        val roomChaps = cachedRoomChaptersMap[novelId]?.filter { it.content.isNotBlank() }
        if (!roomChaps.isNullOrEmpty()) {
            return roomChaps.sortedBy { it.chapterNumber }
        }

        val custom = inMemoryCustomChapters[novelId] ?: emptyList()
        if (custom.isNotEmpty()) {
            return custom.sortedBy { it.chapterNumber }
        }

        // Only fall back to sample chapters if novel is active and marked as downloaded
        if (novel.isDownloaded) {
            val sample = SampleUrduNovels.chaptersMap[novelId] ?: emptyList()
            return sample.sortedBy { it.chapterNumber }
        }

        return emptyList()
    }

    suspend fun getChaptersAsync(novelId: String): List<Chapter> = withContext(Dispatchers.IO) {
        if (getDeletedNovelIds().contains(novelId)) return@withContext emptyList()
        val novel = getNovelByIdAsync(novelId) ?: return@withContext emptyList()

        val entities = chapterDao.getChaptersForNovel(novelId).filter { it.content.isNotBlank() }
        if (entities.isNotEmpty()) {
            return@withContext entities.map { it.toDomainModel() }
        }

        val custom = inMemoryCustomChapters[novelId] ?: emptyList()
        if (custom.isNotEmpty()) {
            return@withContext custom.sortedBy { it.chapterNumber }
        }

        if (novel.isDownloaded) {
            return@withContext SampleUrduNovels.chaptersMap[novelId] ?: emptyList()
        }

        return@withContext emptyList()
    }

    fun getChapter(novelId: String, chapterId: String): Chapter? {
        return getChapters(novelId).firstOrNull { it.id == chapterId }
    }

    suspend fun getChapterAsync(novelId: String, chapterId: String): Chapter? = withContext(Dispatchers.IO) {
        val entity = chapterDao.getChapterById(chapterId)
        if (entity != null) return@withContext entity.toDomainModel()
        getChapters(novelId).firstOrNull { it.id == chapterId }
    }

    fun toggleFavorite(novelId: String) {
        repositoryScope.launch {
            val novel = getNovelById(novelId) ?: return@launch
            val newFav = !novel.isFavorite
            novelDao.updateFavorite(novelId, newFav)
            _novels.value = _novels.value.map {
                if (it.id == novelId) it.copy(isFavorite = newFav) else it
            }
        }
    }

    fun updateReadingProgress(novelId: String, progress: Float) {
        repositoryScope.launch {
            val (lastChapId, lastOffset, _) = getLastReadingLocation(novelId)
            novelDao.updateReadingProgress(novelId, progress, lastChapId, lastOffset)
            _novels.value = _novels.value.map {
                if (it.id == novelId) it.copy(readingProgress = progress) else it
            }
        }
    }

    fun saveReadingLocation(novelId: String, chapterId: String, charOffset: Int, progressPercent: Float) {
        prefs.edit()
            .putString("last_chap_$novelId", chapterId)
            .putInt("last_offset_$novelId", charOffset)
            .putFloat("last_prog_$novelId", progressPercent)
            .apply()

        repositoryScope.launch {
            novelDao.updateReadingProgress(novelId, progressPercent, chapterId, charOffset)
            _novels.value = _novels.value.map {
                if (it.id == novelId) it.copy(readingProgress = progressPercent) else it
            }
        }
    }

    fun getLastReadingLocation(novelId: String): Triple<String?, Int, Float> {
        val chapId = prefs.getString("last_chap_$novelId", null)
        val offset = prefs.getInt("last_offset_$novelId", 0)
        val progress = prefs.getFloat("last_prog_$novelId", 0f)
        return Triple(chapId, offset, progress)
    }

    // Bookmark management via Room + SharedPreferences
    fun addBookmark(
        novel: Novel,
        chapter: Chapter,
        charOffset: Int,
        pageNumber: Int,
        snippet: String,
        note: String = "",
        progressPercent: Float
    ): Bookmark {
        val newBookmark = Bookmark(
            id = UUID.randomUUID().toString(),
            novelId = novel.id,
            novelTitle = novel.title,
            author = novel.author,
            chapterId = chapter.id,
            chapterNumber = chapter.chapterNumber,
            chapterTitle = chapter.title,
            charOffset = charOffset,
            pageNumber = pageNumber,
            snippet = snippet.take(120),
            note = note,
            progressPercent = progressPercent,
            timestamp = System.currentTimeMillis()
        )
        _bookmarks.value = listOf(newBookmark) + _bookmarks.value
        saveReadingLocation(novel.id, chapter.id, charOffset, progressPercent)

        repositoryScope.launch {
            bookmarkDao.insertBookmark(BookmarkEntity.fromDomainModel(newBookmark))
        }
        return newBookmark
    }

    fun removeBookmarkById(bookmarkId: String) {
        _bookmarks.value = _bookmarks.value.filterNot { it.id == bookmarkId }
        repositoryScope.launch {
            bookmarkDao.deleteBookmarkById(bookmarkId)
        }
    }

    fun removeBookmark(novelId: String) {
        _bookmarks.value = _bookmarks.value.filterNot { it.novelId == novelId }
        repositoryScope.launch {
            bookmarkDao.deleteBookmarksForNovel(novelId)
        }
    }

    // Searches management
    fun addRecentSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return
        val updated = (listOf(trimmed) + _recentSearches.value.filterNot { it.equals(trimmed, ignoreCase = true) }).take(10)
        _recentSearches.value = updated
        prefs.edit().putString("recent_searches", updated.joinToString(",")).apply()
    }

    fun clearRecentSearches() {
        _recentSearches.value = emptyList()
        prefs.edit().remove("recent_searches").apply()
    }

    private fun loadRecentSearches() {
        val saved = prefs.getString("recent_searches", "") ?: ""
        if (saved.isNotBlank()) {
            _recentSearches.value = saved.split(",").map { it.trim() }.filter { it.isNotBlank() }
        }
    }

    fun clearReadingHistory() {
        repositoryScope.launch {
            _novels.value.forEach { novel ->
                novelDao.updateReadingProgress(novel.id, 0f, null, 0)
                prefs.edit()
                    .remove("last_chap_${novel.id}")
                    .remove("last_offset_${novel.id}")
                    .remove("last_prog_${novel.id}")
                    .apply()
            }
            _novels.value = _novels.value.map { it.copy(readingProgress = 0f) }
            bookmarkDao.clearAllBookmarks()
            _bookmarks.value = emptyList()
        }
    }

    // -------------------------------------------------------------
    // OFFLINE READING & DOWNLOAD MANAGEMENT
    // -------------------------------------------------------------

    suspend fun downloadNovel(novelId: String): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        unmarkNovelAsDeleted(novelId)
        val novel = getNovelByIdAsync(novelId) ?: return@withContext Pair(false, "ناول نہیں ملا")

        // Set status to DOWNLOADING
        updateNovelDownloadStatus(novelId, DownloadStatus.DOWNLOADING)

        val job = launch {
            try {
                // If cloud is connected, try to fetch fresh chapters from Supabase
                val config = _supabaseConfig.value
                var fetchedChapters: List<Chapter> = emptyList()

                if (config.isConfigured) {
                    val client = getApiService(config)
                    if (client != null) {
                        val response = client.getChapters("eq.$novelId", "chapter_number.asc", config.anonKey, "Bearer ${config.anonKey}")
                        if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                            fetchedChapters = response.body()!!.map { dto ->
                                Chapter(
                                    id = dto.id,
                                    novelId = dto.novelId,
                                    chapterNumber = dto.chapterNumber,
                                    title = dto.displayTitle,
                                    content = dto.content,
                                    wordCount = dto.wordCount,
                                    readTimeMinutes = dto.readTimeMinutes,
                                    publicationStatus = dto.publicationStatus,
                                    createdAt = dto.createdAt ?: "",
                                    updatedAt = dto.updatedAt ?: "",
                                    downloadStatus = DownloadStatus.AVAILABLE_OFFLINE
                                )
                            }
                        }
                    }
                }

                // If not from cloud, use local chapters from Room (if valid content) or Sample list
                if (fetchedChapters.isEmpty()) {
                    val existingValid = chapterDao.getChaptersForNovel(novelId).filter { it.content.isNotBlank() }
                    if (existingValid.isNotEmpty()) {
                        fetchedChapters = existingValid.map { it.toDomainModel() }
                    } else {
                        fetchedChapters = SampleUrduNovels.chaptersMap[novelId] ?: emptyList()
                    }
                }

                // Simulate realistic progressive download if network is simulated
                delay(600)

                // Persist chapters to Room with AVAILABLE_OFFLINE
                val chapterEntities = fetchedChapters.map { ch ->
                    ChapterEntity.fromDomainModel(ch, DownloadStatus.AVAILABLE_OFFLINE)
                }
                chapterDao.insertChapters(chapterEntities)

                // Update novel entity in Room
                updateNovelDownloadStatus(novelId, DownloadStatus.AVAILABLE_OFFLINE)

            } catch (e: CancellationException) {
                // User cancelled the download
                updateNovelDownloadStatus(novelId, DownloadStatus.NOT_DOWNLOADED)
                throw e
            } catch (e: Exception) {
                updateNovelDownloadStatus(novelId, DownloadStatus.FAILED)
            }
        }

        activeDownloadJobs[novelId] = job
        try {
            job.join()
            activeDownloadJobs.remove(novelId)
            Pair(true, "ناول \"${novel.title}\" اور تمام ابواب آف لائن مطالعہ کے لیے محفوظ ہو گئے!")
        } catch (e: CancellationException) {
            activeDownloadJobs.remove(novelId)
            Pair(false, "ڈاؤن لوڈ منسوخ کر دیا گیا")
        } catch (e: Exception) {
            activeDownloadJobs.remove(novelId)
            Pair(false, "ڈاؤن لوڈ میں خرابی: ${e.localizedMessage}")
        }
    }

    fun cancelDownload(novelId: String) {
        val job = activeDownloadJobs[novelId]
        if (job != null && job.isActive) {
            job.cancel()
            activeDownloadJobs.remove(novelId)
            repositoryScope.launch {
                updateNovelDownloadStatus(novelId, DownloadStatus.NOT_DOWNLOADED)
            }
        }
    }

    suspend fun deleteDownloadedNovel(novelId: String): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val novel = getNovelById(novelId) ?: return@withContext Pair(false, "ناول نہیں ملا")
        try {
            // Delete chapters from Room and clear in-memory cache so re-downloading gets fresh full chapter content
            chapterDao.deleteChaptersForNovel(novelId)
            bookmarkDao.deleteBookmarksForNovel(novelId)
            cachedRoomChaptersMap.remove(novelId)
            inMemoryCustomChapters.remove(novelId)

            _bookmarks.value = _bookmarks.value.filterNot { it.novelId == novelId }
            updateNovelDownloadStatus(novelId, DownloadStatus.NOT_DOWNLOADED)

            prefs.edit()
                .remove("last_chap_$novelId")
                .remove("last_offset_$novelId")
                .remove("last_prog_$novelId")
                .apply()

            Pair(true, "ناول \"${novel.title}\" کا آف لائن کیشے اور ڈیٹا کامیابی سے خارج کر دیا گیا")
        } catch (e: Exception) {
            Pair(false, "خارج کرنے میں خرابی: ${e.localizedMessage}")
        }
    }

    private suspend fun updateNovelDownloadStatus(novelId: String, status: DownloadStatus) {
        novelDao.updateDownloadStatus(novelId, status.name, status == DownloadStatus.AVAILABLE_OFFLINE)
        _downloadStatuses.value = _downloadStatuses.value.toMutableMap().apply {
            put(novelId, status)
        }
        _novels.value = _novels.value.map {
            if (it.id == novelId) it.copy(downloadStatus = status, isDownloaded = status == DownloadStatus.AVAILABLE_OFFLINE) else it
        }
    }

    // -------------------------------------------------------------
    // SUPABASE CLOUD SYNCHRONIZATION & PUSH
    // -------------------------------------------------------------

    suspend fun testSupabaseConnection(): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val config = _supabaseConfig.value
        if (!config.isConfigured) {
            return@withContext Pair(false, "براہ کرم Supabase کا درست URL اور Anon Key درج کریں")
        }

        try {
            val client = getApiService(config)
                ?: return@withContext Pair(false, "غلط URL فارمیٹ")

            val response = client.getNovels(config.anonKey, "Bearer ${config.anonKey}")
            if (response.isSuccessful) {
                _supabaseConfig.value = _supabaseConfig.value.copy(
                    isConnected = true,
                    lastSyncMessage = "Supabase سے رابطہ کامیابی سے قائم ہو گیا!",
                    lastSyncTime = System.currentTimeMillis()
                )
                prefs.edit().putBoolean("sb_connected", true).apply()
                Pair(true, "کامیابی! Supabase کلاؤڈ کنکشن فعال ہے")
            } else {
                val code = response.code()
                val errorMsg = when (code) {
                    404, 400 -> "Supabase میں ناولز ٹیبل نہیں ملا (کوڈ: $code)۔ براہ کرم اسکیما اسکرپٹ ڈیش بورڈ میں چلائیں"
                    401, 403 -> "Anon Key غیر معتبر ہے یا RLS پالیسی اجازت نہیں دے رہی (کوڈ: $code)"
                    else -> "رابطہ ناکام رہا (کوڈ: $code)"
                }
                _supabaseConfig.value = _supabaseConfig.value.copy(
                    isConnected = false,
                    lastSyncMessage = errorMsg
                )
                prefs.edit().putBoolean("sb_connected", false).apply()
                Pair(false, errorMsg)
            }
        } catch (e: Exception) {
            val errorMsg = "کنکشن کی خرابی: ${e.localizedMessage ?: "نیٹ ورک کا مسئلہ"}"
            _supabaseConfig.value = _supabaseConfig.value.copy(
                isConnected = false,
                lastSyncMessage = errorMsg
            )
            prefs.edit().putBoolean("sb_connected", false).apply()
            Pair(false, errorMsg)
        }
    }

    suspend fun syncWithSupabase(): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val config = _supabaseConfig.value
        if (!config.isConfigured) {
            return@withContext Pair(false, "Supabase کنفیگر نہیں ہے۔ براہ کرم ترتیبات میں جائیں۔")
        }

        _syncingState.value = true
        try {
            val client = getApiService(config)
                ?: return@withContext Pair(false, "Supabase کلائنٹ تیار نہیں ہو سکا")

            val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date())

            // 1. Fetch remote novels
            val novelsRes = client.getNovels(config.anonKey, "Bearer ${config.anonKey}")
            if (!novelsRes.isSuccessful) {
                return@withContext Pair(false, "کلاؤڈ سے ناولز حاصل نہیں ہو سکے: کوڈ ${novelsRes.code()}")
            }
            val remoteNovels = novelsRes.body() ?: emptyList()

            // 2. Fetch remote authors & push local authors to Supabase
            val authorsRes = client.getAuthors(config.anonKey, "Bearer ${config.anonKey}")
            if (authorsRes.isSuccessful && !authorsRes.body().isNullOrEmpty()) {
                val authorEntities = authorsRes.body()!!.map { dto ->
                    AuthorEntity(
                        id = dto.id,
                        nameUrdu = dto.nameUrdu,
                        nameEnglish = dto.nameEnglish ?: "",
                        biography = dto.biography ?: "",
                        portraitUrl = dto.portraitUrl,
                        createdAt = dto.createdAt ?: "",
                        updatedAt = dto.updatedAt ?: ""
                    )
                }
                authorDao.insertAuthors(authorEntities)
            }

            // Push local authors to Supabase
            val localAuthors = authorDao.getAllAuthorsFlow().first().ifEmpty {
                SampleUrduNovels.sampleAuthors.map { AuthorEntity.fromDomainModel(it) }
            }
            for (author in localAuthors) {
                val authorDto = AuthorDto(
                    id = author.id.toValidUuidString(),
                    nameUrdu = author.nameUrdu,
                    nameEnglish = author.nameEnglish,
                    biography = author.biography,
                    portraitUrl = author.portraitUrl,
                    createdAt = author.createdAt.ifBlank { now },
                    updatedAt = author.updatedAt.ifBlank { now }
                )
                try {
                    client.pushAuthor(authorDto, config.anonKey, "Bearer ${config.anonKey}")
                } catch (e: Exception) {
                    // Non-fatal
                }
            }

            // 3. Fetch remote categories & push local categories to Supabase
            val catRes = client.getCategories(config.anonKey, "Bearer ${config.anonKey}")
            if (catRes.isSuccessful && !catRes.body().isNullOrEmpty()) {
                val catEntities = catRes.body()!!.map { dto ->
                    CategoryEntity(
                        id = dto.id,
                        nameUrdu = dto.nameUrdu,
                        nameEnglish = dto.nameEnglish ?: "",
                        createdAt = dto.createdAt ?: ""
                    )
                }
                categoryDao.insertCategories(catEntities)
            }

            // Push local categories to Supabase
            val localCategories = categoryDao.getAllCategoriesFlow().first().ifEmpty {
                NovelCategory.entries.map {
                    CategoryEntity(id = it.name.lowercase(), nameUrdu = it.urduName, nameEnglish = it.englishName, createdAt = now)
                }
            }
            for (cat in localCategories) {
                val catDto = CategoryDto(
                    id = cat.id,
                    nameUrdu = cat.nameUrdu,
                    nameEnglish = cat.nameEnglish,
                    createdAt = cat.createdAt.ifBlank { now }
                )
                try {
                    client.pushCategory(catDto, config.anonKey, "Bearer ${config.anonKey}")
                } catch (e: Exception) {
                    // Non-fatal
                }
            }

            // 4. Merge remote novels into Room without overwriting local progress or bookmarks
            for (dto in remoteNovels) {
                val existingLocal = novelDao.getNovelById(dto.id)
                val status: DownloadStatus
                val isDownloaded: Boolean

                if (existingLocal != null) {
                    val isRemoteNewer = (dto.updatedAt != null && existingLocal.updatedAt.isNotBlank() && dto.updatedAt > existingLocal.updatedAt)
                    status = if (isRemoteNewer && existingLocal.isDownloaded) {
                        DownloadStatus.UPDATE_AVAILABLE
                    } else {
                        DownloadStatus.fromString(existingLocal.downloadStatus)
                    }
                    isDownloaded = existingLocal.isDownloaded

                    // Update metadata while preserving local progress and favorite
                    novelDao.updateNovel(
                        existingLocal.copy(
                            titleUrdu = dto.displayTitle,
                            titleEnglish = dto.titleEnglish ?: existingLocal.titleEnglish,
                            authorName = dto.author ?: existingLocal.authorName,
                            categoryName = dto.category ?: existingLocal.categoryName,
                            description = dto.displayDescription,
                            coverImageUrl = dto.coverImageUrl ?: existingLocal.coverImageUrl,
                            publicationStatus = dto.publicationStatus,
                            updatedAt = dto.updatedAt ?: existingLocal.updatedAt,
                            downloadStatus = status.name,
                            isDownloaded = isDownloaded
                        )
                    )
                } else {
                    // New remote novel
                    novelDao.insertNovel(
                        NovelEntity(
                            id = dto.id,
                            titleUrdu = dto.displayTitle,
                            titleEnglish = dto.titleEnglish ?: "",
                            authorId = dto.authorId ?: "",
                            authorName = dto.author ?: "اردو مصنف",
                            categoryId = dto.categoryId ?: "",
                            categoryName = dto.category ?: "عام",
                            description = dto.displayDescription,
                            coverImageUrl = dto.coverImageUrl,
                            coverGradientIndex = dto.coverGradientIndex,
                            publicationStatus = dto.publicationStatus,
                            isFeatured = dto.isFeatured,
                            viewsCount = dto.viewsCount,
                            rating = dto.rating,
                            totalChapters = dto.totalChapters,
                            createdAt = dto.createdAt ?: "",
                            updatedAt = dto.updatedAt ?: "",
                            downloadStatus = DownloadStatus.NOT_DOWNLOADED.name,
                            isDownloaded = false
                        )
                    )
                }
            }

            val allEntities = novelDao.getAllNovelsFlow().first()
            if (allEntities.isNotEmpty()) {
                _novels.value = allEntities.map { it.toDomainModel() }
            }

            _supabaseConfig.value = _supabaseConfig.value.copy(
                isConnected = true,
                totalSyncedNovels = remoteNovels.size,
                lastSyncMessage = "کامیابی! ناولز، مصنفین اور اصناف کلاؤڈ سے ہم آہنگ ہو گئے",
                lastSyncTime = System.currentTimeMillis()
            )
            Pair(true, "کامیابی! ناولز، مصنفین اور اصناف کلاؤڈ سے سنک ہو گئے")
        } catch (e: Exception) {
            Pair(false, "سنک کے دوران خرابی: ${e.localizedMessage ?: "کنکشن ٹائم آؤٹ"}")
        } finally {
            _syncingState.value = false
        }
    }

    private fun String.toValidUuidString(): String {
        if (this.isBlank()) return UUID.randomUUID().toString()
        return try {
            UUID.fromString(this).toString()
        } catch (e: Exception) {
            UUID.nameUUIDFromBytes(this.toByteArray()).toString()
        }
    }

    suspend fun pushNovelToSupabase(novelId: String): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val config = _supabaseConfig.value
        if (!config.isConfigured) {
            return@withContext Pair(false, "ڈیٹا پش کرنے کے لیے Supabase URL اور Key درکار ہے")
        }

        val novel = getNovelByIdAsync(novelId) ?: return@withContext Pair(false, "ناول نہیں ملا")
        val chapters = getChaptersAsync(novelId)

        _syncingState.value = true
        try {
            val client = getApiService(config)
                ?: return@withContext Pair(false, "Supabase کلائنٹ کی خرابی")

            val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date())

            val validNovelId = novel.id.toValidUuidString()

            // 1. Push Author
            if (novel.author.isNotBlank()) {
                val authorId = (novel.authorId?.takeIf { it.isNotBlank() } ?: ("author_" + novel.author)).toValidUuidString()
                val authorDto = AuthorDto(
                    id = authorId,
                    nameUrdu = novel.author,
                    nameEnglish = "",
                    biography = "معروف اردو ناول نگار",
                    portraitUrl = null,
                    createdAt = now,
                    updatedAt = now
                )
                try {
                    client.pushAuthor(authorDto, config.anonKey, "Bearer ${config.anonKey}")
                } catch (e: Exception) {
                    // Non-fatal
                }
            }

            // 2. Push Category
            if (novel.category.isNotBlank()) {
                val categoryId = (novel.categoryId?.takeIf { it.isNotBlank() } ?: ("cat_" + novel.category.lowercase().replace(" ", "_")))
                val categoryDto = CategoryDto(
                    id = categoryId,
                    nameUrdu = novel.category,
                    nameEnglish = "",
                    createdAt = now
                )
                try {
                    client.pushCategory(categoryDto, config.anonKey, "Bearer ${config.anonKey}")
                } catch (e: Exception) {
                    // Non-fatal
                }
            }

            // 3. Push Novel
            val novelDto = NovelDto(
                id = validNovelId,
                titleUrdu = novel.title,
                titleEnglish = "",
                author = novel.author,
                authorId = novel.authorId?.takeIf { it.isNotBlank() }?.toValidUuidString(),
                category = novel.category,
                categoryId = novel.categoryId,
                description = novel.synopsis,
                coverImageUrl = novel.coverImageUrl,
                coverGradientIndex = novel.coverGradientIndex,
                totalChapters = chapters.size.coerceAtLeast(1),
                rating = novel.rating,
                viewsCount = novel.viewsCount,
                publicationStatus = novel.publicationStatus,
                isFeatured = novel.isRecommended,
                createdAt = novel.createdAt.ifBlank { now },
                updatedAt = now
            )

            val novelRes = client.pushNovel(novelDto, config.anonKey, "Bearer ${config.anonKey}")
            if (!novelRes.isSuccessful && novelRes.code() != 409) {
                val errBody = novelRes.errorBody()?.string() ?: ""
                val errorDetail = if (errBody.isNotBlank()) " - $errBody" else ""
                return@withContext Pair(false, "ناول پش نہ ہو سکا (کوڈ ${novelRes.code()})$errorDetail")
            }

            // 4. Push Chapters
            if (chapters.isNotEmpty()) {
                val chapterDtos = chapters.map { ch ->
                    ChapterDto(
                        id = ch.id.toValidUuidString(),
                        novelId = validNovelId,
                        chapterNumber = ch.chapterNumber,
                        chapterTitle = ch.title,
                        content = ch.content,
                        wordCount = ch.wordCount,
                        readTimeMinutes = ch.readTimeMinutes,
                        publicationStatus = ch.publicationStatus,
                        createdAt = ch.createdAt.ifBlank { now },
                        updatedAt = now
                    )
                }
                val chapRes = client.pushChapters(chapterDtos, config.anonKey, "Bearer ${config.anonKey}")
                if (!chapRes.isSuccessful && chapRes.code() != 409) {
                    val errBody = chapRes.errorBody()?.string() ?: ""
                    val errorDetail = if (errBody.isNotBlank()) " - $errBody" else ""
                    return@withContext Pair(false, "ابواب پش نہ ہو سکے (کوڈ ${chapRes.code()})$errorDetail")
                }
            }

            Pair(true, "ناول \"${novel.title}\"، مصنف، صنف اور ${chapters.size} ابواب کامیابی سے Supabase کلاؤڈ پر پش ہو گئے!")
        } catch (e: Exception) {
            Pair(false, "پش کے دوران خرابی: ${e.localizedMessage ?: "نیٹ ورک کا مسئلہ"}")
        } finally {
            _syncingState.value = false
        }
    }

    // -------------------------------------------------------------
    // ADMIN / PUBLISHER MANAGEMENT
    // -------------------------------------------------------------

    suspend fun createCustomNovel(
        title: String,
        author: String,
        category: String,
        synopsis: String,
        initialChapterTitle: String,
        initialChapterContent: String,
        publicationStatus: String = "published",
        coverImageUrl: String? = null
    ): Pair<Novel, Chapter> = withContext(Dispatchers.IO) {
        val novelId = "custom_" + UUID.randomUUID().toString().take(8)
        val chapterId = "ch_" + UUID.randomUUID().toString().take(8)
        val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date())

        val words = initialChapterContent.trim().split(Regex("\\s+")).size
        val readTime = maxOf(1, words / 100)

        val newNovel = Novel(
            id = novelId,
            title = title.trim(),
            author = author.trim(),
            category = category,
            synopsis = synopsis.trim(),
            rating = 5.0f,
            viewsCount = 1,
            coverGradientIndex = (_novels.value.size % 5),
            totalChapters = 1,
            isFavorite = true,
            isDownloaded = true,
            isNew = true,
            isRecommended = true,
            createdAt = now,
            updatedAt = now,
            isCustomPublished = true,
            publicationStatus = publicationStatus,
            coverImageUrl = coverImageUrl,
            downloadStatus = DownloadStatus.AVAILABLE_OFFLINE
        )

        val newChapter = Chapter(
            id = chapterId,
            novelId = novelId,
            chapterNumber = 1,
            title = initialChapterTitle.trim().ifEmpty { "باب اول" },
            content = initialChapterContent.trim(),
            wordCount = words,
            readTimeMinutes = readTime,
            publicationStatus = publicationStatus,
            createdAt = now,
            updatedAt = now,
            downloadStatus = DownloadStatus.AVAILABLE_OFFLINE
        )

        // Save to Room
        novelDao.insertNovel(NovelEntity.fromDomainModel(newNovel, DownloadStatus.AVAILABLE_OFFLINE))
        chapterDao.insertChapter(ChapterEntity.fromDomainModel(newChapter, DownloadStatus.AVAILABLE_OFFLINE))

        // Also update in-memory custom list so immediate synchronous lookups work
        inMemoryCustomChapters.getOrPut(novelId) { mutableListOf() }.add(newChapter)
        _novels.value = listOf(newNovel) + _novels.value

        // If Supabase is connected and status is published, try push in background
        if (_supabaseConfig.value.isConfigured && publicationStatus == "published") {
            repositoryScope.launch {
                pushNovelToSupabase(novelId)
            }
        }

        Pair(newNovel, newChapter)
    }

    suspend fun updateNovelMetadata(
        novelId: String,
        title: String,
        author: String,
        category: String,
        synopsis: String,
        publicationStatus: String,
        coverImageUrl: String?
    ): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date())
        try {
            novelDao.updateNovelMetadata(
                id = novelId,
                title = title.trim(),
                author = author.trim(),
                category = category.trim(),
                synopsis = synopsis.trim(),
                publicationStatus = publicationStatus,
                coverImageUrl = coverImageUrl,
                updatedAt = now
            )

            // Update in-memory
            _novels.value = _novels.value.map {
                if (it.id == novelId) {
                    it.copy(
                        title = title.trim(),
                        author = author.trim(),
                        category = category.trim(),
                        synopsis = synopsis.trim(),
                        publicationStatus = publicationStatus,
                        coverImageUrl = coverImageUrl,
                        updatedAt = now
                    )
                } else it
            }

            // Sync with Supabase if configured
            val config = _supabaseConfig.value
            if (config.isConfigured) {
                val client = getApiService(config)
                if (client != null) {
                    val patchData = mapOf(
                        "title_urdu" to title.trim(),
                        "author" to author.trim(),
                        "category" to category.trim(),
                        "description" to synopsis.trim(),
                        "publication_status" to publicationStatus,
                        "cover_image_url" to coverImageUrl,
                        "updated_at" to now
                    )
                    client.updateNovel("eq.${novelId.toValidUuidString()}", patchData, config.anonKey, "Bearer ${config.anonKey}")
                }
            }

            Pair(true, "ناول کی تفصیلات کامیابی سے اپ ڈیٹ ہو گئیں")
        } catch (e: Exception) {
            Pair(false, "خرابی: ${e.localizedMessage}")
        }
    }

    suspend fun addChapterToNovel(
        novelId: String,
        chapterTitle: String,
        content: String,
        publicationStatus: String = "published"
    ): Chapter = withContext(Dispatchers.IO) {
        val currentChapters = getChaptersAsync(novelId)
        val nextNumber = currentChapters.size + 1
        val chapterId = "ch_" + UUID.randomUUID().toString().take(8)
        val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date())
        val words = content.trim().split(Regex("\\s+")).size
        val readTime = maxOf(1, words / 100)

        val newChapter = Chapter(
            id = chapterId,
            novelId = novelId,
            chapterNumber = nextNumber,
            title = chapterTitle.trim().ifEmpty { "باب $nextNumber" },
            content = content.trim(),
            wordCount = words,
            readTimeMinutes = readTime,
            publicationStatus = publicationStatus,
            createdAt = now,
            updatedAt = now,
            downloadStatus = DownloadStatus.AVAILABLE_OFFLINE
        )

        // Save to Room
        chapterDao.insertChapter(ChapterEntity.fromDomainModel(newChapter, DownloadStatus.AVAILABLE_OFFLINE))

        // Update novel's chapter count
        val novel = getNovelById(novelId)
        if (novel != null) {
            val updatedNovel = novel.copy(totalChapters = novel.totalChapters + 1)
            novelDao.updateNovel(NovelEntity.fromDomainModel(updatedNovel))
            _novels.value = _novels.value.map { if (it.id == novelId) updatedNovel else it }
        }

        // Update in-memory map
        inMemoryCustomChapters.getOrPut(novelId) { mutableListOf() }.add(newChapter)

        // Sync with Supabase if configured
        val config = _supabaseConfig.value
        if (config.isConfigured) {
            repositoryScope.launch {
                val client = getApiService(config)
                if (client != null) {
                    val dto = ChapterDto(
                        id = chapterId.toValidUuidString(),
                        novelId = novelId.toValidUuidString(),
                        chapterNumber = nextNumber,
                        chapterTitle = newChapter.title,
                        content = newChapter.content,
                        wordCount = words,
                        readTimeMinutes = readTime,
                        publicationStatus = publicationStatus,
                        createdAt = now,
                        updatedAt = now
                    )
                    client.pushChapter(dto, config.anonKey, "Bearer ${config.anonKey}")
                }
            }
        }

        newChapter
    }

    suspend fun deleteNovel(novelId: String): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        try {
            markNovelAsDeleted(novelId)
            novelDao.deleteNovelById(novelId)
            chapterDao.deleteChaptersForNovel(novelId)
            bookmarkDao.deleteBookmarksForNovel(novelId)

            cachedRoomChaptersMap.remove(novelId)
            inMemoryCustomChapters.remove(novelId)
            _novels.value = _novels.value.filterNot { it.id == novelId }
            _bookmarks.value = _bookmarks.value.filterNot { it.novelId == novelId }

            prefs.edit()
                .remove("last_chap_$novelId")
                .remove("last_offset_$novelId")
                .remove("last_prog_$novelId")
                .apply()

            // Delete from Supabase if connected
            val config = _supabaseConfig.value
            if (config.isConfigured) {
                val client = getApiService(config)
                if (client != null) {
                    client.deleteNovel("eq.${novelId.toValidUuidString()}", config.anonKey, "Bearer ${config.anonKey}")
                }
            }
            Pair(true, "ناول اور اس کا تمام ڈیٹا کامیابی سے مستقل طور پر حذف کر دیا گیا")
        } catch (e: Exception) {
            Pair(false, "حذف کرنے میں خرابی: ${e.localizedMessage}")
        }
    }

    suspend fun restoreDefaultSampleData(): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        try {
            prefs.edit().remove("deleted_novel_ids").putBoolean("has_seeded_database", false).apply()
            val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date())

            val authorEntities = SampleUrduNovels.sampleAuthors.map { AuthorEntity.fromDomainModel(it) }
            authorDao.insertAuthors(authorEntities)

            val categories = NovelCategory.entries.map {
                CategoryEntity(id = it.name.lowercase(), nameUrdu = it.urduName, nameEnglish = it.englishName, createdAt = now)
            }
            categoryDao.insertCategories(categories)

            val novelEntities = SampleUrduNovels.novelsList.map { novel ->
                NovelEntity.fromDomainModel(novel, DownloadStatus.AVAILABLE_OFFLINE).copy(
                    createdAt = now,
                    updatedAt = now,
                    isDownloaded = true
                )
            }
            novelDao.insertNovels(novelEntities)

            val chapterEntities = mutableListOf<ChapterEntity>()
            SampleUrduNovels.chaptersMap.forEach { (novelId, chapList) ->
                chapList.forEach { ch ->
                    chapterEntities.add(
                        ChapterEntity.fromDomainModel(ch, DownloadStatus.AVAILABLE_OFFLINE).copy(
                            createdAt = now,
                            updatedAt = now,
                            isDownloaded = true
                        )
                    )
                }
            }
            chapterDao.insertChapters(chapterEntities)
            prefs.edit().putBoolean("has_seeded_database", true).apply()

            val allEntities = novelDao.getAllNovelsFlow().first()
            _novels.value = allEntities.map { it.toDomainModel() }

            Pair(true, "تمام نمونہ ناولز کامیابی سے بحال کر دیے گئے")
        } catch (e: Exception) {
            Pair(false, "بحالی میں خرابی: ${e.localizedMessage}")
        }
    }

    suspend fun uploadCoverImage(
        novelId: String,
        imageBytes: ByteArray,
        mimeType: String = "image/jpeg"
    ): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        // Validate size (max 5MB)
        if (imageBytes.size > 5 * 1024 * 1024) {
            return@withContext Pair(false, "تصویر کا سائز 5MB سے کم ہونا ضروری ہے")
        }
        if (!mimeType.startsWith("image/")) {
            return@withContext Pair(false, "صرف تصویر (JPEG, PNG, WEBP) اپ لوڈ کی جا سکتی ہے")
        }

        val config = _supabaseConfig.value
        val filename = "cover_${novelId}_${System.currentTimeMillis()}.${if (mimeType.contains("png")) "png" else "jpg"}"

        if (!config.isConfigured) {
            // Local fallback simulation URL
            val localUrl = "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=400&q=80"
            novelDao.updateNovelMetadata(
                id = novelId,
                title = getNovelById(novelId)?.title ?: "",
                author = getNovelById(novelId)?.author ?: "",
                category = getNovelById(novelId)?.category ?: "",
                synopsis = getNovelById(novelId)?.synopsis ?: "",
                publicationStatus = getNovelById(novelId)?.publicationStatus ?: "published",
                coverImageUrl = localUrl,
                updatedAt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date())
            )
            _novels.value = _novels.value.map { if (it.id == novelId) it.copy(coverImageUrl = localUrl) else it }
            return@withContext Pair(true, "تصویر کا کور کامیابی سے محفوظ ہو گیا (لوکل موڈ)")
        }

        try {
            val client = getApiService(config)
                ?: return@withContext Pair(false, "Supabase کلائنٹ تیار نہیں ہو سکا")

            val requestBody = imageBytes.toRequestBody(mimeType.toMediaTypeOrNull())
            val res = client.uploadCoverImage(
                filename = filename,
                imageBytes = requestBody,
                contentType = mimeType,
                apiKey = config.anonKey,
                authHeader = "Bearer ${config.anonKey}"
            )

            val publicUrl = "${config.url.trimEnd('/')}/storage/v1/object/public/novel-covers/$filename"
            if (res.isSuccessful || res.code() == 200 || res.code() == 201) {
                // Update novel entity with public URL
                novelDao.updateNovelMetadata(
                    id = novelId,
                    title = getNovelById(novelId)?.title ?: "",
                    author = getNovelById(novelId)?.author ?: "",
                    category = getNovelById(novelId)?.category ?: "",
                    synopsis = getNovelById(novelId)?.synopsis ?: "",
                    publicationStatus = getNovelById(novelId)?.publicationStatus ?: "published",
                    coverImageUrl = publicUrl,
                    updatedAt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date())
                )
                _novels.value = _novels.value.map { if (it.id == novelId) it.copy(coverImageUrl = publicUrl) else it }
                Pair(true, "سرورق تصویر کامیابی سے Supabase اسٹوریج پر اپ لوڈ ہو گئی!")
            } else {
                Pair(false, "اسٹوریج اپ لوڈ ناکام: کوڈ ${res.code()}")
            }
        } catch (e: Exception) {
            Pair(false, "اپ لوڈ میں خرابی: ${e.localizedMessage}")
        }
    }

    suspend fun searchNovelsOffline(query: String): List<Novel> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext _novels.value
        val entities = novelDao.searchNovels(query.trim())
        entities.map { it.toDomainModel() }
    }
}
