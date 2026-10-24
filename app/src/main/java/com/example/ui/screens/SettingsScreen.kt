package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import android.content.Intent
import android.net.Uri
import com.example.data.model.Novel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.MidnightDarkSurface
import com.example.ui.theme.MidnightDarkSurfaceElevated
import com.example.ui.theme.TextMutedPurple
import com.example.ui.theme.UrduFontFamily
import com.example.ui.viewmodel.NovelViewModel

/**
 * Screen 3: سیٹنگز اور پیج (App Settings)
 * Fully includes Supabase Cloud Database & Sync configuration alongside
 * the luxury Dark Midnight Purple & Gold design system.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: NovelViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var darkModeEnabled by remember { mutableStateOf(true) }
    var fontPrefEnabled by remember { mutableStateOf(true) }
    val notificationsEnabled by viewModel.repository.notificationsEnabled.collectAsState()
    val supabaseConfig by viewModel.supabaseConfig.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()

    val userName by viewModel.userName.collectAsState()
    val userTitle by viewModel.userTitle.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()
    val dailyGoalMinutes by viewModel.dailyGoalMinutes.collectAsState()
    val novels by viewModel.novels.collectAsState()

    var showSupabaseSheet by remember { mutableStateOf(false) }
    var showFullPublisherSheet by remember { mutableStateOf(false) }
    var showNewNovelUploadDialog by remember { mutableStateOf(false) }
    var showAccountDialog by remember { mutableStateOf(false) }
    var showAudioDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // -------------------------------------------------------------
        // 1. TOP HEADER: "سیٹنگز" in Gold Nastaliq
        // -------------------------------------------------------------
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "سیٹنگز",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = GoldAccent
                )
            }
        }

        // -------------------------------------------------------------
        // 2. SETTINGS LIST ITEMS (MATCHING SCREEN 3 + SUPABASE)
        // -------------------------------------------------------------
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. اکاؤنٹ سیٹنگز (Account Settings)
                SettingsRowCard(
                    titleUrdu = "اکاؤنٹ سیٹنگز",
                    subtitleEnglish = "Account Settings",
                    icon = Icons.Default.Person,
                    onClick = { showAccountDialog = true },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = null,
                            tint = TextMutedPurple,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )

                // 2. SUPABASE کلاؤڈ کنفیگریشن و سنک (Supabase Cloud Config & Sync)
                SettingsRowCard(
                    titleUrdu = "Supabase کلاؤڈ و ڈیٹا اپ لوڈ",
                    subtitleEnglish = if (supabaseConfig.isConnected) "منسلک ہے (Cloud Active & Ready)" else "کلاؤڈ ڈیٹا بیس و سنک ترتیبات",
                    icon = if (supabaseConfig.isConnected) Icons.Default.CloudDone else Icons.Default.CloudSync,
                    onClick = { showSupabaseSheet = true },
                    trailingContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = if (supabaseConfig.isConnected) Color(0xFF1B5E20).copy(alpha = 0.35f) else Color(0xFF422730),
                                border = BorderStroke(1.dp, if (supabaseConfig.isConnected) GoldAccent else Color(0xFF8C3848))
                            ) {
                                Text(
                                    text = if (supabaseConfig.isConnected) "Active" else "Setup",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (supabaseConfig.isConnected) GoldAccent else Color(0xFFFF8A80),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = null,
                                tint = TextMutedPurple,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                )

                // 2b. ناول پبلشر و اپ لوڈر (Novel Publisher & Cloud Uploader)
                SettingsRowCard(
                    titleUrdu = "ناول پبلشر و کلاؤڈ اپ لوڈر",
                    subtitleEnglish = "Write Novels, Add Chapters & Push to Supabase",
                    icon = Icons.Default.CloudUpload,
                    onClick = { showFullPublisherSheet = true },
                    trailingContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = GoldAccent.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, GoldAccent)
                            ) {
                                Text(
                                    text = "Upload",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = null,
                                tint = TextMutedPurple,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                )

                // 3. ڈارک موڈ / تھیم (Dark Mode / Theme) with Switch
                SettingsRowCard(
                    titleUrdu = "ڈارک موڈ / تھیم",
                    subtitleEnglish = "Dark Mode / Theme",
                    icon = Icons.Default.DarkMode,
                    onClick = { darkModeEnabled = !darkModeEnabled },
                    trailingContent = {
                        Switch(
                            checked = darkModeEnabled,
                            onCheckedChange = { darkModeEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = GoldAccent
                            )
                        )
                    }
                )

                // 4. فونٹ اور ڈسپلے (Font & Display Preferences)
                SettingsRowCard(
                    titleUrdu = "فونٹ اور ڈسپلے",
                    subtitleEnglish = "Font & Display Preferences",
                    icon = Icons.Default.TextFields,
                    onClick = { fontPrefEnabled = !fontPrefEnabled },
                    trailingContent = {
                        Switch(
                            checked = fontPrefEnabled,
                            onCheckedChange = { fontPrefEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = GoldAccent
                            )
                        )
                    }
                )

                // 5. ڈاؤن لوڈ منیجر (Offline Download Quality / Manager)
                SettingsRowCard(
                    titleUrdu = "ڈاؤن لوڈ منیجر",
                    subtitleEnglish = "Offline Download Quality / Manager",
                    icon = Icons.Default.CloudDownload,
                    onClick = { viewModel.selectTab(3) },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = null,
                            tint = TextMutedPurple,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )

                // 6. اطلاعات (نوٹیفیکیشنز) (Notification Toggle)
                SettingsRowCard(
                    titleUrdu = "اطلاعات (نوٹیفیکیشنز)",
                    subtitleEnglish = "Notification Toggle",
                    icon = Icons.Default.Notifications,
                    onClick = { viewModel.repository.toggleNotifications() },
                    trailingContent = {
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { viewModel.repository.toggleNotifications() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = GoldAccent
                            )
                        )
                    }
                )

                // 7. آڈیو اور اسپیچ (Audio & Text-to-Speech Settings)
                SettingsRowCard(
                    titleUrdu = "آڈیو اور اسپیچ",
                    subtitleEnglish = "Audio & Text-to-Speech Settings",
                    icon = Icons.Default.Headphones,
                    onClick = { showAudioDialog = true },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = null,
                            tint = TextMutedPurple,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )

                // 8. مدد اور رابطہ (Help & Support)
                SettingsRowCard(
                    titleUrdu = "مدد اور رابطہ",
                    subtitleEnglish = "Help & Support",
                    icon = Icons.AutoMirrored.Filled.Help,
                    onClick = {
                        try {
                            val mailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:support@daastan.app")
                                putExtra(Intent.EXTRA_SUBJECT, "داستان ایپ سپورٹ اور فیڈ بیک")
                            }
                            context.startActivity(mailIntent)
                        } catch (e: Exception) {
                            viewModel.showMessage("رابطہ ای میل: support@daastan.app")
                        }
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = null,
                            tint = TextMutedPurple,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )

                // 9. لاگ آؤٹ (Log Out)
                SettingsRowCard(
                    titleUrdu = "لاگ آؤٹ",
                    subtitleEnglish = "Log Out",
                    icon = Icons.Default.ExitToApp,
                    onClick = { showLogoutDialog = true },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = null,
                            tint = TextMutedPurple,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 10. بنیادی نمونہ ناولز بحال کریں (Restore Sample Data)
                Button(
                    onClick = { viewModel.restoreSampleData() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .border(1.dp, GoldAccent.copy(alpha = 0.6f), RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MidnightDarkSurface,
                        contentColor = GoldAccent
                    )
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "بنیادی نمونہ ناولز بحال کریں (Restore Data)",
                        fontFamily = UrduFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // -----------------------------------------------------------------
    // SUPABASE CONFIGURATION MODAL BOTTOM SHEET
    // -----------------------------------------------------------------
    if (showSupabaseSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var inputUrl by remember { mutableStateOf(supabaseConfig.url) }
        var inputKey by remember { mutableStateOf(supabaseConfig.anonKey) }

        ModalBottomSheet(
            onDismissRequest = { showSupabaseSheet = false },
            sheetState = sheetState,
            containerColor = MidnightDarkSurface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState())
                    .testTag("supabase_config_sheet"),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Supabase کلاؤڈ کنفیگریشن",
                        fontFamily = UrduFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = GoldAccent
                    )

                    Surface(
                        shape = CircleShape,
                        color = if (supabaseConfig.isConnected) Color(0xFF1B5E20).copy(alpha = 0.4f) else Color(0xFF422730),
                        border = BorderStroke(1.dp, if (supabaseConfig.isConnected) GoldAccent else Color(0xFF8C3848))
                    ) {
                        Text(
                            text = if (supabaseConfig.isConnected) "منسلک ہے ✓" else "غیر منسلک",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (supabaseConfig.isConnected) GoldAccent else Color(0xFFFF8A80),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Status Message Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MidnightDarkSurfaceElevated),
                    border = BorderStroke(1.dp, Color(0xFF382F54))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (supabaseConfig.isConnected) Icons.Default.CloudDone else Icons.Default.CloudOff,
                                contentDescription = null,
                                tint = if (supabaseConfig.isConnected) GoldAccent else Color(0xFFFF8A80),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "حالت: ${supabaseConfig.lastSyncMessage}",
                                fontFamily = UrduFontFamily,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Input: Supabase Project URL
                OutlinedTextField(
                    value = inputUrl,
                    onValueChange = { inputUrl = it },
                    label = { Text("Supabase Project URL", color = TextMutedPurple, fontSize = 12.sp) },
                    placeholder = { Text("https://your-project.supabase.co", color = TextMutedPurple.copy(alpha = 0.5f), fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = Color(0xFF382F54),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = GoldAccent
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Input: Supabase Anon Key
                OutlinedTextField(
                    value = inputKey,
                    onValueChange = { inputKey = it },
                    label = { Text("Supabase Anon API Key", color = TextMutedPurple, fontSize = 12.sp) },
                    placeholder = { Text("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", color = TextMutedPurple.copy(alpha = 0.5f), fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = Color(0xFF382F54),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = GoldAccent
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Button: Save & Test Connection
                Button(
                    onClick = {
                        viewModel.saveSupabaseSettings(inputUrl.trim(), inputKey.trim())
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldAccent,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "محفوظ کریں اور کنکشن ٹیسٹ کریں",
                        fontFamily = UrduFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Button: Sync from Supabase Now
                OutlinedButton(
                    onClick = {
                        viewModel.syncFromSupabase()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, GoldAccent),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldAccent)
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = GoldAccent
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "کلاؤڈ سے ہم آہنگ ہو رہا ہے...", fontFamily = UrduFontFamily, fontSize = 13.sp)
                    } else {
                        Icon(imageVector = Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "کلاؤڈ سے ناولز سنک کریں (Sync Down)", fontFamily = UrduFontFamily, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // -------------------------------------------------------------
                // SECTION: ناول کا ڈیٹا کلاؤڈ پر اپ لوڈ کریں (Novel Data Cloud Upload)
                // -------------------------------------------------------------
                HorizontalDivider(color = Color(0xFF382F54), thickness = 1.dp)

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "ناول کا ڈیٹا کلاؤڈ پر اپ لوڈ کریں",
                            fontFamily = UrduFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = GoldAccent
                        )
                        Text(
                            text = "Upload Novels & Chapters to Supabase",
                            fontSize = 11.sp,
                            color = TextMutedPurple
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action 1: Upload All Novels to Supabase
                Button(
                    onClick = {
                        viewModel.pushAllNovelsToCloud()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6B3BA7),
                        contentColor = Color.White
                    ),
                    enabled = !isSyncing
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "کلاؤڈ پر اپ لوڈ ہو رہا ہے...", fontFamily = UrduFontFamily, fontSize = 13.sp)
                    } else {
                        Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "کلاؤڈ پر تمام ناولز اپ لوڈ کریں (Push All Novels)",
                            fontFamily = UrduFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action 2: Add New Novel and Upload
                Button(
                    onClick = {
                        showNewNovelUploadDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldAccent,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "+ نیا ناول شامل کریں اور اپ لوڈ کریں",
                        fontFamily = UrduFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action 3: Open Full Publisher Studio
                OutlinedButton(
                    onClick = {
                        showSupabaseSheet = false
                        showFullPublisherSheet = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.7f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldAccent)
                ) {
                    Icon(imageVector = Icons.Default.PostAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "مکمل ناول پبلشر اسٹوڈیو کھولیں (Publisher Studio)",
                        fontFamily = UrduFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action 4: Individual Novel Push list
                if (novels.isNotEmpty()) {
                    Text(
                        text = "موجودہ ناولز کلاؤڈ پر بھیجیں:",
                        fontFamily = UrduFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextMutedPurple,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    novels.take(6).forEach { novel ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MidnightDarkSurfaceElevated),
                            border = BorderStroke(1.dp, Color(0xFF382F54))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = novel.title,
                                        fontFamily = UrduFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${novel.author} • ${novel.totalChapters} ابواب",
                                        fontFamily = UrduFontFamily,
                                        fontSize = 11.sp,
                                        color = TextMutedPurple
                                    )
                                }

                                Button(
                                    onClick = {
                                        viewModel.pushNovelToCloud(novel.id)
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = GoldAccent.copy(alpha = 0.2f),
                                        contentColor = GoldAccent
                                    ),
                                    border = BorderStroke(1.dp, GoldAccent),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "کلاؤڈ پش",
                                        fontFamily = UrduFontFamily,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // -----------------------------------------------------------------
    // 0. NEW NOVEL UPLOAD DIALOG (Direct Cloud & Local Publish)
    // -----------------------------------------------------------------
    if (showNewNovelUploadDialog) {
        var novelTitle by remember { mutableStateOf("") }
        var novelAuthor by remember { mutableStateOf(userName) }
        var novelCategory by remember { mutableStateOf("رومانوی") }
        var novelSynopsis by remember { mutableStateOf("") }
        var initialChapTitle by remember { mutableStateOf("باب اول") }
        var initialChapContent by remember { mutableStateOf("") }
        var coverUrl by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showNewNovelUploadDialog = false },
            containerColor = MidnightDarkSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "نیا ناول شامل کریں اور کلاؤڈ پر اپ لوڈ کریں",
                        fontFamily = UrduFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = GoldAccent
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "ناول کا ڈیٹا درج کریں۔ محفوظ ہونے پر یہ خودکار طور پر لوکل ڈیٹا بیس میں محفوظ اور Supabase کلاؤڈ پر اپ لوڈ ہو جائے گا۔",
                        fontFamily = UrduFontFamily,
                        fontSize = 12.sp,
                        color = TextMutedPurple
                    )

                    OutlinedTextField(
                        value = novelTitle,
                        onValueChange = { novelTitle = it },
                        label = { Text("ناول کا عنوان *", fontFamily = UrduFontFamily, color = TextMutedPurple) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = Color(0xFF382F58),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = novelAuthor,
                        onValueChange = { novelAuthor = it },
                        label = { Text("مصنف کا نام *", fontFamily = UrduFontFamily, color = TextMutedPurple) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = Color(0xFF382F58),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = novelCategory,
                        onValueChange = { novelCategory = it },
                        label = { Text("صنف (رومانوی، سسپنس، سماجی، تاریخی)", fontFamily = UrduFontFamily, color = TextMutedPurple) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = Color(0xFF382F58),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = novelSynopsis,
                        onValueChange = { novelSynopsis = it },
                        label = { Text("مختصر تعارف / خلاصہ", fontFamily = UrduFontFamily, color = TextMutedPurple) },
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = Color(0xFF382F58),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = initialChapTitle,
                        onValueChange = { initialChapTitle = it },
                        label = { Text("پہلے باب کا عنوان", fontFamily = UrduFontFamily, color = TextMutedPurple) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = Color(0xFF382F58),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = initialChapContent,
                        onValueChange = { initialChapContent = it },
                        label = { Text("پہلے باب کا متن / تحریر *", fontFamily = UrduFontFamily, color = TextMutedPurple) },
                        minLines = 4,
                        maxLines = 8,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = Color(0xFF382F58),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = coverUrl,
                        onValueChange = { coverUrl = it },
                        label = { Text("سرورق تصویر کا لنک (اختیاری)", fontFamily = UrduFontFamily, color = TextMutedPurple) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = Color(0xFF382F58),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (novelTitle.isBlank() || novelAuthor.isBlank() || initialChapContent.isBlank()) {
                            viewModel.showMessage("براہ کرم عنوان، مصنف اور باب کا متن درج کریں")
                            return@Button
                        }
                        viewModel.publishNewNovel(
                            title = novelTitle,
                            author = novelAuthor,
                            category = novelCategory.ifBlank { "عام" },
                            synopsis = novelSynopsis,
                            initialChapterTitle = initialChapTitle.ifBlank { "باب اول" },
                            initialChapterContent = initialChapContent,
                            publicationStatus = "published",
                            coverImageUrl = coverUrl.ifBlank { null },
                            onSuccess = { createdNovel ->
                                viewModel.pushNovelToCloud(createdNovel.id)
                                showNewNovelUploadDialog = false
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black)
                ) {
                    Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("کلاؤڈ پر اپ لوڈ کریں", fontFamily = UrduFontFamily, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewNovelUploadDialog = false }) {
                    Text("منسوخ", fontFamily = UrduFontFamily, color = TextMutedPurple)
                }
            }
        )
    }

    // -----------------------------------------------------------------
    // FULL PUBLISHER STUDIO SHEET
    // -----------------------------------------------------------------
    if (showFullPublisherSheet) {
        val pubSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showFullPublisherSheet = false },
            sheetState = pubSheetState,
            containerColor = MidnightDarkSurface,
            tonalElevation = 8.dp
        ) {
            PublisherScreen(viewModel = viewModel)
        }
    }

    // -----------------------------------------------------------------
    // 1. ACCOUNT SETTINGS DIALOG
    // -----------------------------------------------------------------
    if (showAccountDialog) {
        var editName by remember { mutableStateOf(userName) }
        var editTitle by remember { mutableStateOf(userTitle) }
        var editEmail by remember { mutableStateOf(userEmail) }
        var editGoal by remember { mutableStateOf(dailyGoalMinutes.toString()) }

        AlertDialog(
            onDismissRequest = { showAccountDialog = false },
            containerColor = MidnightDarkSurface,
            title = {
                Text(
                    text = "اکاؤنٹ اور پروفائل کی ترتیبات",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = GoldAccent
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("نام", fontFamily = UrduFontFamily, color = TextMutedPurple) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = Color(0xFF382F58),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("لقب / اسٹیٹس", fontFamily = UrduFontFamily, color = TextMutedPurple) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = Color(0xFF382F58),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("ای میل پتہ", fontFamily = UrduFontFamily, color = TextMutedPurple) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = Color(0xFF382F58),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editGoal,
                        onValueChange = { editGoal = it.filter { ch -> ch.isDigit() } },
                        label = { Text("روزانہ مطالعہ کا ہدف (منٹ)", fontFamily = UrduFontFamily, color = TextMutedPurple) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = Color(0xFF382F58),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateProfile(
                            name = editName.ifBlank { "قاری" },
                            title = editTitle.ifBlank { "ادبی شوقین" },
                            email = editEmail.ifBlank { "reader@daastan.app" }
                        )
                        val goal = editGoal.toIntOrNull() ?: 30
                        viewModel.setDailyGoalMinutes(goal)
                        showAccountDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldAccent,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("محفوظ کریں", fontFamily = UrduFontFamily, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAccountDialog = false }) {
                    Text("منسوخ", fontFamily = UrduFontFamily, color = TextMutedPurple)
                }
            }
        )
    }

    // -----------------------------------------------------------------
    // 2. AUDIO & TEXT-TO-SPEECH SETTINGS DIALOG
    // -----------------------------------------------------------------
    if (showAudioDialog) {
        AlertDialog(
            onDismissRequest = { showAudioDialog = false },
            containerColor = MidnightDarkSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Headphones, contentDescription = null, tint = GoldAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "آڈیو اور اسپیچ کی ترتیبات",
                        fontFamily = UrduFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = GoldAccent
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MidnightDarkSurfaceElevated),
                        border = BorderStroke(1.dp, Color(0xFF382F58))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "اردو ٹیکسٹ ٹو اسپیچ (Android Urdu TTS)",
                                fontFamily = UrduFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "ریڈر اسکرین پر پلے بٹن دبانے سے ناول کا موجودہ صفحہ اردو تلفظ کے ساتھ پڑھا جاتا ہے۔ رفتار اور خودکار صفحہ پلٹنے کی رفتار بھی حسبِ منشا سیٹ کی جا سکتی ہے۔",
                                fontFamily = UrduFontFamily,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("زبان:", fontFamily = UrduFontFamily, color = TextMutedPurple, fontSize = 13.sp)
                        Text("اردو (پاکستان - ur_PK)", fontFamily = UrduFontFamily, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("آٹو اسکرول اسپیڈ:", fontFamily = UrduFontFamily, color = TextMutedPurple, fontSize = 13.sp)
                        Text("${viewModel.readerSettings.value.autoScrollSpeed}x", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.showMessage("آڈیو سروس فعال ہے")
                        showAudioDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("ٹھیک ہے", fontFamily = UrduFontFamily, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // -----------------------------------------------------------------
    // 3. LOGOUT CONFIRMATION DIALOG
    // -----------------------------------------------------------------
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = MidnightDarkSurface,
            title = {
                Text(
                    text = "لاگ آؤٹ کی تصدیق",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = GoldAccent
                )
            },
            text = {
                Text(
                    text = "کیا آپ واقعی اپنے اکاؤنٹ سے لاگ آؤٹ کرنا چاہتے ہیں؟ آپ کا آف لائن ڈیٹا اور نوولز محفوظ رہیں گے۔",
                    fontFamily = UrduFontFamily,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.showMessage("کامیابی سے لاگ آؤٹ کر دیا گیا")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB71C1C),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("ہاں، لاگ آؤٹ کریں", fontFamily = UrduFontFamily, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("منسوخ", fontFamily = UrduFontFamily, color = TextMutedPurple)
                }
            }
        )
    }
}

/**
 * Standard Dark Purple card row for Settings matching Screen 3
 */
@Composable
private fun SettingsRowCard(
    titleUrdu: String,
    subtitleEnglish: String,
    icon: ImageVector,
    onClick: () -> Unit,
    trailingContent: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightDarkSurface),
        border = BorderStroke(1.dp, Color(0xFF2C2448))
    ) {
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
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MidnightDarkSurfaceElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = titleUrdu,
                        fontFamily = UrduFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Text(
                        text = subtitleEnglish,
                        fontSize = 11.sp,
                        color = TextMutedPurple
                    )
                }
            }

            trailingContent()
        }
    }
}
