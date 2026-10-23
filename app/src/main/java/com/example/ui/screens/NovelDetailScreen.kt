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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Chapter
import com.example.data.model.Novel
import com.example.ui.components.CoverGradients
import com.example.ui.components.NovelVerticalCard
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.NovelViewModel

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

    if (novel == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("ناول نہیں ملا", fontFamily = FontFamily.Serif)
        }
        return
    }

    val relatedNovels = viewModel.getRelatedNovels(novel)
    val gradientColors = CoverGradients[novel.coverGradientIndex % CoverGradients.size]

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("novel_detail_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // 1. Cover & Header Art Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(gradientColors[0], gradientColors[1], MaterialTheme.colorScheme.background)
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top Navigation Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.navigateBack() },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0x33000000))
                                .testTag("detail_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    val shareIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "میں اردو ناول \"${novel.title}\" از ${novel.author} پڑھ رہا ہوں! آپ بھی داستانِ نو ایپ پر مطالعہ کریں۔"
                                        )
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "شیئر کریں"))
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color(0x33000000))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = { viewModel.toggleFavorite(novel.id) },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color(0x33000000))
                            ) {
                                Icon(
                                    imageVector = if (novel.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (novel.isFavorite) Color(0xFFE53935) else Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Large Premium Novel Cover
                    Box(
                        modifier = Modifier
                            .width(148.dp)
                            .height(210.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.verticalGradient(gradientColors))
                            .border(2.dp, GoldAccent, RoundedCornerShape(16.dp))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(text = "✦ ✤ ✦", color = GoldAccent, fontSize = 11.sp)

                            Text(
                                text = novel.title,
                                color = Color.White,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 24.sp
                            )

                            Text(
                                text = novel.author,
                                color = Color.White.copy(alpha = 0.9f),
                                fontFamily = FontFamily.Serif,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Title & Author
                    Text(
                        text = novel.title,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "مصنف: ${novel.author}",
                        fontFamily = FontFamily.Serif,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Genre, Language, and Reading Status Badges
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = novel.category,
                                fontFamily = FontFamily.Serif,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = novel.language,
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (novel.readingProgress > 0f) Color(0xFF1B5E20).copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (novel.readingProgress > 0f) "زیرِ مطالعہ" else "غیر مطالعہ شدہ",
                                fontFamily = FontFamily.Serif,
                                fontSize = 11.sp,
                                color = if (novel.readingProgress > 0f) EmeraldPrimary else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        // Publication Status Badge (Published vs Draft)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (novel.isDraft) Color(0xFFE65100).copy(alpha = 0.15f) else Color(0xFF00695C).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (novel.isDraft) "ڈرافٹ (غیر شائع)" else "شائع شدہ",
                                fontFamily = FontFamily.Serif,
                                fontSize = 11.sp,
                                color = if (novel.isDraft) Color(0xFFE65100) else EmeraldPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Reading progress if previously opened
                    if (novel.readingProgress > 0f) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Column(modifier = Modifier.fillMaxWidth(0.85f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "مطالعہ کی پیشرفت",
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${(novel.readingProgress * 100).toInt()}%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { novel.readingProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = EmeraldPrimary,
                                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary and Secondary Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Primary Button: "ناول پڑھیں"
                        Button(
                            onClick = { viewModel.navigateToReader(novel.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("detail_read_novel_button")
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (novel.readingProgress > 0f) "پڑھنا جاری رکھیں" else "ناول پڑھیں",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        // Secondary Button: "بک مارک کریں"
                        OutlinedButton(
                            onClick = {
                                val firstChapter = chapters.firstOrNull()
                                if (firstChapter != null) {
                                    viewModel.saveBookmark(novel, firstChapter, novel.readingProgress)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("detail_bookmark_button")
                        ) {
                            Icon(imageVector = Icons.Default.BookmarkBorder, contentDescription = null, tint = EmeraldPrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "بک مارک کریں",
                                fontFamily = FontFamily.Serif,
                                fontSize = 12.sp,
                                color = EmeraldPrimary
                            )
                        }
                    }

                    // OFFLINE DOWNLOAD CONTROL SECTION
                    val downloadStatuses by viewModel.downloadStatuses.collectAsState()
                    val currentStatus = downloadStatuses[novel.id] ?: novel.downloadStatus

                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .testTag("novel_download_card"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when (currentStatus) {
                                com.example.data.local.DownloadStatus.AVAILABLE_OFFLINE -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                com.example.data.local.DownloadStatus.DOWNLOADING -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                                com.example.data.local.DownloadStatus.UPDATE_AVAILABLE -> GoldAccent.copy(alpha = 0.2f)
                                com.example.data.local.DownloadStatus.FAILED -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            when (currentStatus) {
                                com.example.data.local.DownloadStatus.DOWNLOADING -> {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        androidx.compose.material3.CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            strokeWidth = 2.dp,
                                            color = EmeraldPrimary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "آف لائن محفوظ ہو رہا ہے...",
                                            fontFamily = FontFamily.Serif,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                    androidx.compose.material3.TextButton(
                                        onClick = { viewModel.cancelDownload(novel.id) }
                                    ) {
                                        Text("منسوخ", fontFamily = FontFamily.Serif, fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                                    }
                                }
                                com.example.data.local.DownloadStatus.AVAILABLE_OFFLINE -> {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = EmeraldPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "آف لائن دستیاب (انٹرنیٹ کے بغیر پڑھیں)",
                                            fontFamily = FontFamily.Serif,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                    androidx.compose.material3.IconButton(
                                        onClick = { viewModel.deleteDownloadedNovel(novel.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remove Offline Cache",
                                            tint = MaterialTheme.colorScheme.outline,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                com.example.data.local.DownloadStatus.UPDATE_AVAILABLE -> {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CloudUpload,
                                            contentDescription = null,
                                            tint = GoldAccent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "نیا ورژن کلاؤڈ پر دستیاب ہے",
                                            fontFamily = FontFamily.Serif,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Button(
                                        onClick = { viewModel.downloadNovel(novel.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("اپ ڈیٹ", fontFamily = FontFamily.Serif, fontSize = 11.sp, color = Color.Black)
                                    }
                                }
                                com.example.data.local.DownloadStatus.FAILED -> {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "ڈاؤن لوڈ ناکام رہا",
                                            fontFamily = FontFamily.Serif,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                    Button(
                                        onClick = { viewModel.downloadNovel(novel.id) },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("دوبارہ کوشش", fontFamily = FontFamily.Serif, fontSize = 11.sp)
                                    }
                                }
                                com.example.data.local.DownloadStatus.NOT_DOWNLOADED -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.CloudDone,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "آف لائن پڑھنے کے لیے محفوظ کریں",
                                                fontFamily = FontFamily.Serif,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        OutlinedButton(
                                            onClick = { viewModel.downloadNovel(novel.id) },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                        ) {
                                            Text("ڈاؤن لوڈ", fontFamily = FontFamily.Serif, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Formatted Urdu Synopsis
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "خلاصۂ ناول (Synopsis)",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = novel.synopsis,
                        fontFamily = FontFamily.Serif,
                        fontSize = 14.sp,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        }

        // 3. Author Information Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "مصنف کے بارے میں",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(EmeraldDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = novel.author,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "اردو ادب کے معتبر ادیب اور کہانی نویس۔ متعدد شاہکار کتب کے مصنف۔",
                                fontFamily = FontFamily.Serif,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.outline,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }

        // 4. Chapters List Section
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "فہرستِ ابواب (${chapters.size})",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "مکمل یونیکوڈ اردو متن",
                    fontFamily = FontFamily.Serif,
                    fontSize = 12.sp,
                    color = EmeraldPrimary
                )
            }
        }

        items(chapters, key = { it.id }) { chapter ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
                    .clickable { viewModel.navigateToReader(novel.id, chapter.id) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(EmeraldDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${chapter.chapterNumber}",
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = chapter.title,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${chapter.readTimeMinutes} منٹ مطالعہ  •  ${chapter.wordCount} الفاظ",
                                fontFamily = FontFamily.Serif,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Read",
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 5. Related Novels Section ("اس سے ملتے جلتے ناولز")
        if (relatedNovels.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "اس سے ملتے جلتے ناولز (Related Novels)",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(relatedNovels, key = { it.id }) { rel ->
                        NovelVerticalCard(
                            novel = rel,
                            onClick = { viewModel.navigateToDetail(rel.id) }
                        )
                    }
                }
            }
        }
    }
}
