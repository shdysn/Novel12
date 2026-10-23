package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Bookmark
import com.example.data.model.FontStyleType
import com.example.ui.components.ReaderSettingsSheet
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldDark
import com.example.ui.theme.UrduFontFamily
import com.example.ui.viewmodel.NovelViewModel

/**
 * Premium Royal Profile & Reader Studio Screen.
 * Designed with authentic Urdu typography, real-time literary statistics,
 * interactive goal tracking, VIP membership perks, and robust dialog confirmations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: NovelViewModel,
    onOpenPublisher: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Live App & Profile State
    val userName by viewModel.userName.collectAsState()
    val userTitle by viewModel.userTitle.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()
    val dailyGoalMinutes by viewModel.dailyGoalMinutes.collectAsState()
    val isVipMember by viewModel.isVipMember.collectAsState()

    val readerSettings by viewModel.readerSettings.collectAsState()
    val notificationsEnabled by viewModel.repository.notificationsEnabled.collectAsState()
    val wifiOnlyDownload by viewModel.repository.wifiOnlyDownload.collectAsState()
    val supabaseConfig by viewModel.supabaseConfig.collectAsState()

    // Real dynamic counts from database
    val allNovels by viewModel.allNovels.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val continueReadingNovels by viewModel.continueReadingNovels.collectAsState()

    val favoritesCount = remember(allNovels) { allNovels.count { it.isFavorite } }
    val readingCount = remember(continueReadingNovels) { continueReadingNovels.size }
    val completedCount = remember(allNovels) { allNovels.count { it.readingProgress >= 0.99f } }
    val offlineCount = remember(allNovels) { allNovels.count { it.isDownloaded } }

    // Gamified Level & Experience based on actual reading progress
    val totalProgressSum = remember(allNovels) { allNovels.sumOf { (it.readingProgress * 100).toInt() } }
    val readerPoints = remember(totalProgressSum, bookmarks.size, completedCount) {
        totalProgressSum + (bookmarks.size * 15) + (completedCount * 100)
    }

    val (currentRank, rankTitle, progressToNextRank) = remember(readerPoints, completedCount) {
        when {
            readerPoints >= 400 || completedCount >= 4 -> Triple("درجہ ۵", "عظیم محقق و ناقدِ ادب", 1.0f)
            readerPoints >= 250 || completedCount >= 2 -> Triple("درجہ ۴", "شاہکار قاری (Master Reader)", ((readerPoints - 250) / 150f).coerceIn(0.1f, 1f))
            readerPoints >= 120 -> Triple("درجہ ۳", "ادبی شناسا (Connoisseur)", ((readerPoints - 120) / 130f).coerceIn(0.1f, 1f))
            readerPoints >= 40 -> Triple("درجہ ۲", "شائقِ مطالعہ (Avid Reader)", ((readerPoints - 40) / 80f).coerceIn(0.1f, 1f))
            else -> Triple("درجہ ۱", "نو آموز قاری (Novice Reader)", (readerPoints / 40f).coerceIn(0.1f, 1f))
        }
    }

    // Modal & Dialog Visibility States
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showAllBookmarksSheet by remember { mutableStateOf(false) }
    var showAppearanceSheet by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // -------------------------------------------------------------
        // 1. HERO HEADER: Ornate Royal Emerald & Gold Member Card
        // -------------------------------------------------------------
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF041B13),
                                Color(0xFF0C3829),
                                EmeraldDark,
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top Bar Actions (Edit Profile + Share App)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0x33000000),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f)),
                            modifier = Modifier.clickable { showEditProfileDialog = true }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Profile",
                                    tint = GoldAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ترمیم پروفائل",
                                    fontFamily = UrduFontFamily,
                                    fontSize = 11.sp,
                                    color = Color.White
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "اردو ادب اور شاہکار ناولز کا سب سے معیاری ریڈر \"داستانِ نو\"۔ اب نستعلیق خطاطی اور آف لائن مطالعہ کے ساتھ آزمائیں!"
                                    )
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "شیئر کریں"))
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0x33000000))
                                .size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Ornate Avatar with Golden Ring
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.sweepGradient(
                                    listOf(
                                        GoldAccent,
                                        Color(0xFFFFF59D),
                                        GoldDark,
                                        GoldAccent
                                    )
                                )
                            )
                            .padding(3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color(0xFF08271C)),
                            contentAlignment = Alignment.Center
                        ) {
                            val initial = userName.trim().firstOrNull()?.toString() ?: "ش"
                            Text(
                                text = initial,
                                fontFamily = UrduFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 42.sp,
                                color = GoldAccent,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // User Name & Literary Pen Name
                    Text(
                        text = userName,
                        fontFamily = UrduFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = userTitle,
                        fontFamily = UrduFontFamily,
                        fontSize = 13.sp,
                        color = GoldAccent,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Text(
                        text = userEmail,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // VIP Golden Reader Crest Badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0x33D4AF37),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "گولڈ وی آئی پی ممبر • تا حیات",
                                fontFamily = UrduFontFamily,
                                fontSize = 11.sp,
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Gamified Literary Rank & XP Bar
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0x2E000000)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = GoldAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "$currentRank: $rankTitle",
                                        fontFamily = UrduFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = "$readerPoints پوائنٹس",
                                    fontFamily = UrduFontFamily,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            LinearProgressIndicator(
                                progress = { progressToNextRank },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = GoldAccent,
                                trackColor = Color.White.copy(alpha = 0.15f)
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "اگلے ادبی درجے کے لیے مزید کتب اور ابواب کا مطالعہ کریں",
                                fontFamily = UrduFontFamily,
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // 2. REAL-TIME ACTIVITY & LIBRARY STATS GRID
        // -------------------------------------------------------------
        item {
            ProfileSectionHeader(
                title = "مطالعہ کا احاطہ و لائبریری اعدادوشمار",
                subtitle = "حقیقی وقت کے اعدادوشمار پر مبنی ریکارڈ"
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 1: زیرِ مطالعہ (In Progress)
                MetricTile(
                    title = "زیرِ مطالعہ",
                    value = "$readingCount ناولز",
                    subtitle = "مطالعہ جاری رکھیں",
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    accentColor = EmeraldPrimary,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.selectTab(3) }
                )

                // Card 2: پسندیدہ (Favorites)
                MetricTile(
                    title = "پسندیدہ",
                    value = "$favoritesCount ناولز",
                    subtitle = "پسندیدہ مجموعہ",
                    icon = Icons.Default.Favorite,
                    accentColor = Color(0xFFE53935),
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.selectTab(3) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 3: بک مارکس (Bookmarks)
                MetricTile(
                    title = "بک مارکس",
                    value = "${bookmarks.size} محفوظ",
                    subtitle = "اہم صفحات و اقتباسات",
                    icon = Icons.Default.Bookmark,
                    accentColor = GoldAccent,
                    modifier = Modifier.weight(1f),
                    onClick = { showAllBookmarksSheet = true }
                )

                // Card 4: آف لائن کتب (Offline Cached)
                MetricTile(
                    title = "آف لائن کتب",
                    value = "$offlineCount مکمل",
                    subtitle = "بغیر انٹرنیٹ دستیاب",
                    icon = Icons.Default.CloudDownload,
                    accentColor = Color(0xFF00897B),
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.selectTab(3) }
                )
            }
        }

        // -------------------------------------------------------------
        // 3. VIP GOLD PASS MEMBERSHIP PRIVILEGES SHOWCASE
        // -------------------------------------------------------------
        item {
            ProfileSectionHeader(
                title = "وی آئی پی ممبرشپ مراعات (VIP Benefits)",
                subtitle = "پریمیم ممبر کے لیے مخصوص خصوصی سہولیات"
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E170A)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent.copy(alpha = 0.85f))
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(GoldAccent.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WorkspacePremium,
                                        contentDescription = null,
                                        tint = GoldAccent,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "داستانِ نو — شاہکار وی آئی پی پاس",
                                        fontFamily = UrduFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = GoldAccent
                                    )
                                    Text(
                                        text = "تمام ناولز اور کتب بغیر کسی پابندی کے فعال ہیں",
                                        fontFamily = UrduFontFamily,
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1B5E20)
                            ) {
                                Text(
                                    text = "فعال ہے",
                                    fontFamily = UrduFontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = GoldAccent.copy(alpha = 0.25f))
                        Spacer(modifier = Modifier.height(12.dp))

                        VipFeatureRow("مکمل و نایاب ناولز تک بلا روک ٹوک رسائی")
                        VipFeatureRow("جمیل نوری نستعلیق اور مہر نستعلیق خطاطی سپورٹ")
                        VipFeatureRow("مکمل کتب کی لامحدود آف لائن ڈاؤنلوڈنگ")
                        VipFeatureRow("اشتہارات اور تجارتی اعلانات سے مکمل پاک تجربہ")
                        VipFeatureRow("کلاؤڈ سنک اور خودکار بک مارک بیک اپ")
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // 4. DAILY READING GOAL TRACKER
        // -------------------------------------------------------------
        item {
            ProfileSectionHeader(
                title = "روزانہ مطالعہ کا ہدف (Daily Goal)",
                subtitle = "مطالعہ کا تسلسل قائم رکھنے کے لیے ہدف مقرر کریں"
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "روزانہ مطالعہ کا ہدف",
                                    fontFamily = UrduFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "آج کا تجویز کردہ ہدف: $dailyGoalMinutes منٹ",
                                    fontFamily = UrduFontFamily,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "$dailyGoalMinutes منٹ",
                                fontFamily = UrduFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = EmeraldPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Preset Goal Chips (15, 30, 45, 60 minutes)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(15, 30, 45, 60).forEach { mins ->
                            val isSelected = dailyGoalMinutes == mins
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setDailyGoalMinutes(mins) },
                                label = {
                                    Text(
                                        text = "$mins منٹ",
                                        fontFamily = UrduFontFamily,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldPrimary,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = "✦ \"کتابیں انسان کی بہترین اور وفادار ترین دوست ہیں۔\" — مطالعہ کا روزانہ ایک گھنٹہ آپ کی فکر کو نئی وسعتیں بخشتا ہے۔",
                            fontFamily = UrduFontFamily,
                            fontSize = 11.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // 5. READING PREFERENCES & TYPOGRAPHY SETTINGS
        // -------------------------------------------------------------
        item {
            ProfileSectionHeader(
                title = "قراءت و فونٹ کی ترتیبات (Reading Settings)",
                subtitle = "رسم الخط، فونٹ سائز اور تھیم کی ذاتی ترجیحات"
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column {
                    ProfileMenuRow(
                        icon = Icons.Default.FontDownload,
                        title = "اردو رسم الخط (Nastaliq Calligraphy)",
                        subtitle = when (readerSettings.fontStyle) {
                            FontStyleType.NASTALEEQ -> "نوری نستعلیق (خالص اردو خطاطی)"
                            FontStyleType.MEHR_NASTALEEQ -> "مہر نستعلیق (جدید خطاطی)"
                            FontStyleType.NASKH -> "امیری نسخ (عربی و اردو نسخ)"
                        },
                        onClick = { showAppearanceSheet = true }
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                    ProfileMenuRow(
                        icon = Icons.Default.DarkMode,
                        title = "مطالعہ کی تھیم اور فونٹ سائز",
                        subtitle = "${readerSettings.theme.titleUrdu}  •  سائز: ${readerSettings.fontSizeSp.toInt()} pt  •  مارجن: ${readerSettings.horizontalMarginDp} dp",
                        onClick = { showAppearanceSheet = true }
                    )
                }
            }
        }

        // -------------------------------------------------------------
        // 6. APP & SYSTEM PREFERENCES (NOTIFICATIONS, WIFI, RESET)
        // -------------------------------------------------------------
        item {
            ProfileSectionHeader(
                title = "ایپلی کیشن کنٹرولز (App Controls)",
                subtitle = "اطلاعات اور نیٹ ورک کی بچت کی ترتیبات"
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column {
                    // Notifications Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = EmeraldPrimary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "نئے ابواب کے الرٹس (Notifications)",
                                    fontFamily = UrduFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "جب نیا باب کلاؤڈ پر شائع ہو تو مطلع کریں",
                                    fontFamily = UrduFontFamily,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { viewModel.repository.setNotificationsEnabled(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary)
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                    // Wi-Fi Only Download Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Wifi, contentDescription = null, tint = EmeraldPrimary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "صرف وائی فائی پر ڈاؤن لوڈ (Wi-Fi Only)",
                                    fontFamily = UrduFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "موبائل سم ڈیٹا کی غیر ضروری کھپت سے بچیں",
                                    fontFamily = UrduFontFamily,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                        Switch(
                            checked = wifiOnlyDownload,
                            onCheckedChange = { viewModel.repository.setWifiOnlyDownload(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary)
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                    // Safe History Reset (With Confirmation Dialog!)
                    ProfileMenuRow(
                        icon = Icons.Default.DeleteOutline,
                        iconTint = MaterialTheme.colorScheme.error,
                        title = "مطالعہ اور تلاش کی تاریخ صاف کریں",
                        subtitle = "ریڈنگ پوزیشنز اور سرچ ہسٹری ری سیٹ کرنے کے لیے",
                        onClick = { showClearHistoryDialog = true }
                    )
                }
            }
        }

        // -------------------------------------------------------------
        // 7. AUTHOR & PUBLISHER STUDIO SHORTCUT
        // -------------------------------------------------------------
        item {
            ProfileSectionHeader(
                title = "مصنفین و پبلشر اسٹوڈیو (Author Studio)",
                subtitle = "نیا ناول تحریر کریں یا Supabase کلاؤڈ ہم آہنگ کریں"
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable(onClick = onOpenPublisher),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "ناول پبلشر و کلاؤڈ سنک اسٹوڈیو",
                                fontFamily = UrduFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = if (supabaseConfig.isConnected) "Supabase کلاؤڈ منسلک ہے (فعال)" else "لوکل ڈیٹا بیس اور آف لائن ایڈمن موڈ فعال ہے",
                                fontFamily = UrduFontFamily,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Open Studio",
                        tint = EmeraldPrimary
                    )
                }
            }
        }

        // -------------------------------------------------------------
        // 8. ABOUT APP & PRIVACY POLICY
        // -------------------------------------------------------------
        item {
            ProfileSectionHeader(
                title = "معلومات و قانونی پالیسی (About & Legal)",
                subtitle = "داستانِ نو پلیٹ فارم اور حقوقِ اشاعت کی تفصیلات"
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column {
                    ProfileMenuRow(
                        icon = Icons.Default.Info,
                        title = "ایپ کے بارے میں (About Dastan-e-Nau)",
                        subtitle = "ورژن 1.0  •  خالص یونیکوڈ ڈیجیٹل اردو ادب",
                        onClick = { showAboutDialog = true }
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                    ProfileMenuRow(
                        icon = Icons.Default.PrivacyTip,
                        title = "پرائیویسی پالیسی و سیکیورٹی",
                        subtitle = "آپ کے کوائف اور بک مارکس کے تحفظ کی ضمانت",
                        onClick = { showPrivacyDialog = true }
                    )
                }
            }
        }
    }

    // =============================================================
    // DIALOGS & BOTTOM SHEETS
    // =============================================================

    // 1. Edit Profile Dialog
    if (showEditProfileDialog) {
        var tempName by remember { mutableStateOf(userName) }
        var tempTitle by remember { mutableStateOf(userTitle) }
        var tempEmail by remember { mutableStateOf(userEmail) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = GoldAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "پروفائل معلومات میں ترمیم",
                        fontFamily = UrduFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "اپنا نام، قلمی لقب اور ای میل درج کریں۔ یہ تفصیلات آپ کے پروفائل کارڈ پر ظاہر ہوں گی۔",
                        fontFamily = UrduFontFamily,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("قاری کا نام", fontFamily = UrduFontFamily) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = tempTitle,
                        onValueChange = { tempTitle = it },
                        label = { Text("ادبی لقب / قلمی نام", fontFamily = UrduFontFamily) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = tempEmail,
                        onValueChange = { tempEmail = it },
                        label = { Text("ای میل پتہ", fontFamily = UrduFontFamily) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateProfile(tempName, tempTitle, tempEmail)
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("محفوظ کریں", fontFamily = UrduFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("منسوخ", fontFamily = UrduFontFamily)
                }
            }
        )
    }

    // 2. Safe Clear History Confirmation Dialog
    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "مطالعہ کا ریکارڈ صاف کرنے کی تصدیق",
                        fontFamily = UrduFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                Text(
                    text = "کیا آپ واقعی تمام ناولز کی ریڈنگ پروگریس، بک مارکس اور سرچ ہسٹری ری سیٹ کرنا چاہتے ہیں؟ یہ عمل واپس نہیں کیا جا سکے گا۔",
                    fontFamily = UrduFontFamily,
                    fontSize = 13.sp,
                    lineHeight = 22.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearReadingHistory()
                        viewModel.clearRecentSearches()
                        showClearHistoryDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("ہاں، ریکارڈ صاف کریں", fontFamily = UrduFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("منسوخ", fontFamily = UrduFontFamily)
                }
            }
        )
    }

    // 3. All Bookmarks Dedicated Sheet
    if (showAllBookmarksSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
        ModalBottomSheet(
            onDismissRequest = { showAllBookmarksSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Bookmark, contentDescription = null, tint = GoldAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تمام محفوظ شدہ بک مارکس (${bookmarks.size})",
                            fontFamily = UrduFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    }
                    Text(
                        text = "ایک کلک پر پڑھیں",
                        fontFamily = UrduFontFamily,
                        fontSize = 11.sp,
                        color = EmeraldPrimary
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (bookmarks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "کوئی بک مارک محفوظ نہیں ہے",
                                fontFamily = UrduFontFamily,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(bookmarks, key = { it.id }) { bm ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        showAllBookmarksSheet = false
                                        viewModel.navigateToReader(bm.novelId, bm.chapterId, bm.charOffset)
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = bm.novelTitle,
                                            fontFamily = UrduFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = EmeraldPrimary
                                        )
                                        Text(
                                            text = "${bm.chapterTitle}  •  صفحہ ${bm.pageNumber}",
                                            fontFamily = UrduFontFamily,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        if (bm.note.isNotBlank()) {
                                            Text(
                                                text = "نوٹ: ${bm.note}",
                                                fontFamily = UrduFontFamily,
                                                fontSize = 11.sp,
                                                color = GoldAccent
                                            )
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = { viewModel.deleteBookmark(bm.id) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(16.dp)
                                            )
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
                        }
                    }
                }
            }
        }
    }

    // 4. Appearance Settings Sheet
    if (showAppearanceSheet) {
        ReaderSettingsSheet(
            settings = readerSettings,
            onSettingsChanged = { viewModel.updateReaderSettings(it) },
            onDismiss = { showAppearanceSheet = false }
        )
    }

    // 5. About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = EmeraldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "داستانِ نو — اردو ناولز ریڈر",
                        fontFamily = UrduFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "داستانِ نو اردو زبان اور کلاسیک لٹریچر کو جدید ترین ڈیجیٹل تقاضوں سے ہم آہنگ کرنے والا ایک پروقار پلیٹ فارم ہے۔",
                        fontFamily = UrduFontFamily,
                        fontSize = 13.sp,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "خصوصیات:\n• 100٪ خالص یونیکوڈ متن (تصاویر کے بجائے اصلی ٹیکسٹ)\n• پیج بائی پیج بک ریڈنگ انجن\n• جمیل نوری و مہر نستعلیق فونٹس\n• مکمل آف لائن سپورٹ بغیر انٹرنیٹ",
                        fontFamily = UrduFontFamily,
                        fontSize = 12.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAboutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("ٹھیک ہے", fontFamily = UrduFontFamily)
                }
            }
        )
    }

    // 6. Privacy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.PrivacyTip, contentDescription = null, tint = EmeraldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "پرائیویسی پالیسی اور ڈیٹا کا تحفظ",
                        fontFamily = UrduFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
            },
            text = {
                Text(
                    text = "آپ کا مطالعہ کا ریکارڈ، بک مارکس اور ترجیحات مکمل طور پر آپ کے ڈیوائس پر نجی اور محفوظ ہیں۔ ایپ کوئی غیر ضروری معلومات جمع نہیں کرتی۔ جب آپ خود Supabase کلاؤڈ سنک فعال کرتے ہیں تو صرف کتابیں اور پبلش کردہ ابواب کلاؤڈ پر ہم آہنگ ہوتے ہیں۔",
                    fontFamily = UrduFontFamily,
                    fontSize = 13.sp,
                    lineHeight = 22.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showPrivacyDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("سمجھ گیا", fontFamily = UrduFontFamily)
                }
            }
        )
    }
}

// =============================================================
// REUSABLE STAT & MENU COMPONENTS
// =============================================================

@Composable
private fun ProfileSectionHeader(title: String, subtitle: String? = null) {
    Column(
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 8.dp)
    ) {
        Text(
            text = title,
            fontFamily = UrduFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                fontFamily = UrduFontFamily,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun MetricTile(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                fontFamily = UrduFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = title,
                fontFamily = UrduFontFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = accentColor
            )

            Text(
                text = subtitle,
                fontFamily = UrduFontFamily,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.outline,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun VipFeatureRow(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 3.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = GoldAccent,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontFamily = UrduFontFamily,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}

@Composable
private fun ProfileMenuRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color = EmeraldPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontFamily = UrduFontFamily,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline
        )
    }
}
