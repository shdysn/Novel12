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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FontStyleType
import com.example.data.model.ReaderTheme
import com.example.ui.components.ReaderSettingsSheet
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.NovelViewModel

@Composable
fun ProfileScreen(
    viewModel: NovelViewModel,
    onOpenPublisher: () -> Unit,
    modifier: Modifier = Modifier
) {
    val readerSettings by viewModel.readerSettings.collectAsState()
    val notificationsEnabled by viewModel.repository.notificationsEnabled.collectAsState()
    val wifiOnlyDownload by viewModel.repository.wifiOnlyDownload.collectAsState()

    var showAppearanceSheet by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // 1. Profile Header Card
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
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large Avatar
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF246D55))
                        .border(2.5.dp, GoldAccent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ش",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "شاہد ثاقب",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )

                Text(
                    text = "shahid.saaqib@gmail.com",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Premium Reader Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GoldAccent.copy(alpha = 0.25f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "پریمیئم قاری (Premium Member)",
                            fontFamily = FontFamily.Serif,
                            fontSize = 11.sp,
                            color = GoldAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Streak & Stats Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x22000000))
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "7 دن", fontWeight = FontWeight.Bold, color = GoldAccent, fontSize = 16.sp)
                        Text(text = "مسلسل مطالعہ", fontFamily = FontFamily.Serif, fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.White.copy(alpha = 0.2f)))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "42 گھنٹے", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                        Text(text = "مطالعہ کا وقت", fontFamily = FontFamily.Serif, fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.White.copy(alpha = 0.2f)))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "5 ناولز", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                        Text(text = "مکمل کتابیں", fontFamily = FontFamily.Serif, fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
        }

        // 2. Reading Preferences Section
        item {
            ProfileSectionTitle("قراءت کی ترتیبات (Reading Preferences)")
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
                    ProfileSettingItem(
                        icon = Icons.Default.FontDownload,
                        title = "اردو رسم الخط (Nastaliq / Naskh)",
                        subtitle = if (readerSettings.fontStyle == FontStyleType.NASTALEEQ) "جمیل نوری نستعلیق / مہر نستعلیق" else "جدید نسخ فونٹ",
                        onClick = { showAppearanceSheet = true }
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                    ProfileSettingItem(
                        icon = Icons.Default.DarkMode,
                        title = "پس منظر تھیم اور فونٹ سائز",
                        subtitle = "${readerSettings.theme.titleUrdu}  •  ${readerSettings.fontSizeSp.toInt()} pt",
                        onClick = { showAppearanceSheet = true }
                    )
                }
            }
        }

        // 3. App Settings (Notifications & Downloads)
        item {
            ProfileSectionTitle("ایپ ترتیبات (App Settings)")
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = EmeraldPrimary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "نئے ابواب کی اطلاعات (Notifications)", fontFamily = FontFamily.Serif, fontSize = 14.sp)
                                Text(text = "ناول کے نئے باب پر الرٹ موصول کریں", fontFamily = FontFamily.Serif, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { viewModel.repository.setNotificationsEnabled(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary)
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Wifi, contentDescription = null, tint = EmeraldPrimary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "صرف وائی فائی پر ڈاؤن لوڈ", fontFamily = FontFamily.Serif, fontSize = 14.sp)
                                Text(text = "موبائل ڈیٹا کی بچت کریں", fontFamily = FontFamily.Serif, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                        Switch(
                            checked = wifiOnlyDownload,
                            onCheckedChange = { viewModel.repository.setWifiOnlyDownload(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary)
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                    ProfileSettingItem(
                        icon = Icons.Default.DeleteOutline,
                        title = "مطالعہ اور تلاش کی تاریخ صاف کریں",
                        subtitle = "تمام محفوظ ہسٹری کو ری سیٹ کریں",
                        onClick = {
                            viewModel.clearReadingHistory()
                            viewModel.clearRecentSearches()
                        }
                    )
                }
            }
        }

        // 4. Publisher Studio Shortcut
        item {
            ProfileSectionTitle("ناول نگار و پبلشر (Author Studio)")
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, tint = EmeraldPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "پبلشر اسٹوڈیو اور کلاؤڈ سنک", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text(text = "نیا ناول تحریر کریں یا Supabase کلاؤڈ سے سنک کریں", fontFamily = FontFamily.Serif, fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                        }
                    }
                    Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = null, tint = EmeraldPrimary)
                }
            }
        }

        // 5. About & Privacy Policy
        item {
            ProfileSectionTitle("معلومات و شرائط (About & Privacy)")
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
                    ProfileSettingItem(
                        icon = Icons.Default.Info,
                        title = "ایپ کے بارے میں (About App)",
                        subtitle = "ورژن 1.0  •  خالص یونیکوڈ اردو لٹریچر پلیٹ فارم",
                        onClick = { showAboutDialog = true }
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                    ProfileSettingItem(
                        icon = Icons.Default.PrivacyTip,
                        title = "پرائیویسی پالیسی (Privacy Policy)",
                        subtitle = "آپ کے ڈیٹا اور کتب کے حقوق کی حفاظت",
                        onClick = { showPrivacyDialog = true }
                    )
                }
            }
        }
    }

    // Appearance Bottom Sheet
    if (showAppearanceSheet) {
        ReaderSettingsSheet(
            settings = readerSettings,
            onSettingsChanged = { viewModel.updateReaderSettings(it) },
            onDismiss = { showAppearanceSheet = false }
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Text(
                    text = "اردو ناولز ریڈر — داستانِ نو",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "یہ ایپلیکیشن اردو ناولز اور کلاسیک ادب کو اعلیٰ ترین ڈیجیٹل معیار پر پیش کرنے کے لیے ڈیزائن کی گئی ہے۔ اس میں تمام ناولز تصاویر کے بجائے 100٪ خالص یونیکوڈ متن میں دستیاب ہیں، جس کی بدولت آپ جمیل نوری نستعلیق خطاطی اور نائٹ موڈ کے ساتھ مطالعہ کا لطف اٹھا سکتے ہیں۔",
                    fontFamily = FontFamily.Serif,
                    fontSize = 13.sp,
                    lineHeight = 22.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showAboutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("ٹھیک ہے", fontFamily = FontFamily.Serif)
                }
            }
        )
    }

    // Privacy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = {
                Text(
                    text = "پرائیویسی پالیسی و حقوق",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "ہم آپ کے مطالعہ کی ترجیحات، بک مارکس اور ذاتی معلومات کا احترام کرتے ہیں۔ آپ کا تمام لوکل ڈیٹا مکمل طور پر محفوظ رہتا ہے۔ کلاؤڈ سنک فیچر صرف آپ کے منتخب کردہ ناولز کو Supabase پر پش کرنے کے لیے استعمال ہوتا ہے۔",
                    fontFamily = FontFamily.Serif,
                    fontSize = 13.sp,
                    lineHeight = 22.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showPrivacyDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("سمجھ گیا", fontFamily = FontFamily.Serif)
                }
            }
        )
    }
}

@Composable
private fun ProfileSectionTitle(title: String) {
    Text(
        text = title,
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp)
    )
}

@Composable
private fun ProfileSettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = EmeraldPrimary)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, fontFamily = FontFamily.Serif, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(text = subtitle, fontFamily = FontFamily.Serif, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
            }
        }
        Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
    }
}
