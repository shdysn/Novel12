package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Bookmark
import com.example.data.model.Novel
import com.example.ui.components.ContinueReadingCard
import com.example.ui.components.NovelCoverCard
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.NovelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: NovelViewModel,
    modifier: Modifier = Modifier
) {
    val bookmarks by viewModel.bookmarks.collectAsState()
    val allNovels by viewModel.allNovels.collectAsState()
    val continueReadingNovels by viewModel.continueReadingNovels.collectAsState()

    // 0: میری کتابیں, 1: بک مارکس, 2: حال ہی میں پڑھے گئے, 3: ڈاؤن لوڈ شدہ, 4: پڑھنے کی تاریخ
    var selectedTab by remember { mutableIntStateOf(0) }

    val downloadedNovels = allNovels.filter { it.isDownloaded }
    val myBooks = allNovels.filter { it.isFavorite || it.readingProgress > 0f }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("library_screen")
    ) {
        // Top Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(EmeraldDark)
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalLibrary,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "میری لائبریری (My Library)",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )
            }
            Text(
                text = "آپ کے محفوظ کردہ ناولز، بک مارکس اور مطالعہ کی سرگزشت",
                fontFamily = FontFamily.Serif,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        // 5 Library Categories Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            edgePadding = 16.dp
        ) {
            val tabs = listOf(
                "میری کتابیں (${myBooks.size})",
                "بک مارکس (${bookmarks.size})",
                "حال ہی میں پڑھے گئے",
                "ڈاؤن لوڈ شدہ (${downloadedNovels.size})",
                "پڑھنے کی تاریخ"
            )

            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontFamily = FontFamily.Serif,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        // Tab Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // میری کتابیں (My Books / Saved)
                    if (myBooks.isEmpty()) {
                        item {
                            EmptyLibraryState(
                                title = "کوئی کتاب شامل نہیں",
                                message = "کسی بھی ناول کو پڑھنا شروع کریں یا دل کے نشان پر ٹیپ کر کے لائبریری میں محفوظ کریں"
                            )
                        }
                    } else {
                        items(myBooks, key = { it.id }) { novel ->
                            NovelCoverCard(
                                novel = novel,
                                onClick = { viewModel.navigateToDetail(novel.id) },
                                onToggleFavorite = { viewModel.toggleFavorite(novel.id) }
                            )
                        }
                    }
                }

                1 -> {
                    // بک مارک کیے گئے ناولز
                    if (bookmarks.isEmpty()) {
                        item {
                            EmptyLibraryState(
                                title = "کوئی بک مارک موجود نہیں",
                                message = "ناول کا مطالعہ کرتے وقت اوپر بک مارک آئیکون پر کلک کریں تاکہ وہ یہاں محفوظ ہو سکے"
                            )
                        }
                    } else {
                        items(bookmarks, key = { it.novelId + it.chapterId }) { bookmark ->
                            BookmarkCard(
                                bookmark = bookmark,
                                onResumeClick = {
                                    viewModel.navigateToReader(bookmark.novelId, bookmark.chapterId)
                                },
                                onRemoveClick = {
                                    viewModel.repository.removeBookmark(bookmark.novelId)
                                }
                            )
                        }
                    }
                }

                2 -> {
                    // حال ہی میں پڑھے گئے (Recently Read)
                    if (continueReadingNovels.isEmpty()) {
                        item {
                            EmptyLibraryState(
                                title = "حال ہی میں کچھ نہیں پڑھا گیا",
                                message = "کسی بھی ناول کا مطالعہ شروع کریں، وہ یہاں خود بخود شامل ہو جائے گا"
                            )
                        }
                    } else {
                        items(continueReadingNovels, key = { it.id }) { novel ->
                            ContinueReadingCard(
                                novel = novel,
                                onResumeClick = { viewModel.navigateToReader(novel.id) }
                            )
                        }
                    }
                }

                3 -> {
                    // ڈاؤن لوڈ کیے گئے ناولز (Offline)
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = null,
                                    tint = EmeraldPrimary
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "یہ تمام ناولز آپ کے فون میں محفوظ ہیں اور انٹرنیٹ کے بغیر بھی پڑھے جا سکتے ہیں۔",
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }

                    if (downloadedNovels.isEmpty()) {
                        item {
                            EmptyLibraryState(
                                title = "کوئی آف لائن ناول موجود نہیں",
                                message = "کسی بھی ناول کی تفصیلات کھول کر 'آف لائن پڑھنے کے لیے محفوظ کریں' پر کلک کریں"
                            )
                        }
                    } else {
                        items(downloadedNovels, key = { it.id }) { novel ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.navigateToDetail(novel.id) },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = novel.title,
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "مصنف: ${novel.author}  •  ${novel.totalChapters} ابواب  •  آف لائن محفوظ",
                                            fontFamily = FontFamily.Serif,
                                            fontSize = 12.sp,
                                            color = EmeraldPrimary
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = { viewModel.deleteDownloadedNovel(novel.id) }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete Offline Cache",
                                                tint = MaterialTheme.colorScheme.outline
                                            )
                                        }

                                        Button(
                                            onClick = { viewModel.navigateToReader(novel.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("مطالعہ", fontFamily = FontFamily.Serif, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                4 -> {
                    // پڑھنے کی تاریخ اور اعداد و شمار (Reading History & Stats)
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "مطالعہ کے اعداد و شمار (Reading Stats)",
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    ReadingStatItem("42 گھنٹے", "کل مطالعہ کا وقت")
                                    ReadingStatItem("${allNovels.size}", "ناولز مکمل")
                                    ReadingStatItem("7 دن", "مسلسل مطالعہ (Streak)")
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "تاریخچہ مطالعہ",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )

                            IconButton(onClick = { viewModel.clearReadingHistory() }) {
                                Icon(
                                    imageVector = Icons.Default.DeleteSweep,
                                    contentDescription = "Clear History",
                                    tint = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }

                    items(allNovels.filter { it.readingProgress > 0f }, key = { it.id }) { novel ->
                        ContinueReadingCard(
                            novel = novel,
                            onResumeClick = { viewModel.navigateToReader(novel.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReadingStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = EmeraldPrimary
        )
        Text(
            text = label,
            fontFamily = FontFamily.Serif,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun BookmarkCard(
    bookmark: Bookmark,
    onResumeClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onResumeClick)
            .testTag("bookmark_card_${bookmark.novelId}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "Bookmark",
                        tint = GoldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = bookmark.novelTitle,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = onRemoveClick,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove Bookmark",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = "مصنف: ${bookmark.author}  •  ${bookmark.chapterTitle}",
                fontFamily = FontFamily.Serif,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Reading Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { bookmark.scrollProgressPercent.coerceIn(0.05f, 1f) },
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = EmeraldPrimary,
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "${(bookmark.scrollProgressPercent * 100).toInt()}% پڑھا گیا",
                    fontFamily = FontFamily.Serif,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable(onClick = onResumeClick)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "وہیں سے جاری رکھیں",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyLibraryState(
    title: String,
    message: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.BookmarkBorder,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = title,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = message,
            fontFamily = FontFamily.Serif,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.outline,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
