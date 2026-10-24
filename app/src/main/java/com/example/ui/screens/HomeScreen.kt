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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Novel
import com.example.data.model.NovelCategory
import com.example.ui.components.AuthorCard
import com.example.ui.components.NovelVerticalCard
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.MidnightDarkSurface
import com.example.ui.theme.MidnightDarkSurfaceElevated
import com.example.ui.theme.TextMutedPurple
import com.example.ui.theme.UrduFontFamily
import com.example.ui.viewmodel.NovelViewModel

/**
 * Screen 1: ہوم اسکرین (Writer's Highlight / Home Screen)
 * Directly matches Screen 1 of the reference mockup in Dark Midnight Purple & Gold.
 */
@Composable
fun HomeScreen(
    viewModel: NovelViewModel,
    modifier: Modifier = Modifier
) {
    val allNovels by viewModel.allNovels.collectAsState()
    val continueReadingNovels by viewModel.continueReadingNovels.collectAsState()
    val trendingNovels by viewModel.trendingNovels.collectAsState()
    val newNovels by viewModel.newNovels.collectAsState()
    val authors by viewModel.authors.collectAsState()

    val currentReadingNovel = continueReadingNovels.firstOrNull() ?: allNovels.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // -------------------------------------------------------------
        // 1. TOP BAR: "ہیلو، قاری!" & Notification Bell
        // -------------------------------------------------------------
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ہیلو، قاری!",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.White
                )

                IconButton(
                    onClick = { viewModel.showMessage("آپ کے لیے نئے ابواب دستیاب ہیں") },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MidnightDarkSurfaceElevated)
                ) {
                    BadgedBox(
                        badge = {
                            Badge(containerColor = GoldAccent)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "اطلاعات",
                            tint = GoldAccent,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // 2. SEARCH BAR: "ناول، مصنف تلاش کریں"
        // -------------------------------------------------------------
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MidnightDarkSurfaceElevated)
                    .border(1.dp, Color(0xFF2F2948), RoundedCornerShape(24.dp))
                    .clickable { viewModel.selectTab(2) } // Navigate to Search tab
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "تلاش",
                        tint = GoldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "ناول، مصنف تلاش کریں",
                        fontFamily = UrduFontFamily,
                        fontSize = 14.sp,
                        color = TextMutedPurple
                    )
                }
            }
        }

        // -------------------------------------------------------------
        // 3. POPULAR AUTHORS: "مقبول مصنفین" (With Gold Rings & Follow Pills)
        // -------------------------------------------------------------
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "مقبول مصنفین",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Previous",
                        tint = GoldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Next",
                        tint = GoldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(authors, key = { it.id }) { author ->
                    AuthorCard(
                        author = author,
                        onClick = {
                            viewModel.navigateToAuthorDetail(author.id)
                        }
                    )
                }
            }
        }

        // -------------------------------------------------------------
        // 4. CATEGORY TILES (4 Square Cards: رومانوی، جاسوسی، تاریخی، ادبی)
        // -------------------------------------------------------------
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CategoryGridItem(
                    title = "رومانوی",
                    icon = Icons.Default.Favorite,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.selectCategory(NovelCategory.MOHABBAT)
                        viewModel.selectTab(1)
                    }
                )
                CategoryGridItem(
                    title = "جاسوسی",
                    icon = Icons.Default.Search,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.selectCategory(NovelCategory.SPY)
                        viewModel.selectTab(1)
                    }
                )
                CategoryGridItem(
                    title = "تاریخی",
                    icon = Icons.Default.AccountBalance,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.selectCategory(NovelCategory.HISTORICAL)
                        viewModel.selectTab(1)
                    }
                )
                CategoryGridItem(
                    title = "ادبی",
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.selectCategory(NovelCategory.CLASSIC)
                        viewModel.selectTab(1)
                    }
                )
            }
        }

        // -------------------------------------------------------------
        // 5. CONTINUE READING CARD: "پڑھنا جاری رکھیں"
        // -------------------------------------------------------------
        if (currentReadingNovel != null) {
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "پڑھنا جاری رکھیں",
                        fontFamily = UrduFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .clickable { viewModel.navigateToReader(currentReadingNovel.id) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MidnightDarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF383058))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Novel Mini Cover Thumbnail
                        Box(
                            modifier = Modifier
                                .size(52.dp, 68.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF282343))
                                .border(1.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentReadingNovel.title.take(6),
                                fontFamily = UrduFontFamily,
                                fontSize = 10.sp,
                                color = GoldAccent,
                                maxLines = 2,
                                modifier = Modifier.padding(2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        // Text & Progress
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentReadingNovel.title,
                                fontFamily = UrduFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = "پڑھنا جاری رکھیں — از ${currentReadingNovel.author}",
                                fontFamily = UrduFontFamily,
                                fontSize = 11.sp,
                                color = TextMutedPurple
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            val progressVal = if (currentReadingNovel.readingProgress > 0f) currentReadingNovel.readingProgress else 0.35f
                            LinearProgressIndicator(
                                progress = { progressVal },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = GoldAccent,
                                trackColor = Color(0xFF2A2345)
                            )
                        }
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // 6. POPULAR NOVELS: "مقبول ترین ناول"
        // -------------------------------------------------------------
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "مقبول ترین ناول",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )

                TextButton(onClick = { viewModel.selectTab(1) }) {
                    Text(
                        text = "سبھی دیکھیں",
                        fontFamily = UrduFontFamily,
                        fontSize = 13.sp,
                        color = GoldAccent
                    )
                }
            }
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(trendingNovels.ifEmpty { allNovels.take(6) }, key = { it.id }) { novel ->
                    NovelVerticalCard(
                        novel = novel,
                        onClick = { viewModel.navigateToDetail(novel.id) }
                    )
                }
            }
        }

        // Full Width Gold Pill: "سبھی نوول دیکھیں"
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = { viewModel.selectTab(1) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldAccent,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "سبھی نوول دیکھیں",
                        fontFamily = UrduFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // -------------------------------------------------------------
        // 7. LATEST NOVELS: "تازہ ترین"
        // -------------------------------------------------------------
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "تازہ ترین",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Previous",
                        tint = GoldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Next",
                        tint = GoldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        item {
            val latestList = if (newNovels.isNotEmpty()) newNovels else allNovels.takeLast(4)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(latestList, key = { it.id }) { novel ->
                    NovelVerticalCard(
                        novel = novel,
                        onClick = { viewModel.navigateToDetail(novel.id) }
                    )
                }
            }
        }
    }
}

/**
 * 4 Category Grid Box matching Screen 1 in reference mockup
 */
@Composable
private fun CategoryGridItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(72.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E274A))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = GoldAccent,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontFamily = UrduFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                color = Color.White
            )
        }
    }
}
