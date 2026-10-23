package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Author
import com.example.data.model.Bookmark
import com.example.data.model.Chapter
import com.example.data.model.Novel
import com.example.data.model.NovelCategory
import com.example.data.model.ReaderSettings
import com.example.data.model.SupabaseConfig
import com.example.data.repository.NovelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ScreenState {
    data object Main : ScreenState()
    data class NovelDetail(val novelId: String) : ScreenState()
    data class Reader(val novelId: String, val chapterId: String, val initialCharOffset: Int = 0) : ScreenState()
    data class AuthorDetail(val authorId: String) : ScreenState()
}

class NovelViewModel(application: Application) : AndroidViewModel(application) {

    val repository = NovelRepository(application.applicationContext)

    // Navigation and screen state
    private val _screenState = MutableStateFlow<ScreenState>(ScreenState.Main)
    val screenState: StateFlow<ScreenState> = _screenState.asStateFlow()

    // 0: Home (ہوم), 1: Categories (کیٹیگریز), 2: Search (سرچ), 3: Library (میری لائبریری), 4: Profile (پروفائل)
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // Search and Category filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(NovelCategory.ALL)
    val selectedCategory: StateFlow<NovelCategory> = _selectedCategory.asStateFlow()

    val allNovels: StateFlow<List<Novel>> = repository.novels
    val authors: StateFlow<List<Author>> = repository.authors
    val bookmarks: StateFlow<List<Bookmark>> = repository.bookmarks
    val recentSearches: StateFlow<List<String>> = repository.recentSearches
    val popularSearches: List<String> = repository.popularSearches
    val readerSettings: StateFlow<ReaderSettings> = repository.readerSettings
    val supabaseConfig: StateFlow<SupabaseConfig> = repository.supabaseConfig
    val isSyncing: StateFlow<Boolean> = repository.syncingState
    val downloadStatuses: StateFlow<Map<String, com.example.data.local.DownloadStatus>> = repository.downloadStatuses

    // Specialized Novel Sections for Home Screen
    val continueReadingNovels: StateFlow<List<Novel>> = repository.novels.map { list ->
        list.filter { it.readingProgress > 0f }.sortedByDescending { it.readingProgress }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trendingNovels: StateFlow<List<Novel>> = repository.novels.map { list ->
        list.filter { it.isTrending || it.viewsCount > 25000 }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val newNovels: StateFlow<List<Novel>> = repository.novels.map { list ->
        list.filter { it.isNew || it.isCustomPublished }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recommendedNovels: StateFlow<List<Novel>> = repository.novels.map { list ->
        list.filter { it.isRecommended || it.rating >= 4.88f }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered novels list for Search & Category screens
    val filteredNovels: StateFlow<List<Novel>> = combine(
        repository.novels,
        _searchQuery,
        _selectedCategory
    ) { list, query, category ->
        list.filter { novel ->
            val matchesCategory = (category == NovelCategory.ALL) ||
                    novel.category.contains(category.urduName, ignoreCase = true) ||
                    (category == NovelCategory.MOHABBAT && (novel.category.contains("محبت") || novel.category.contains("رومانوی"))) ||
                    (category == NovelCategory.ROMANCE && novel.category.contains("رومانوی")) ||
                    (category == NovelCategory.SPY && (novel.category.contains("جاسوسی") || novel.category.contains("سسپنس"))) ||
                    (category == NovelCategory.SUSPENSE && novel.category.contains("سسپنس")) ||
                    (category == NovelCategory.HISTORICAL && novel.category.contains("تاریخی")) ||
                    (category == NovelCategory.ISLAHI && novel.category.contains("اصلاحی")) ||
                    (category == NovelCategory.SOCIAL && novel.category.contains("معاشرتی")) ||
                    (category == NovelCategory.SPIRITUAL && novel.category.contains("روحانی")) ||
                    (category == NovelCategory.CLASSIC && novel.category.contains("کلاسیک"))

            val matchesQuery = query.isBlank() ||
                    novel.title.contains(query, ignoreCase = true) ||
                    novel.author.contains(query, ignoreCase = true) ||
                    novel.synopsis.contains(query, ignoreCase = true) ||
                    novel.category.contains(query, ignoreCase = true)

            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Toast/Snackbar notifications
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun dismissUserMessage() {
        _userMessage.value = null
    }

    fun showMessage(msg: String) {
        _userMessage.value = msg
    }

    fun selectTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
        _screenState.value = ScreenState.Main
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isNotBlank() && query.length >= 3) {
            repository.addRecentSearch(query)
        }
    }

    fun selectCategory(category: NovelCategory) {
        _selectedCategory.value = category
    }

    fun clearRecentSearches() {
        repository.clearRecentSearches()
        showMessage("تلاش کی تاریخ صاف کر دی گئی")
    }

    fun clearReadingHistory() {
        repository.clearReadingHistory()
        showMessage("مطالعہ کی تاریخ صاف کر دی گئی")
    }

    fun navigateToDetail(novelId: String) {
        _screenState.value = ScreenState.NovelDetail(novelId)
    }

    fun navigateToReader(novelId: String, chapterId: String? = null, initialCharOffset: Int = 0) {
        if (chapterId != null) {
            _screenState.value = ScreenState.Reader(novelId, chapterId, initialCharOffset)
            return
        }

        // Check if there is a saved reading location
        val (lastChapId, lastOffset, _) = repository.getLastReadingLocation(novelId)
        val targetChapterId = lastChapId ?: repository.getChapters(novelId).firstOrNull()?.id ?: return
        _screenState.value = ScreenState.Reader(novelId, targetChapterId, lastOffset)
    }

    fun navigateBack() {
        when (_screenState.value) {
            is ScreenState.Reader -> {
                val novelId = (_screenState.value as ScreenState.Reader).novelId
                _screenState.value = ScreenState.NovelDetail(novelId)
            }
            is ScreenState.NovelDetail, is ScreenState.AuthorDetail -> {
                _screenState.value = ScreenState.Main
            }
            ScreenState.Main -> {
                // At root
            }
        }
    }

    fun toggleFavorite(novelId: String) {
        repository.toggleFavorite(novelId)
        val novel = repository.getNovelById(novelId)
        if (novel != null) {
            val status = if (novel.isFavorite) "پسندیدہ میں شامل کر لیا گیا" else "پسندیدہ سے ہٹا دیا گیا"
            showMessage(status)
        }
    }

    fun saveBookmark(
        novel: Novel,
        chapter: Chapter,
        progressPercent: Float = 0f,
        charOffset: Int = 0,
        pageNumber: Int = 1,
        snippet: String = "",
        note: String = ""
    ) {
        repository.addBookmark(novel, chapter, charOffset, pageNumber, snippet, note, progressPercent)
        showMessage("صفحہ $pageNumber کا بک مارک محفوظ ہو گیا")
    }

    fun removeBookmarkById(bookmarkId: String) {
        repository.removeBookmarkById(bookmarkId)
        showMessage("بک مارک ہٹا دیا گیا")
    }

    fun deleteBookmark(bookmarkId: String) {
        removeBookmarkById(bookmarkId)
    }

    fun saveReadingLocation(novelId: String, chapterId: String, charOffset: Int, progressPercent: Float) {
        repository.saveReadingLocation(novelId, chapterId, charOffset, progressPercent)
    }

    fun updateReaderSettings(settings: ReaderSettings) {
        repository.updateReaderSettings(settings)
    }

    fun getNovel(novelId: String): Novel? = repository.getNovelById(novelId)

    fun getChapters(novelId: String): List<Chapter> = repository.getChapters(novelId)

    fun getChapter(novelId: String, chapterId: String): Chapter? = repository.getChapter(novelId, chapterId)

    fun getRelatedNovels(novel: Novel): List<Novel> {
        return repository.novels.value
            .filter { it.id != novel.id && (it.category == novel.category || it.author == novel.author) }
            .take(4)
    }

    fun getAuthorNovels(authorName: String): List<Novel> {
        return repository.novels.value.filter { it.author.contains(authorName) || authorName.contains(it.author) }
    }

    // Chapter Navigation in Reader
    fun navigateToNextChapter(novelId: String, currentChapterId: String) {
        val chapters = repository.getChapters(novelId)
        val currentIndex = chapters.indexOfFirst { it.id == currentChapterId }
        if (currentIndex in 0 until chapters.size - 1) {
            val nextChap = chapters[currentIndex + 1]
            _screenState.value = ScreenState.Reader(novelId, nextChap.id)
        } else {
            showMessage("یہ اس ناول کا آخری باب ہے")
        }
    }

    fun navigateToPreviousChapter(novelId: String, currentChapterId: String) {
        val chapters = repository.getChapters(novelId)
        val currentIndex = chapters.indexOfFirst { it.id == currentChapterId }
        if (currentIndex > 0) {
            val prevChap = chapters[currentIndex - 1]
            _screenState.value = ScreenState.Reader(novelId, prevChap.id)
        } else {
            showMessage("یہ ناول کا پہلا باب ہے")
        }
    }

    // Supabase actions
    fun saveSupabaseSettings(url: String, key: String) {
        repository.saveSupabaseConfig(url, key)
        showMessage("Supabase کی معلومات محفوظ ہو گئیں")
    }

    fun testSupabaseConnection() {
        viewModelScope.launch {
            val (success, message) = repository.testSupabaseConnection()
            showMessage(message)
        }
    }

    fun syncFromSupabase() {
        viewModelScope.launch {
            val (success, message) = repository.syncWithSupabase()
            showMessage(message)
        }
    }

    fun pushNovelToCloud(novelId: String) {
        viewModelScope.launch {
            val (success, message) = repository.pushNovelToSupabase(novelId)
            showMessage(message)
        }
    }

    // Offline Download controls
    fun downloadNovel(novelId: String) {
        viewModelScope.launch {
            showMessage("ڈاؤن لوڈ شروع ہو رہا ہے...")
            val (success, message) = repository.downloadNovel(novelId)
            showMessage(message)
        }
    }

    fun cancelDownload(novelId: String) {
        repository.cancelDownload(novelId)
        showMessage("ڈاؤن لوڈ منسوخ کر دیا گیا")
    }

    fun deleteDownloadedNovel(novelId: String) {
        viewModelScope.launch {
            val (success, message) = repository.deleteDownloadedNovel(novelId)
            showMessage(message)
        }
    }

    // Admin & Novel Management actions
    fun publishNewNovel(
        title: String,
        author: String,
        category: String,
        synopsis: String,
        initialChapterTitle: String,
        initialChapterContent: String,
        publicationStatus: String = "published",
        coverImageUrl: String? = null,
        onSuccess: (Novel) -> Unit
    ) {
        viewModelScope.launch {
            if (title.isBlank() || author.isBlank() || initialChapterContent.isBlank()) {
                showMessage("براہ کرم عنوان، مصنف کا نام اور باب کا متن درج کریں")
                return@launch
            }
            val (newNovel, _) = repository.createCustomNovel(
                title = title,
                author = author,
                category = category,
                synopsis = synopsis,
                initialChapterTitle = initialChapterTitle,
                initialChapterContent = initialChapterContent,
                publicationStatus = publicationStatus,
                coverImageUrl = coverImageUrl
            )
            val statusMsg = if (publicationStatus == "draft") "ڈرافٹ محفوظ ہو گیا" else "شائع ہو گیا"
            showMessage("ناول \"$title\" کامیابی سے $statusMsg!")
            onSuccess(newNovel)
        }
    }

    fun updateNovelMetadata(
        novelId: String,
        title: String,
        author: String,
        category: String,
        synopsis: String,
        publicationStatus: String,
        coverImageUrl: String? = null,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val (success, msg) = repository.updateNovelMetadata(
                novelId = novelId,
                title = title,
                author = author,
                category = category,
                synopsis = synopsis,
                publicationStatus = publicationStatus,
                coverImageUrl = coverImageUrl
            )
            showMessage(msg)
            if (success) onSuccess()
        }
    }

    fun deleteNovel(novelId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val (success, msg) = repository.deleteNovel(novelId)
            showMessage(msg)
            if (success) onSuccess()
        }
    }

    fun uploadCoverImage(novelId: String, imageBytes: ByteArray, mimeType: String = "image/jpeg") {
        viewModelScope.launch {
            showMessage("تصویر اپ لوڈ کی جا رہی ہے...")
            val (success, msg) = repository.uploadCoverImage(novelId, imageBytes, mimeType)
            showMessage(msg)
        }
    }

    fun addNewChapter(
        novelId: String,
        chapterTitle: String,
        content: String,
        publicationStatus: String = "published",
        onSuccess: (Chapter) -> Unit
    ) {
        viewModelScope.launch {
            if (content.isBlank()) {
                showMessage("براہ کرم باب کا متن درج کریں")
                return@launch
            }
            val chapter = repository.addChapterToNovel(novelId, chapterTitle, content, publicationStatus)
            showMessage("نیا باب \"${chapter.title}\" شامل ہو گیا")
            onSuccess(chapter)
        }
    }
}
