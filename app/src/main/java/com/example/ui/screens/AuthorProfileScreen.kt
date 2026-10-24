package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Author
import com.example.ui.components.NovelVerticalCard
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldDark
import com.example.ui.theme.MidnightDarkSurface
import com.example.ui.theme.MidnightDarkSurfaceElevated
import com.example.ui.theme.TextMutedPurple
import com.example.ui.theme.UrduFontFamily
import com.example.ui.viewmodel.NovelViewModel

/**
 * Screen 2: مصنف کا پروفائل (Writer's Profile)
 * Designed to exactly match Screen 2 from the reference mockup.
 */
@Composable
fun AuthorProfileScreen(
    authorId: String,
    viewModel: NovelViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val authors by viewModel.authors.collectAsState()
    val author = authors.firstOrNull { it.id == authorId }
        ?: authors.firstOrNull()
        ?: Author(
            id = "author_umera_ahmed",
            name = "عمیرہ احمد",
            bio = "اردو ادب کی مقبول ترین معاصر ناول نگار جن کے شاہکاروں میں پیرِ کامل، آبِ حیات اور میری ذات ذرہ بے نشاں شامل ہیں۔",
            totalNovelsCount = 50,
            followersCount = "2.5M+",
            avatarColorIndex = 0
        )

    val authorNovels = viewModel.getAuthorNovels(author.name)
    val followedAuthors by viewModel.followedAuthors.collectAsState()
    val isFollowing = followedAuthors.contains(author.id)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .testTag("author_profile_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // 1. Top Navigation Bar with Back Arrow
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MidnightDarkSurfaceElevated)
                        .testTag("author_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "واپس",
                        tint = GoldAccent
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "مصنف کا پروفائل",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = GoldAccent
                )

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(40.dp))
            }
        }

        // 2. Profile Avatar with Gold Ring Border & Verified Badge
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    // Circular Avatar
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(MidnightDarkSurfaceElevated)
                            .border(2.5.dp, GoldAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = author.name.firstOrNull()?.toString() ?: "ع",
                            fontFamily = UrduFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 38.sp,
                            color = GoldAccent
                        )
                    }

                    // Verified Checkmark Badge
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2196F3))
                            .border(1.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Verified",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Author Name in Gold
                Text(
                    text = author.name,
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = GoldAccent
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Stats Row: "50+ کتب" | "2.5M+ قاری"
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${author.totalNovelsCount.coerceAtLeast(10)}+",
                            fontFamily = UrduFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Text(
                            text = "کتب",
                            fontFamily = UrduFontFamily,
                            fontSize = 12.sp,
                            color = TextMutedPurple
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(28.dp)
                            .background(TextMutedPurple.copy(alpha = 0.3f))
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (author.followersCount.isNotEmpty()) author.followersCount else "2.5M+",
                            fontFamily = UrduFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Text(
                            text = "قاری",
                            fontFamily = UrduFontFamily,
                            fontSize = 12.sp,
                            color = TextMutedPurple
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Follow Button: "فالو کریں"
                Button(
                    onClick = {
                        viewModel.toggleFollowAuthor(author.id)
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFollowing) MidnightDarkSurfaceElevated else GoldAccent,
                        contentColor = if (isFollowing) GoldAccent else Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.55f)
                        .height(44.dp)
                        .border(1.dp, GoldAccent, RoundedCornerShape(24.dp))
                        .testTag("author_follow_button")
                ) {
                    Text(
                        text = if (isFollowing) "فالو شدہ ✓" else "فالو کریں",
                        fontFamily = UrduFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }

        // 3. مختصر سوانح (Brief Biography)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A2440))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "مختصر سوانح",
                        fontFamily = UrduFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = GoldAccent
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = author.bio.ifEmpty {
                            "اردو ادب کی معروف اور ہر دلعزیز مصنفہ جن کے ناولوں نے معاشرتی حقیقتوں اور روحانی سفر کو شاندار انداز میں پیش کیا ہے۔"
                        },
                        fontFamily = UrduFontFamily,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Start
                    )
                }
            }
        }

        // 4. مقبول ترین ناول (Most Popular Novels by Author)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
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
            val displayNovels = if (authorNovels.isNotEmpty()) authorNovels else viewModel.allNovels.value.take(4)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(displayNovels, key = { it.id }) { novel ->
                    NovelVerticalCard(
                        novel = novel,
                        onClick = { viewModel.navigateToDetail(novel.id) }
                    )
                }
            }
        }

        // 5. سبھی ناولز (All Novels by this Author)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "دیگر تصانیف",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }

        item {
            val allOtherNovels = viewModel.allNovels.value.filter { it.author == author.name }
            val listToShow = if (allOtherNovels.isNotEmpty()) allOtherNovels else viewModel.allNovels.value.drop(2).take(4)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(listToShow, key = { it.id }) { novel ->
                    NovelVerticalCard(
                        novel = novel,
                        onClick = { viewModel.navigateToDetail(novel.id) }
                    )
                }
            }
        }
    }
}
