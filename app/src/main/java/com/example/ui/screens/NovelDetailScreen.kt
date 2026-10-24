package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DownloadStatus
import com.example.data.model.Chapter
import com.example.data.model.Novel
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.MidnightDarkSurface
import com.example.ui.theme.MidnightDarkSurfaceElevated
import com.example.ui.theme.TextMutedPurple
import com.example.ui.theme.UrduFontFamily
import com.example.ui.viewmodel.NovelViewModel

/**
 * Screen 6: تفصیلات اور تبصرے (Book Details & Reviews)
 * Strictly matches Screen 6 of the reference mockup in Dark Midnight Purple & Gold.
 */
@Composable
fun NovelDetailScreen(
    novelId: String,
    viewModel: NovelViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current
    val novel = viewModel.getNovel(novelId)
    val chapters = viewModel.getChapters(novelId)
    val downloadStatuses by viewModel.downloadStatuses.collectAsState()

    if (novel == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Text("ناول نہیں ملا", fontFamily = UrduFontFamily, color = Color.White)
        }
        return
    }

    val currentStatus = downloadStatuses[novel.id] ?: novel.downloadStatus

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .testTag("novel_detail_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // -------------------------------------------------------------
        // 1. TOP BAR: Back button, Title "کتاب کی تفصیلات", Share & Favorite
        // -------------------------------------------------------------
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MidnightDarkSurfaceElevated)
                        .testTag("detail_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "واپس",
                        tint = GoldAccent
                    )
                }

                Text(
                    text = "کتاب کی تفصیلات",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = GoldAccent
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "اردو ناول \"${novel.title}\" از ${novel.author} پڑھیں!"
                                )
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "شیئر کریں"))
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MidnightDarkSurfaceElevated)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "شیئر",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { viewModel.toggleFavorite(novel.id) },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MidnightDarkSurfaceElevated)
                    ) {
                        Icon(
                            imageVector = if (novel.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "پسندیدہ",
                            tint = if (novel.isFavorite) Color(0xFFE53935) else GoldAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // 2. HERO BOOK CARD: Cover on Left, Title, Rating & Tags on Right
        // -------------------------------------------------------------
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C2448))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Novel Cover with Glowing Gold Border
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF221D38))
                            .border(2.dp, GoldAccent, RoundedCornerShape(12.dp))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text("✦ ✤ ✦", color = GoldAccent, fontSize = 9.sp)
                            Text(
                                text = novel.title,
                                fontFamily = UrduFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = GoldAccent,
                                textAlign = TextAlign.Center,
                                lineHeight = 17.sp,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = novel.author,
                                fontFamily = UrduFontFamily,
                                fontSize = 9.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Right: Title, Rating, and Category Tags
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = novel.title,
                            fontFamily = UrduFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = GoldAccent,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "مصنف: ${novel.author}",
                            fontFamily = UrduFontFamily,
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Star Rating
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            repeat(5) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "2.42  (242+ تبصرہ جات)",
                                fontSize = 11.sp,
                                color = TextMutedPurple
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Tags row: روایتی، نئے معاشرتی، امتحان کی، ایمان کی
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf(novel.category, "معاشرتی", "ایمان کی").take(3).forEach { tag ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MidnightDarkSurfaceElevated,
                                    border = androidx.compose.foundation.BorderStroke(0.8.dp, Color(0xFF382F58))
                                ) {
                                    Text(
                                        text = tag,
                                        fontFamily = UrduFontFamily,
                                        fontSize = 10.sp,
                                        color = TextMutedPurple,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // 3. خلا صہ (SYNOPSIS CARD)
        // -------------------------------------------------------------
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C2448))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "خلاصہ",
                        fontFamily = UrduFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = GoldAccent
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = novel.synopsis.ifEmpty {
                            "ایک سسپنس، رومانٹک اور سبق آموز کہانی جس میں انسانی جذبوں اور آزمائشوں کو شاندار انداز میں پیش کیا گیا ہے۔ یہ ناول زندگی، ضمیر اور محبت کے مختلف پہلوؤں کی عکاسی کرتا ہے۔"
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

        // -------------------------------------------------------------
        // 4. ACTION BUTTONS: "ابھی پڑھیں" & "آف لائن ڈاؤن لوڈ"
        // -------------------------------------------------------------
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Primary Gold CTA: "ابھی پڑھیں"
                Button(
                    onClick = { viewModel.navigateToReader(novel.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("detail_read_novel_button"),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldAccent,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ابھی پڑھیں",
                        fontFamily = UrduFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                // Secondary CTA: "آف لائن ڈاؤن لوڈ" (with MB stats)
                Button(
                    onClick = {
                        if (currentStatus == DownloadStatus.AVAILABLE_OFFLINE) {
                            viewModel.deleteDownloadedNovel(novel.id)
                        } else {
                            viewModel.downloadNovel(novel.id)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .border(1.dp, GoldAccent, RoundedCornerShape(24.dp))
                        .testTag("detail_download_button"),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MidnightDarkSurface,
                        contentColor = GoldAccent
                    )
                ) {
                    when (currentStatus) {
                        DownloadStatus.DOWNLOADING -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = GoldAccent
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "محفوظ ہو رہا ہے...",
                                fontFamily = UrduFontFamily,
                                fontSize = 13.sp
                            )
                        }
                        DownloadStatus.AVAILABLE_OFFLINE -> {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "آف لائن محفوظ شدہ (14.2 MB) — حذف کریں",
                                fontFamily = UrduFontFamily,
                                fontSize = 13.sp
                            )
                        }
                        else -> {
                            Icon(
                                imageVector = Icons.Default.CloudDownload,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "آف لائن ڈاؤن لوڈ (with 14.2 MB stats)",
                                fontFamily = UrduFontFamily,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // 5. تبصرے (REVIEWS & COMMENTS)
        // -------------------------------------------------------------
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "تبصرے",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }

        // Review Item 1: Goodread
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C2448))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF382F58)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("G", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Goodread",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Row {
                            repeat(4) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                            }
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "ایک بہت ہی عمدہ اور سبق آموز ناول، کہانی کی شروعات سے لے کر اختتام تک تجسس برقرار رہا۔ کردار نگاری قابلِ تعریف ہے۔",
                        fontFamily = UrduFontFamily,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Review Item 2: Cornhost
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C2448))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF382F58)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("C", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Cornhost",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Row {
                            repeat(5) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "منظر کشی اور مکالمے انتہائی جاندار تھے! کتاب کا اختتام دل کو چھو گیا۔ ہر کتاب دوست کو ضرور پڑھنی چاہیے۔",
                        fontFamily = UrduFontFamily,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // -------------------------------------------------------------
        // 6. ابواب کی فہرست (CHAPTERS LIST)
        // -------------------------------------------------------------
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ابواب کی فہرست",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )

                Text(
                    text = "${chapters.size} ابواب",
                    fontFamily = UrduFontFamily,
                    fontSize = 12.sp,
                    color = GoldAccent
                )
            }
        }

        items(chapters, key = { it.id }) { chapter ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 3.dp)
                    .clickable { viewModel.navigateToReader(novel.id, chapter.id) },
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF261E42))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "باب ${chapter.chapterNumber}: ${chapter.title}",
                        fontFamily = UrduFontFamily,
                        fontSize = 13.sp,
                        color = Color.White
                    )

                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "دستیاب",
                        tint = GoldAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
