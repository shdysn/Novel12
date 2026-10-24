package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import com.example.data.model.ReaderTheme
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import com.example.data.model.toUrduDigits
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookPage
import com.example.data.model.FontStyleType
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.MehrNastaliqFontFamily
import com.example.ui.theme.NotoNastaliqUrduFontFamily
import com.example.data.model.TextAlignmentType
import com.example.ui.components.BookmarkDialog
import com.example.ui.components.ReaderSettingsSheet
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.UrduFontFamily
import com.example.ui.viewmodel.NovelViewModel
import com.example.util.TextPaginator
import kotlinx.coroutines.launch

/**
 * Phase 2: Professional Page-by-Page Urdu Novel Reader Screen.
 * Replaces continuous vertical scroll with a real digital book reader experience:
 * - Fixed, readable portion per page calculated with native StaticLayout
 * - Horizontal swipe and tap zone navigation
 * - Immersive mode with toggleable top/bottom controls
 * - Nastaliq Urdu typography, 4 reading themes, adjustable margins and alignment
 * - Bookmarks with notes, character offset restoration, and table of contents
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    novelId: String,
    chapterId: String,
    initialCharOffset: Int = 0,
    viewModel: NovelViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    val novel = viewModel.getNovel(novelId)
    val chapter = viewModel.getChapter(novelId, chapterId)
    val allChapters = viewModel.getChapters(novelId)
    val readerSettings by viewModel.readerSettings.collectAsState()
    val allBookmarks by viewModel.bookmarks.collectAsState()

    // Dialog & Sheet states
    var showSettingsSheet by remember { mutableStateOf(false) }
    var showChaptersSheet by remember { mutableStateOf(false) }
    var showBookmarkDialog by remember { mutableStateOf(false) }
    var showSearchBar by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Immersive Reading mode: open directly into pure book leaf mode without intrusive toolbars
    var isControlsVisible by remember { mutableStateOf(false) }

    // Track character offset to restore reading location on font / margin recalculations
    var currentTrackingOffset by remember { mutableIntStateOf(initialCharOffset) }

    if (novel == null || chapter == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("باب دستیاب نہیں ہے", fontFamily = UrduFontFamily)
        }
        return
    }

    // Filter bookmarks for this specific novel
    val novelBookmarks = remember(allBookmarks, novel.id) {
        allBookmarks.filter { it.novelId == novel.id }
    }

    val fontFam = com.example.ui.theme.getFontFamily(readerSettings.fontStyle)
    val activeTheme = readerSettings.theme

    val composeTextAlign = when (readerSettings.alignment) {
        TextAlignmentType.JUSTIFIED -> TextAlign.Justify
        TextAlignmentType.RIGHT_ALIGNED -> TextAlign.Right
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(activeTheme.deskColor)
            .testTag("reader_screen")
    ) {
        val screenWidthDp = maxWidth
        val screenHeightDp = maxHeight

        // Physical book page sheet padding from surrounding desk surface
        val sheetHorizPadding = if (activeTheme == ReaderTheme.PURE_WHITE) 0.dp else 6.dp
        val sheetVertPadding = if (activeTheme == ReaderTheme.PURE_WHITE) 0.dp else 4.dp

        // Dimensions available for pure page text inside the physical book sheet
        val extraHorizPadDp = if (readerSettings.showBookFrame) 16.dp else 6.dp
        val textWidthDp = (screenWidthDp - (sheetHorizPadding * 2) - (readerSettings.horizontalMarginDp * 2).dp - extraHorizPadDp).coerceAtLeast(100.dp)

        // Exact vertical overhead calculation:
        // System insets + Sheet padding (8dp) + Page column padding (extraVertPadDp + verticalMargin*2) + Header/Footer (~48dp)
        val insets = WindowInsets.systemBars.asPaddingValues()
        val sysBarsHeight = insets.calculateTopPadding() + insets.calculateBottomPadding()
        val extraVertPadDp = if (readerSettings.showBookFrame) 10.dp else 4.dp
        val bookOverheadDp = sysBarsHeight + (sheetVertPadding * 2) + extraVertPadDp + (readerSettings.verticalMarginDp * 2).dp + 64.dp
        val textHeightDp = (screenHeightDp - bookOverheadDp).coerceAtLeast(150.dp)

        val widthPx = with(density) { textWidthDp.roundToPx() }
        val heightPx = with(density) { textHeightDp.roundToPx() }
        val textSizePx = with(density) { readerSettings.fontSizeSp.sp.toPx() }
        val paragraphSpacingPx = with(density) { readerSettings.paragraphSpacingDp.dp.toPx() }

        val context = androidx.compose.ui.platform.LocalContext.current

        // Real text pagination calculated via Android's native StaticLayout
        val pages: List<BookPage> = remember(
            chapter.id,
            chapter.content,
            widthPx,
            heightPx,
            textSizePx,
            readerSettings.lineHeightMultiplier,
            paragraphSpacingPx,
            readerSettings.fontStyle,
            context
        ) {
            TextPaginator.paginate(
                text = chapter.content,
                widthPx = widthPx,
                heightPx = heightPx,
                textSizePx = textSizePx,
                lineSpacingMultiplier = readerSettings.lineHeightMultiplier,
                paragraphSpacingPx = paragraphSpacingPx,
                fontStyle = readerSettings.fontStyle,
                context = context
            )
        }

        // Horizontal Pager State
        val pagerState = rememberPagerState(
            initialPage = 0,
            pageCount = { pages.size.coerceAtLeast(1) }
        )

        // Restore reading location whenever pages recalculate or on initial entry
        LaunchedEffect(pages, currentTrackingOffset) {
            if (pages.isNotEmpty()) {
                val targetPage = TextPaginator.findPageIndexForCharOffset(pages, currentTrackingOffset)
                if (targetPage in pages.indices && targetPage != pagerState.currentPage) {
                    pagerState.scrollToPage(targetPage)
                }
            }
        }

        // Auto-save reading position on page change
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { currentPageIdx ->
                if (pages.isNotEmpty() && currentPageIdx in pages.indices) {
                    val currentPage = pages[currentPageIdx]
                    currentTrackingOffset = currentPage.startCharOffset
                    val progress = if (pages.size > 1) {
                        (currentPageIdx.toFloat() / (pages.size - 1)).coerceIn(0f, 1f)
                    } else {
                        1f
                    }
                    viewModel.saveReadingLocation(
                        novelId = novel.id,
                        chapterId = chapter.id,
                        charOffset = currentPage.startCharOffset,
                        progressPercent = progress
                    )
                }
            }
        }

        // Check if the current page has a bookmark
        val isCurrentPageBookmarked by remember {
            derivedStateOf {
                val currentPageIdx = pagerState.currentPage
                if (pages.isNotEmpty() && currentPageIdx in pages.indices) {
                    val p = pages[currentPageIdx]
                    novelBookmarks.any { b ->
                        b.chapterId == chapter.id && (
                            b.pageNumber == p.pageNumber ||
                                (b.charOffset >= p.startCharOffset && b.charOffset <= p.endCharOffset)
                        )
                    }
                } else {
                    false
                }
            }
        }

        // Reading Area with Horizontal Pager (Authentic Book Leaf resting on Desk)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = sheetHorizPadding, vertical = sheetVertPadding)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("novel_horizontal_pager")
            ) { pageIndex ->
                val page = pages.getOrNull(pageIndex)
                if (page != null) {
                    SinglePageView(
                        page = page,
                        novelTitle = novel.title,
                        chapterTitle = chapter.title,
                        chapterNumber = chapter.chapterNumber,
                        totalChapters = allChapters.size,
                        totalPages = pages.size,
                        readerSettings = readerSettings,
                        searchQuery = searchQuery,
                        activeTheme = activeTheme,
                        fontFam = fontFam,
                        composeTextAlign = composeTextAlign,
                        onNextChapter = {
                            if (chapter.chapterNumber < allChapters.size) {
                                viewModel.navigateToNextChapter(novel.id, chapter.id)
                            }
                        }
                    )
                }
            }
        }

        // Tap Zones Detection Overlay: Left 22% = Next Page (Urdu RTL), Right 22% = Prev Page, Center 56% = Toggle UI
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .pointerInput(pages.size) {
                    detectTapGestures(
                        onTap = { offset ->
                            val screenW = size.width
                            val x = offset.x
                            when {
                                // In RTL reading: Left side tap turns to NEXT page
                                x < screenW * 0.22f -> {
                                    if (pagerState.currentPage < pages.size - 1) {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                        }
                                    } else if (chapter.chapterNumber < allChapters.size) {
                                        viewModel.navigateToNextChapter(novel.id, chapter.id)
                                    }
                                }
                                // Right side tap turns to PREVIOUS page
                                x > screenW * 0.78f -> {
                                    if (pagerState.currentPage > 0) {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                        }
                                    } else if (chapter.chapterNumber > 1) {
                                        viewModel.navigateToPreviousChapter(novel.id, chapter.id)
                                    }
                                }
                                // Center tap: toggles top & bottom controls
                                else -> {
                                    isControlsVisible = !isControlsVisible
                                }
                            }
                        }
                    )
                }
        )

        // Top Navigation Bar (Animated)
        AnimatedVisibility(
            visible = isControlsVisible,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Surface(
                color = activeTheme.backgroundColor.copy(alpha = 0.96f),
                shadowElevation = 3.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Back Button & Novel Title
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            IconButton(
                                onClick = { viewModel.navigateBack() },
                                modifier = Modifier.testTag("reader_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = activeTheme.textColor
                                )
                            }

                            Column(modifier = Modifier.padding(start = 4.dp)) {
                                Text(
                                    text = novel.title,
                                    fontFamily = fontFam,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = activeTheme.textColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = chapter.title,
                                    fontFamily = fontFam,
                                    fontSize = 12.sp,
                                    color = activeTheme.textColor.copy(alpha = 0.75f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Action Buttons: Search, Bookmark, Appearance, Table of Contents
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Search in Chapter
                            IconButton(
                                onClick = { showSearchBar = !showSearchBar },
                                modifier = Modifier.testTag("reader_search_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search in chapter",
                                    tint = activeTheme.textColor
                                )
                            }

                            // Bookmark Button (Toggles bookmark or opens dialog)
                            IconButton(
                                onClick = { showBookmarkDialog = true },
                                modifier = Modifier.testTag("save_bookmark_button")
                            ) {
                                Icon(
                                    imageVector = if (isCurrentPageBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Bookmark",
                                    tint = if (isCurrentPageBookmarked) GoldAccent else activeTheme.textColor
                                )
                            }

                            // Appearance Settings
                            IconButton(
                                onClick = { showSettingsSheet = true },
                                modifier = Modifier.testTag("reader_appearance_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FormatSize,
                                    contentDescription = "Font settings",
                                    tint = activeTheme.textColor
                                )
                            }

                            // Table of Contents
                            IconButton(
                                onClick = { showChaptersSheet = true },
                                modifier = Modifier.testTag("reader_chapters_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.List,
                                    contentDescription = "Chapters list",
                                    tint = activeTheme.textColor
                                )
                            }
                        }
                    }

                    // Quick Navigation Pills matching Screen 4 in reference screenshot
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF261F38),
                            border = BorderStroke(0.8.dp, GoldAccent.copy(alpha = 0.5f)),
                            modifier = Modifier.clickable { viewModel.downloadNovel(novel.id) }
                        ) {
                            Text("ڈاؤن لوڈز", fontFamily = UrduFontFamily, fontSize = 11.sp, color = GoldAccent, modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp))
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF261F38),
                            border = BorderStroke(0.8.dp, Color(0xFF382F50)),
                            modifier = Modifier.clickable { showSettingsSheet = true }
                        ) {
                            Text("قاری", fontFamily = UrduFontFamily, fontSize = 11.sp, color = Color.White, modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp))
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF261F38),
                            border = BorderStroke(0.8.dp, Color(0xFF382F50)),
                            modifier = Modifier.clickable {
                                if (pagerState.currentPage > 0) {
                                    coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                                } else {
                                    viewModel.navigateToPreviousChapter(novel.id, chapter.id)
                                }
                            }
                        ) {
                            Text("پچھلا", fontFamily = UrduFontFamily, fontSize = 11.sp, color = Color.White, modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp))
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF261F38),
                            border = BorderStroke(0.8.dp, Color(0xFF382F50)),
                            modifier = Modifier.clickable {
                                if (pagerState.currentPage < pages.size - 1) {
                                    coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                                } else {
                                    viewModel.navigateToNextChapter(novel.id, chapter.id)
                                }
                            }
                        ) {
                            Text("اگلا", fontFamily = UrduFontFamily, fontSize = 11.sp, color = Color.White, modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp))
                        }
                    }

                    // Optional In-Chapter Search Bar
                    AnimatedVisibility(visible = showSearchBar) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = {
                                    Text(
                                        "اس باب میں لفظ تلاش کریں...",
                                        fontFamily = fontFam,
                                        fontSize = 12.sp
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                singleLine = true,
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                                        }
                                    }
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = activeTheme.backgroundColor,
                                    unfocusedContainerColor = activeTheme.backgroundColor,
                                    focusedTextColor = activeTheme.textColor,
                                    unfocusedTextColor = activeTheme.textColor
                                )
                            )
                        }
                    }
                }
            }
        }

        // Bottom Reading Controls Bar (Animated)
        AnimatedVisibility(
            visible = isControlsVisible,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Surface(
                color = activeTheme.backgroundColor.copy(alpha = 0.96f),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Page Progress & Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val currentPageNumber = (pagerState.currentPage + 1).coerceAtMost(pages.size)
                        val totalPages = pages.size.coerceAtLeast(1)

                        Text(
                            text = "صفحہ $currentPageNumber / $totalPages",
                            fontFamily = fontFam,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = activeTheme.textColor
                        )

                        // Direct Page Jump Slider
                        Slider(
                            value = pagerState.currentPage.toFloat(),
                            onValueChange = { targetVal ->
                                coroutineScope.launch {
                                    pagerState.scrollToPage(targetVal.toInt().coerceIn(0, totalPages - 1))
                                }
                            },
                            valueRange = 0f..(totalPages - 1).coerceAtLeast(0).toFloat(),
                            steps = (totalPages - 2).coerceAtLeast(0),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 10.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = EmeraldPrimary,
                                activeTrackColor = EmeraldPrimary,
                                inactiveTrackColor = activeTheme.textColor.copy(alpha = 0.2f)
                            )
                        )

                        val percent = if (totalPages > 0) ((currentPageNumber.toFloat() / totalPages) * 100).toInt() else 0
                        Text(
                            text = "$percent%",
                            fontFamily = fontFam,
                            fontSize = 12.sp,
                            color = activeTheme.textColor.copy(alpha = 0.8f)
                        )
                    }

                    // Navigation Controls: Prev Page, Chapters, Next Page
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Previous Page / Chapter Button (Right-aligned in RTL)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    if (pagerState.currentPage > 0) {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                        }
                                    } else if (chapter.chapterNumber > 1) {
                                        viewModel.navigateToPreviousChapter(novel.id, chapter.id)
                                    }
                                },
                                enabled = pagerState.currentPage > 0 || chapter.chapterNumber > 1,
                                modifier = Modifier.testTag("reader_prev_page_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "پچھلا صفحہ",
                                    tint = activeTheme.textColor
                                )
                            }

                            Text(
                                text = "پچھلا",
                                fontFamily = fontFam,
                                fontSize = 12.sp,
                                color = activeTheme.textColor.copy(alpha = 0.75f)
                            )
                        }

                        // Chapter Indicator & Selector
                        TextButton(
                            onClick = { showChaptersSheet = true },
                            modifier = Modifier.testTag("reader_chapter_indicator")
                        ) {
                            Text(
                                text = "باب ${chapter.chapterNumber} از ${allChapters.size}",
                                fontFamily = fontFam,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = EmeraldPrimary
                            )
                        }

                        // Next Page / Chapter Button (Left-aligned in RTL)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "اگلا",
                                fontFamily = fontFam,
                                fontSize = 12.sp,
                                color = activeTheme.textColor.copy(alpha = 0.75f)
                            )

                            IconButton(
                                onClick = {
                                    if (pagerState.currentPage < pages.size - 1) {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                        }
                                    } else if (chapter.chapterNumber < allChapters.size) {
                                        viewModel.navigateToNextChapter(novel.id, chapter.id)
                                    }
                                },
                                enabled = pagerState.currentPage < pages.size - 1 || chapter.chapterNumber < allChapters.size,
                                modifier = Modifier.testTag("reader_next_page_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "اگلا صفحہ",
                                    tint = activeTheme.textColor
                                )
                            }
                        }
                    }
                }
            }
        }

        // Software Brightness Dimming Overlay
        if (readerSettings.brightness < 0.99f) {
            val dimAlpha = ((1f - readerSettings.brightness) * 0.75f).coerceIn(0f, 0.75f)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = dimAlpha))
            )
        }

        // Bookmarks Management Dialog
        if (showBookmarkDialog) {
            val currentPage = (pagerState.currentPage + 1).coerceIn(1, pages.size.coerceAtLeast(1))
            val currentSnippet = chapter.content.substring(
                currentTrackingOffset,
                (currentTrackingOffset + 120).coerceAtMost(chapter.content.length)
            )

            BookmarkDialog(
                currentPageNumber = currentPage,
                currentChapterTitle = chapter.title,
                currentSnippet = currentSnippet,
                novelBookmarks = novelBookmarks,
                onAddBookmark = { note ->
                    val progress = if (pages.size > 1) (pagerState.currentPage.toFloat() / (pages.size - 1)).coerceIn(0f, 1f) else 1f
                    viewModel.saveBookmark(
                        novel = novel,
                        chapter = chapter,
                        progressPercent = progress,
                        charOffset = currentTrackingOffset,
                        pageNumber = currentPage,
                        note = note,
                        snippet = currentSnippet
                    )
                    showBookmarkDialog = false
                },
                onSelectBookmark = { bookmark ->
                    showBookmarkDialog = false
                    if (bookmark.chapterId != chapter.id) {
                        viewModel.navigateToReader(novel.id, bookmark.chapterId, bookmark.charOffset)
                    } else {
                        currentTrackingOffset = bookmark.charOffset
                    }
                },
                onDeleteBookmark = { bookmarkId ->
                    viewModel.deleteBookmark(bookmarkId)
                },
                onDismiss = { showBookmarkDialog = false }
            )
        }
    }

    // Reader Appearance Settings Bottom Sheet
    if (showSettingsSheet) {
        ReaderSettingsSheet(
            settings = readerSettings,
            onSettingsChanged = { updated ->
                viewModel.updateReaderSettings(updated)
            },
            onDismiss = { showSettingsSheet = false }
        )
    }

    // Chapters Selector Bottom Sheet
    if (showChaptersSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showChaptersSheet = false },
            sheetState = sheetState,
            containerColor = activeTheme.backgroundColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "فہرستِ ابواب — ${novel.title}",
                    fontFamily = fontFam,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = activeTheme.textColor,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                ) {
                    items(allChapters, key = { it.id }) { ch ->
                        val isCurrent = ch.id == chapter.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    showChaptersSheet = false
                                    viewModel.navigateToReader(novel.id, ch.id, 0)
                                },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCurrent) EmeraldPrimary.copy(alpha = 0.15f) else activeTheme.textColor.copy(alpha = 0.05f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${ch.chapterNumber}. ${ch.title}",
                                    fontFamily = fontFam,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = if (isCurrent) EmeraldPrimary else activeTheme.textColor
                                )

                                if (isCurrent) {
                                    Text(
                                        text = "موجودہ باب",
                                        fontFamily = fontFam,
                                        fontSize = 11.sp,
                                        color = EmeraldPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Renders an individual book page formatted with pure Unicode Urdu text,
 * balanced margins, Nastaliq typography, and decorative elements on start/end.
 */
/**
 * Renders an authentic physical book page formatted with pure Unicode Urdu text,
 * running book title header at the top, genuine book frame, spine binding crease,
 * and prominent page number folio at the bottom.
 */
@Composable
private fun SinglePageView(
    page: BookPage,
    novelTitle: String,
    chapterTitle: String,
    chapterNumber: Int,
    totalChapters: Int,
    totalPages: Int,
    readerSettings: com.example.data.model.ReaderSettings,
    searchQuery: String,
    activeTheme: com.example.data.model.ReaderTheme,
    fontFam: FontFamily,
    composeTextAlign: TextAlign,
    onNextChapter: () -> Unit
) {
    val pageNumberDisplay = if (readerSettings.useUrduNumerals) {
        page.pageNumber.toUrduDigits()
    } else {
        page.pageNumber.toString()
    }
    val totalPagesDisplay = if (readerSettings.useUrduNumerals) {
        totalPages.toUrduDigits()
    } else {
        totalPages.toString()
    }
    val chapterNumberDisplay = if (readerSettings.useUrduNumerals) {
        chapterNumber.toUrduDigits()
    } else {
        chapterNumber.toString()
    }
    val totalChaptersDisplay = if (readerSettings.useUrduNumerals) {
        totalChapters.toUrduDigits()
    } else {
        totalChapters.toString()
    }

    // Highlight search keyword if entered
    val annotatedText = remember(page.text, searchQuery, activeTheme) {
        buildAnnotatedString {
            val text = page.text.replace(Regex(" {2,}"), " ")
            if (searchQuery.isBlank()) {
                append(text)
            } else {
                var startIndex = 0
                while (startIndex < text.length) {
                    val index = text.indexOf(searchQuery, startIndex, ignoreCase = true)
                    if (index == -1) {
                        append(text.substring(startIndex))
                        break
                    }
                    append(text.substring(startIndex, index))
                    withStyle(
                        style = SpanStyle(
                            background = GoldAccent.copy(alpha = 0.45f),
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(text.substring(index, index + searchQuery.length))
                    }
                    startIndex = index + searchQuery.length
                }
            }
        }
    }

    val isPureWhite = activeTheme == ReaderTheme.PURE_WHITE

    // Authentic Physical Book Page Sheet Container
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("page_content_${page.pageNumber}"),
        shape = if (isPureWhite) {
            RectangleShape
        } else {
            RoundedCornerShape(
                topStart = 8.dp,
                bottomStart = 8.dp,
                topEnd = 3.dp,
                bottomEnd = 3.dp
            )
        },
        color = activeTheme.backgroundColor,
        shadowElevation = if (isPureWhite) 0.dp else 5.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Stacked Pages Layer Illusion & Spine Crease (Only shown in book paper themes, not in pure white)
            if (!isPureWhite) {
                // 1. Stacked Pages Layer Illusion on Left Outer Edge (turning edge in RTL)
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .fillMaxHeight()
                        .width(6.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    activeTheme.textColor.copy(alpha = 0.08f),
                                    activeTheme.textColor.copy(alpha = 0.02f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // 2. Spine Binding Crease Shadow on Right Inner Edge (binding gutter in RTL)
                // Curves realistically into the book spine
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .width(26.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    activeTheme.textColor.copy(alpha = 0.02f),
                                    activeTheme.textColor.copy(alpha = 0.06f),
                                    activeTheme.textColor.copy(alpha = 0.13f)
                                )
                            )
                        )
                )
                // Hairline spine gutter fold crease
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .width(1.dp)
                        .background(activeTheme.textColor.copy(alpha = 0.16f))
                )
            }

            // 3. Classical Urdu Double Book Frame (کتابی حاشیہ / جدول)
            if (readerSettings.showBookFrame) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)
                ) {
                    // Outer border
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(
                                width = 0.8.dp,
                                color = activeTheme.textColor.copy(alpha = 0.22f),
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                    // Inner hairline border
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(3.dp)
                            .border(
                                width = 0.5.dp,
                                color = activeTheme.textColor.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                    // Classical corner flourishes
                    Text(
                        text = "❖",
                        fontSize = 7.sp,
                        color = GoldAccent.copy(alpha = 0.85f),
                        modifier = Modifier.align(Alignment.TopStart).padding(4.5.dp)
                    )
                    Text(
                        text = "❖",
                        fontSize = 7.sp,
                        color = GoldAccent.copy(alpha = 0.85f),
                        modifier = Modifier.align(Alignment.TopEnd).padding(4.5.dp)
                    )
                    Text(
                        text = "❖",
                        fontSize = 7.sp,
                        color = GoldAccent.copy(alpha = 0.85f),
                        modifier = Modifier.align(Alignment.BottomStart).padding(4.5.dp)
                    )
                    Text(
                        text = "❖",
                        fontSize = 7.sp,
                        color = GoldAccent.copy(alpha = 0.85f),
                        modifier = Modifier.align(Alignment.BottomEnd).padding(4.5.dp)
                    )
                }
            }

            // 4. Unified Sequential Page Layout (Header -> Body [weight 1f, clipToBounds] -> Footer)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = (readerSettings.horizontalMarginDp + (if (readerSettings.showBookFrame) 8 else 3)).dp,
                        end = (readerSettings.horizontalMarginDp + (if (readerSettings.showBookFrame) 8 else 3)).dp,
                        top = (readerSettings.verticalMarginDp + (if (readerSettings.showBookFrame) 6 else 2)).dp,
                        bottom = (readerSettings.verticalMarginDp + (if (readerSettings.showBookFrame) 6 else 3)).dp
                    )
            ) {
                // [A] TOP RUNNING BOOK HEADER:
                // Suppressed on chapter opening page (page 1) as in real printed novels
                if (!page.isFirstPageOfChapter) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Right: Novel title in quotes
                            Text(
                                text = "« $novelTitle »",
                                fontFamily = fontFam,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp,
                                color = activeTheme.textColor.copy(alpha = 0.72f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )

                            // Center: Classic book ornament
                            Text(
                                text = "❦  ❖  ❦",
                                fontSize = 9.sp,
                                color = GoldAccent.copy(alpha = 0.9f),
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )

                            // Left: Chapter title
                            Text(
                                text = chapterTitle,
                                fontFamily = fontFam,
                                fontWeight = FontWeight.Normal,
                                fontSize = 11.sp,
                                color = activeTheme.textColor.copy(alpha = 0.72f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Elegant Book Hairline Rule with Center Diamond
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(0.6.dp)
                                    .background(activeTheme.textColor.copy(alpha = 0.18f))
                            )
                            Text(
                                text = " ❖ ",
                                fontSize = 7.sp,
                                color = GoldAccent.copy(alpha = 0.85f),
                                modifier = Modifier.padding(horizontal = 3.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(0.6.dp)
                                    .background(activeTheme.textColor.copy(alpha = 0.18f))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                } else {
                    // COMPACT CHAPTER HEADER (مختصر باب کا عنوان)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("— ❖ ", color = if (isPureWhite) Color.Black else GoldAccent, fontSize = 11.sp)
                        Text(
                            text = chapterTitle,
                            fontFamily = fontFam,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (isPureWhite) Color.Black else EmeraldPrimary,
                            textAlign = TextAlign.Center
                        )
                        Text(" ❖ —", color = if (isPureWhite) Color.Black else GoldAccent, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                }

                // [B] PAGE BODY TEXT CONTAINER (Strict bounded container with clipToBounds)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clipToBounds()
                ) {
                    Text(
                        text = annotatedText,
                        fontFamily = fontFam,
                        fontSize = readerSettings.fontSizeSp.sp,
                        lineHeight = (readerSettings.fontSizeSp * readerSettings.lineHeightMultiplier).sp,
                        color = activeTheme.textColor,
                        textAlign = composeTextAlign,
                        style = TextStyle(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // [C] BOTTOM RUNNING BOOK FOLIO (Sequential footer at the bottom of the column)
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Subtle hairline rule above page number
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(0.6.dp)
                                .background(if (isPureWhite) Color(0x26000000) else activeTheme.textColor.copy(alpha = 0.14f))
                        )
                        Text(
                            text = " ❖ ",
                            fontSize = 7.sp,
                            color = if (isPureWhite) Color.Black.copy(alpha = 0.4f) else GoldAccent.copy(alpha = 0.6f),
                            modifier = Modifier.padding(horizontal = 3.dp)
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(0.6.dp)
                                .background(if (isPureWhite) Color(0x26000000) else activeTheme.textColor.copy(alpha = 0.14f))
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Right: Chapter info
                        Text(
                            text = "باب $chapterNumberDisplay",
                            fontFamily = fontFam,
                            fontSize = 10.sp,
                            color = if (isPureWhite) Color.Black.copy(alpha = 0.45f) else activeTheme.textColor.copy(alpha = 0.45f)
                        )

                        // Center: Authentic Printed Page Number
                        Text(
                            text = "—  $pageNumberDisplay  —",
                            fontFamily = fontFam,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isPureWhite) Color.Black.copy(alpha = 0.8f) else activeTheme.textColor.copy(alpha = 0.75f)
                        )

                        // Left: Page Count Progress
                        Text(
                            text = "$pageNumberDisplay / $totalPagesDisplay",
                            fontFamily = fontFam,
                            fontSize = 10.sp,
                            color = if (isPureWhite) Color.Black.copy(alpha = 0.45f) else activeTheme.textColor.copy(alpha = 0.45f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Calculates page number helper.
 */
private fun pagerStateCurrentPage(novel: com.example.data.model.Novel, chapter: com.example.data.model.Chapter, charOffset: Int): Int {
    return ((charOffset / 800) + 1).coerceAtLeast(1)
}
