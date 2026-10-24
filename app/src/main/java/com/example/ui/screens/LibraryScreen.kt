package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Novel
import com.example.ui.components.NovelVerticalCard
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.MidnightDarkSurface
import com.example.ui.theme.MidnightDarkSurfaceElevated
import com.example.ui.theme.TextMutedPurple
import com.example.ui.theme.UrduFontFamily
import com.example.ui.viewmodel.NovelViewModel

/**
 * Screen 5: میری لائبریری (My Shelf / Library)
 * Strictly matches Screen 5 of the reference mockup in Dark Midnight Purple & Gold.
 */
@Composable
fun LibraryScreen(
    viewModel: NovelViewModel,
    modifier: Modifier = Modifier
) {
    val bookmarks by viewModel.bookmarks.collectAsState()
    val allNovels by viewModel.allNovels.collectAsState()
    val continueReadingNovels by viewModel.continueReadingNovels.collectAsState()

    // 0: پسندیدہ, 1: ڈاؤن لوڈز, 2: جاری
    var selectedTab by remember { mutableIntStateOf(0) }

    val downloadedNovels = allNovels.filter { it.isDownloaded }
    val favoriteNovels = allNovels.filter { it.isFavorite }
    val activeNovels = if (continueReadingNovels.isNotEmpty()) continueReadingNovels else allNovels.take(4)

    val currentTabNovels = when (selectedTab) {
        0 -> if (favoriteNovels.isNotEmpty()) favoriteNovels else allNovels.take(6)
        1 -> if (downloadedNovels.isNotEmpty()) downloadedNovels else allNovels.take(3)
        else -> activeNovels
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .testTag("library_screen")
    ) {
        // -------------------------------------------------------------
        // 1. TOP HEADER: "میری لائبریری" in Gold
        // -------------------------------------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "میری لائبریری",
                fontFamily = UrduFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = GoldAccent
            )
        }

        // -------------------------------------------------------------
        // 2. SEGMENTED TABS: "پسندیدہ" | "ڈاؤن لوڈز" | "جاری"
        // -------------------------------------------------------------
        val tabs = listOf("پسندیدہ", "ڈاؤن لوڈز", "جاری")
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = GoldAccent,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = GoldAccent,
                    height = 2.dp
                )
            },
            divider = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFF2E2748))
                )
            },
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontFamily = UrduFontFamily,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp,
                            color = if (selectedTab == index) GoldAccent else TextMutedPurple
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // ---------------------------------------------------------
            // 3. آخری پڑھا ہوا (HORIZONTAL CAROUSEL OF RECENT NOVELS)
            // ---------------------------------------------------------
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "آخری پڑھا ہوا",
                        fontFamily = UrduFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                }
            }

            item {
                val recentCards = if (continueReadingNovels.isNotEmpty()) continueReadingNovels else allNovels.take(3)
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recentCards, key = { "recent_${it.id}" }) { novel ->
                        Card(
                            modifier = Modifier
                                .width(120.dp)
                                .clickable { viewModel.navigateToReader(novel.id) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MidnightDarkSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E2748))
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF241E38))
                                        .border(1.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .padding(6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = novel.title,
                                        fontFamily = UrduFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = GoldAccent,
                                        textAlign = TextAlign.Center,
                                        maxLines = 3
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = novel.title,
                                    fontFamily = UrduFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Surface(
                                    shape = CircleShape,
                                    color = MidnightDarkSurfaceElevated,
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Text(
                                        text = "1KD 85%",
                                        fontSize = 9.sp,
                                        color = GoldAccent,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ---------------------------------------------------------
            // 4. آخری پڑھا ہوا / LIBRARY GRID (3-COLUMN NOVEL CARDS)
            // ---------------------------------------------------------
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "محفوظ شدہ کتب",
                        fontFamily = UrduFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                }
            }

            // 3-Column Grid of Novels
            val chunkedNovels = currentTabNovels.chunked(3)
            items(chunkedNovels) { rowNovels ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowNovels.forEach { novel ->
                        LibraryGridCard(
                            novel = novel,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.navigateToDetail(novel.id) }
                        )
                    }
                    // Fill remaining slots in row if less than 3
                    repeat(3 - rowNovels.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

/**
 * 3-Column Novel Card matching Screen 5 in reference mockup
 */
@Composable
private fun LibraryGridCard(
    novel: Novel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E2748))
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF201B34))
                    .border(1.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = novel.title,
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = GoldAccent,
                    textAlign = TextAlign.Center,
                    maxLines = 3
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = novel.title,
                fontFamily = UrduFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Surface(
                shape = CircleShape,
                color = Color(0xFF1B152A),
                border = androidx.compose.foundation.BorderStroke(0.6.dp, GoldAccent),
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Text(
                    text = if (novel.readingProgress > 0f) "Read ${(novel.readingProgress * 100).toInt()}%" else "Read",
                    fontSize = 9.sp,
                    color = GoldAccent,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}
