package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Novel
import com.example.data.model.NovelCategory
import com.example.ui.components.AuthorCard
import com.example.ui.components.ContinueReadingCard
import com.example.ui.components.CoverGradients
import com.example.ui.components.NovelCoverCard
import com.example.ui.components.NovelVerticalCard
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.NovelViewModel

@Composable
fun HomeScreen(
    viewModel: NovelViewModel,
    modifier: Modifier = Modifier
) {
    val allNovels by viewModel.allNovels.collectAsState()
    val continueReadingNovels by viewModel.continueReadingNovels.collectAsState()
    val trendingNovels by viewModel.trendingNovels.collectAsState()
    val newNovels by viewModel.newNovels.collectAsState()
    val recommendedNovels by viewModel.recommendedNovels.collectAsState()
    val authors by viewModel.authors.collectAsState()

    val featuredNovel = allNovels.firstOrNull { it.id == "novel_peer_e_kamil" } ?: allNovels.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // 1. Premium Top App Bar & Welcome Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(EmeraldDark, EmeraldPrimary)
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                // Top App Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile Avatar with Gold Border
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { viewModel.selectTab(4) } // Profile tab
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF246D55))
                                .border(1.5.dp, GoldAccent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "ش",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "خوش آمدید",
                                fontFamily = FontFamily.Serif,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "شاہد ثاقب",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                    }

                    // App Title Center
                    Text(
                        text = "داستانِ نو",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = GoldAccent
                    )

                    // Action Icons: Search & Notifications
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.selectTab(2) }, // Search tab
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "تلاش کریں",
                                tint = Color.White
                            )
                        }

                        IconButton(
                            onClick = { viewModel.showMessage("آپ کے لیے 2 نئے ابواب دستیاب ہیں") },
                            modifier = Modifier.size(38.dp)
                        ) {
                            BadgedBox(
                                badge = {
                                    Badge(containerColor = GoldAccent) {
                                        Text("2", color = Color.Black, fontSize = 9.sp)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "اطلاعات",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Welcome Literature Quote / Heading
                Column {
                    Text(
                        text = "آج آپ کون سا شاہکار پڑھنا چاہیں گے؟",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Text(
                        text = "اردو ادب کا سب سے بڑا ڈیجیٹل کتب خانہ — خالص یونیکوڈ متن میں",
                        fontFamily = FontFamily.Serif,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        // 2. Large Featured Novel Card
        if (featuredNovel != null) {
            item {
                FeaturedNovelHero(
                    novel = featuredNovel,
                    onReadClick = { viewModel.navigateToReader(featuredNovel.id) },
                    onDetailsClick = { viewModel.navigateToDetail(featuredNovel.id) }
                )
            }
        }

        // 3. Continue Reading Section (دوبارہ پڑھنا جاری رکھیں)
        if (continueReadingNovels.isNotEmpty()) {
            item {
                HomeSectionHeader(
                    title = "دوبارہ پڑھنا جاری رکھیں",
                    subtitle = "جہاں سے چھوڑا تھا وہیں سے شروع کریں",
                    showAction = true,
                    actionText = "تمام دیکھیں",
                    onActionClick = { viewModel.selectTab(3) } // Library
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    continueReadingNovels.take(2).forEach { novel ->
                        ContinueReadingCard(
                            novel = novel,
                            onResumeClick = { viewModel.navigateToReader(novel.id) }
                        )
                    }
                }
            }
        }

        // 4. Horizontal "مشہور ناولز" (Popular Novels)
        item {
            HomeSectionHeader(
                title = "مشہور اور مقبول ترین ناولز",
                subtitle = "قارئین کی پہلی پسند",
                showAction = true,
                actionText = "سب دیکھیں",
                onActionClick = {
                    viewModel.selectCategory(NovelCategory.ALL)
                    viewModel.selectTab(1)
                }
            )
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(trendingNovels, key = { it.id }) { novel ->
                    NovelVerticalCard(
                        novel = novel,
                        onClick = { viewModel.navigateToDetail(novel.id) }
                    )
                }
            }
        }

        // 5. Genres / Categories Horizontal Chips
        item {
            HomeSectionHeader(
                title = "اصناف و موضوعات (Genres)",
                subtitle = "اپنی پسندیدہ صنف منتخب کریں",
                showAction = true,
                actionText = "تمام اصناف",
                onActionClick = { viewModel.selectTab(1) }
            )
        }

        item {
            val categoriesList = listOf(
                NovelCategory.MOHABBAT,
                NovelCategory.ROMANCE,
                NovelCategory.SPY,
                NovelCategory.SUSPENSE,
                NovelCategory.HISTORICAL,
                NovelCategory.ISLAHI,
                NovelCategory.SOCIAL,
                NovelCategory.SPIRITUAL,
                NovelCategory.CLASSIC
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categoriesList.forEach { category ->
                    FilterChip(
                        selected = false,
                        onClick = {
                            viewModel.selectCategory(category)
                            viewModel.selectTab(1) // Navigate to categories
                        },
                        label = {
                            Text(
                                text = category.urduName,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Text("✦", color = GoldAccent, fontSize = 10.sp)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }
        }

        // 6. "نئے ناولز" (New Releases)
        item {
            HomeSectionHeader(
                title = "نئے شامل کردہ ناولز",
                subtitle = "تازہ ترین اشاعتیں اور ابواب",
                showAction = true,
                actionText = "مزید",
                onActionClick = { viewModel.selectTab(1) }
            )
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(newNovels, key = { it.id }) { novel ->
                    NovelVerticalCard(
                        novel = novel,
                        onClick = { viewModel.navigateToDetail(novel.id) }
                    )
                }
            }
        }

        // 7. Popular Authors ("مقبول مصنفین")
        item {
            HomeSectionHeader(
                title = "مقبول اور معتبر مصنفین",
                subtitle = "اردو ادب کے نامور ادیب اور قلم کار",
                showAction = false
            )
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
                            viewModel.setSearchQuery(author.name)
                            viewModel.selectTab(2) // Jump to search
                        }
                    )
                }
            }
        }

        // 8. "آپ کے لیے منتخب" (Recommended for you)
        item {
            HomeSectionHeader(
                title = "آپ کے لیے منتخب ناولز",
                subtitle = "اعلیٰ ریٹنگ اور بہترین کلام",
                showAction = false
            )
        }

        items(recommendedNovels.take(3), key = { it.id }) { novel ->
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)) {
                NovelCoverCard(
                    novel = novel,
                    onClick = { viewModel.navigateToDetail(novel.id) },
                    onToggleFavorite = { viewModel.toggleFavorite(novel.id) }
                )
            }
        }
    }
}

@Composable
private fun HomeSectionHeader(
    title: String,
    subtitle: String,
    showAction: Boolean = false,
    actionText: String = "",
    onActionClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontFamily = FontFamily.Serif,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }

        if (showAction) {
            Text(
                text = actionText,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = EmeraldPrimary,
                modifier = Modifier
                    .clickable(onClick = onActionClick)
                    .padding(4.dp)
            )
        }
    }
}

@Composable
private fun FeaturedNovelHero(
    novel: Novel,
    onReadClick: () -> Unit,
    onDetailsClick: () -> Unit
) {
    val gradientColors = CoverGradients[novel.coverGradientIndex % CoverGradients.size]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp)
            .clickable(onClick = onDetailsClick)
            .testTag("featured_hero_card"),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(EmeraldDark, Color(0xFF132F24))
                    )
                )
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Book Cover preview
                Box(
                    modifier = Modifier
                        .width(96.dp)
                        .height(138.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.verticalGradient(gradientColors))
                        .border(1.5.dp, GoldAccent.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(text = "✦", color = GoldAccent, fontSize = 10.sp)
                        Text(
                            text = novel.title,
                            color = Color.White,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = novel.author,
                            color = Color.White.copy(alpha = 0.85f),
                            fontFamily = FontFamily.Serif,
                            fontSize = 9.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Novel metadata & Read Now button
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GoldAccent.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "آج کا شاہکار انتخاب",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = GoldAccent,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = novel.title,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "از ${novel.author}",
                        fontFamily = FontFamily.Serif,
                        fontSize = 13.sp,
                        color = GoldAccent,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )

                    Text(
                        text = novel.synopsis,
                        fontFamily = FontFamily.Serif,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.75f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = onReadClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldAccent,
                                contentColor = Color(0xFF2C1E05)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ابھی پڑھیں",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "%.1f".format(novel.rating),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
