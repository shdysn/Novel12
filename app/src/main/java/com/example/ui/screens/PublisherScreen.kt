package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Novel
import com.example.data.model.NovelCategory
import com.example.data.supabase.TypeScriptGuide
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldDark
import com.example.ui.theme.UrduFontFamily
import com.example.ui.viewmodel.NovelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublisherScreen(
    viewModel: NovelViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val supabaseConfig by viewModel.supabaseConfig.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val allNovels by viewModel.allNovels.collectAsState()
    val authors by viewModel.authors.collectAsState()

    var showConfigDialog by remember { mutableStateOf(false) }
    var showCreateNovelDialog by remember { mutableStateOf(false) }
    var showAddChapterDialog by remember { mutableStateOf(false) }
    var showTypeScriptDialog by remember { mutableStateOf(false) }
    var editingNovel by remember { mutableStateOf<Novel?>(null) }
    var deletingNovel by remember { mutableStateOf<Novel?>(null) }
    var coverUploadNovel by remember { mutableStateOf<Novel?>(null) }
    var selectedNovelForChapter by remember { mutableStateOf<Novel?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("publisher_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Publisher Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EmeraldDark)
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ناول پبلشر و ایڈمن اسٹوڈیو (Admin Panel)",
                                fontFamily = UrduFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "Supabase کلاؤڈ سنک، ڈرافٹ / اشاعت، اور آف لائن کیشے مینجمنٹ",
                            fontFamily = UrduFontFamily,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    IconButton(
                        onClick = { showConfigDialog = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Supabase Settings",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // Supabase Connection Status Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .testTag("supabase_status_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (supabaseConfig.isConnected) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (supabaseConfig.isConnected) Icons.Default.CloudDone else Icons.Default.CloudOff,
                                contentDescription = "Status",
                                tint = if (supabaseConfig.isConnected) EmeraldPrimary else MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (supabaseConfig.isConnected) "Supabase کلاؤڈ کنکشن فعال ہے" else "Supabase غیر منسلک ہے (آف لائن لوکل موڈ فعال)",
                                fontFamily = UrduFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = EmeraldPrimary
                            )
                        }
                    }

                    Text(
                        text = if (supabaseConfig.url.isNotBlank()) "پروجیکٹ URL: ${supabaseConfig.url}" else "Supabase URL اور Anon Key درج کریں تاکہ کلاؤڈ سنک فعال ہو سکے",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Text(
                        text = "حیثیت: ${supabaseConfig.lastSyncMessage}",
                        fontFamily = UrduFontFamily,
                        fontSize = 12.sp,
                        color = if (supabaseConfig.isConnected) EmeraldPrimary else MaterialTheme.colorScheme.outline
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.testSupabaseConnection() },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Link, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("کنکشن ٹیسٹ", fontFamily = UrduFontFamily, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { viewModel.syncFromSupabase() },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("کلاؤڈ سے سنک", fontFamily = UrduFontFamily, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Action Buttons Row (Create Novel, Add Chapter, TypeScript Guide)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "ایڈمن و تصنیف کنٹرولز",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showCreateNovelDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("publish_new_novel_button")
                    ) {
                        Icon(imageVector = Icons.Default.PostAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("نیا ناول شامل کریں", fontFamily = UrduFontFamily, fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            selectedNovelForChapter = allNovels.firstOrNull()
                            showAddChapterDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldDark),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("add_chapter_button")
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("نیا باب شامل کریں", fontFamily = UrduFontFamily, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Developer SQL & TypeScript Guide button
                OutlinedButton(
                    onClick = { showTypeScriptDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("view_typescript_code_button")
                ) {
                    Icon(imageVector = Icons.Default.Code, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Supabase SQL اسکیما، RLS پالیسیز اور اسکرپٹس",
                        fontFamily = UrduFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Section: Novels Management List
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "تمام رجسٹرڈ ناولز (${allNovels.size})",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "ڈرافٹ اور شائع شدہ",
                    fontFamily = UrduFontFamily,
                    fontSize = 12.sp,
                    color = EmeraldPrimary
                )
            }
        }

        items(allNovels, key = { it.id }) { novel ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = novel.title,
                                    fontFamily = UrduFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (novel.isDraft) Color(0xFFFFF3E0) else Color(0xFFE8F5E9)
                                ) {
                                    Text(
                                        text = if (novel.isDraft) "ڈرافٹ" else "شائع شدہ",
                                        fontFamily = UrduFontFamily,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (novel.isDraft) Color(0xFFE65100) else EmeraldPrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "مصنف: ${novel.author}  •  ${novel.category}  •  ${novel.totalChapters} ابواب",
                                fontFamily = UrduFontFamily,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        // Status Badge: Local vs Cloud
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Text(
                                text = if (novel.isDownloaded) "آف لائن کیشے" else "کلاؤڈ پر دستیاب",
                                fontFamily = UrduFontFamily,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Admin Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Edit metadata button
                        OutlinedButton(
                            onClick = { editingNovel = novel },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ترمیم", fontFamily = UrduFontFamily, fontSize = 11.sp)
                        }

                        // Upload cover button
                        OutlinedButton(
                            onClick = { coverUploadNovel = novel },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("سرورق", fontFamily = UrduFontFamily, fontSize = 11.sp)
                        }

                        // Push to Cloud
                        Button(
                            onClick = { viewModel.pushNovelToCloud(novel.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.weight(1.2f).testTag("push_novel_${novel.id}")
                        ) {
                            Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("کلاؤڈ پش", fontFamily = UrduFontFamily, fontSize = 11.sp)
                        }

                        // Delete
                        IconButton(
                            onClick = { deletingNovel = novel },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Novel",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Supabase Settings Dialog
    if (showConfigDialog) {
        var tempUrl by remember { mutableStateOf(supabaseConfig.url) }
        var tempKey by remember { mutableStateOf(supabaseConfig.anonKey) }

        AlertDialog(
            onDismissRequest = { showConfigDialog = false },
            title = {
                Text(
                    text = "Supabase کی ترتیبات درج کریں",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "اپنے Supabase پراجیکٹ کا URL اور anon/public کی درج کریں۔ سیکیورٹی کی خاطر service_role کی ایپ میں کبھی درج نہ کریں۔",
                        fontFamily = UrduFontFamily,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = tempUrl,
                        onValueChange = { tempUrl = it },
                        label = { Text("Supabase URL (e.g. https://xyz.supabase.co)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = tempKey,
                        onValueChange = { tempKey = it },
                        label = { Text("Supabase Anon Key (Public Key)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveSupabaseSettings(tempUrl, tempKey)
                        showConfigDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("محفوظ کریں", fontFamily = UrduFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfigDialog = false }) {
                    Text("منسوخ", fontFamily = UrduFontFamily)
                }
            }
        )
    }

    // Create New Novel Dialog
    if (showCreateNovelDialog) {
        CreateNovelDialog(
            onDismiss = { showCreateNovelDialog = false },
            onPublish = { title, author, category, synopsis, chTitle, chContent, publicationStatus, coverUrl ->
                viewModel.publishNewNovel(
                    title = title,
                    author = author,
                    category = category,
                    synopsis = synopsis,
                    initialChapterTitle = chTitle,
                    initialChapterContent = chContent,
                    publicationStatus = publicationStatus,
                    coverImageUrl = coverUrl,
                    onSuccess = { showCreateNovelDialog = false }
                )
            }
        )
    }

    // Edit Novel Metadata Dialog
    if (editingNovel != null) {
        EditNovelDialog(
            novel = editingNovel!!,
            onDismiss = { editingNovel = null },
            onSave = { novelId, title, author, category, synopsis, publicationStatus, coverUrl ->
                viewModel.updateNovelMetadata(
                    novelId = novelId,
                    title = title,
                    author = author,
                    category = category,
                    synopsis = synopsis,
                    publicationStatus = publicationStatus,
                    coverImageUrl = coverUrl,
                    onSuccess = { editingNovel = null }
                )
            }
        )
    }

    // Upload Cover Dialog
    if (coverUploadNovel != null) {
        UploadCoverDialog(
            novel = coverUploadNovel!!,
            onDismiss = { coverUploadNovel = null },
            onUpload = { imageBytes, mimeType ->
                viewModel.uploadCoverImage(coverUploadNovel!!.id, imageBytes, mimeType)
                coverUploadNovel = null
            }
        )
    }

    // Delete Confirmation Dialog
    if (deletingNovel != null) {
        AlertDialog(
            onDismissRequest = { deletingNovel = null },
            title = {
                Text(
                    text = "ناول حذف کرنے کی تصدیق",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "کیا آپ واقعی ناول \"${deletingNovel!!.title}\" اور اس کے تمام ابواب کو لوکل ڈیٹابیس اور کلاؤڈ سے مستقل طور پر حذف کرنا چاہتے ہیں؟",
                    fontFamily = UrduFontFamily,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val id = deletingNovel!!.id
                        deletingNovel = null
                        viewModel.deleteNovel(id) {}
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("ہاں، حذف کریں", fontFamily = UrduFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingNovel = null }) {
                    Text("منسوخ", fontFamily = UrduFontFamily)
                }
            }
        )
    }

    // Add Chapter Dialog
    if (showAddChapterDialog && allNovels.isNotEmpty()) {
        AddChapterDialog(
            novels = allNovels,
            initialSelectedNovel = selectedNovelForChapter ?: allNovels.first(),
            onDismiss = { showAddChapterDialog = false },
            onAdd = { novelId, chTitle, content, pubStatus ->
                viewModel.addNewChapter(
                    novelId = novelId,
                    chapterTitle = chTitle,
                    content = content,
                    publicationStatus = pubStatus,
                    onSuccess = { showAddChapterDialog = false }
                )
            }
        )
    }

    // TypeScript Scripts & SQL Guide Dialog
    if (showTypeScriptDialog) {
        TypeScriptGuideDialog(
            onDismiss = { showTypeScriptDialog = false },
            onCopyCode = { code, title ->
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(title, code)
                clipboard.setPrimaryClip(clip)
                viewModel.showMessage("$title کلپ بورڈ پر کاپی ہو گیا!")
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateNovelDialog(
    onDismiss: () -> Unit,
    onPublish: (String, String, String, String, String, String, String, String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("سماجی و اصلاحی") }
    var synopsis by remember { mutableStateOf("") }
    var chTitle by remember { mutableStateOf("باب اول") }
    var chContent by remember { mutableStateOf("") }
    var publicationStatus by remember { mutableStateOf("published") } // "published" vs "draft"
    var coverUrl by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "نیا اردو ناول تحریر و شائع کریں",
                fontFamily = UrduFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Draft vs Published toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = publicationStatus == "published",
                        onClick = { publicationStatus = "published" },
                        label = { Text("شائع کریں (Published)", fontFamily = UrduFontFamily, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = EmeraldPrimary)
                    )
                    FilterChip(
                        selected = publicationStatus == "draft",
                        onClick = { publicationStatus = "draft" },
                        label = { Text("ڈرافٹ کے طور پر محفوظ (Draft)", fontFamily = UrduFontFamily, fontSize = 11.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("ناول کا اردو عنوان *", fontFamily = UrduFontFamily) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("مصنف کا نام *", fontFamily = UrduFontFamily) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("صنف (رومانوی، سماجی، روحانی، تاریخی)", fontFamily = UrduFontFamily) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = synopsis,
                    onValueChange = { synopsis = it },
                    label = { Text("ناول کا خلاصہ", fontFamily = UrduFontFamily) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = coverUrl,
                    onValueChange = { coverUrl = it },
                    label = { Text("سرورق تصویر کا URL (اختیاری)", fontFamily = UrduFontFamily) },
                    placeholder = { Text("https://...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "پہلا باب (First Chapter):",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = chTitle,
                    onValueChange = { chTitle = it },
                    label = { Text("باب کا عنوان", fontFamily = UrduFontFamily) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = chContent,
                    onValueChange = { chContent = it },
                    label = { Text("باب کا یونیکوڈ اردو متن *", fontFamily = UrduFontFamily) },
                    placeholder = { Text("یہاں مکمل اردو متن پیسٹ یا ٹائپ کریں...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    maxLines = 6
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onPublish(
                        title,
                        author,
                        category,
                        synopsis,
                        chTitle,
                        chContent,
                        publicationStatus,
                        coverUrl.ifBlank { null }
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text(if (publicationStatus == "draft") "ڈرافٹ محفوظ کریں" else "شائع کریں", fontFamily = UrduFontFamily)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("منسوخ", fontFamily = UrduFontFamily)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditNovelDialog(
    novel: Novel,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String, String?) -> Unit
) {
    var title by remember { mutableStateOf(novel.title) }
    var author by remember { mutableStateOf(novel.author) }
    var category by remember { mutableStateOf(novel.category) }
    var synopsis by remember { mutableStateOf(novel.synopsis) }
    var publicationStatus by remember { mutableStateOf(novel.publicationStatus) }
    var coverUrl by remember { mutableStateOf(novel.coverImageUrl ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "ناول کی معلومات میں ترمیم",
                fontFamily = UrduFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = publicationStatus == "published",
                        onClick = { publicationStatus = "published" },
                        label = { Text("شائع شدہ", fontFamily = UrduFontFamily, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = EmeraldPrimary)
                    )
                    FilterChip(
                        selected = publicationStatus == "draft",
                        onClick = { publicationStatus = "draft" },
                        label = { Text("ڈرافٹ", fontFamily = UrduFontFamily, fontSize = 11.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان", fontFamily = UrduFontFamily) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("مصنف", fontFamily = UrduFontFamily) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("صنف", fontFamily = UrduFontFamily) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = synopsis,
                    onValueChange = { synopsis = it },
                    label = { Text("خلاصہ", fontFamily = UrduFontFamily) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = coverUrl,
                    onValueChange = { coverUrl = it },
                    label = { Text("سرورق تصویر کا URL", fontFamily = UrduFontFamily) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(novel.id, title, author, category, synopsis, publicationStatus, coverUrl.ifBlank { null }) },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("محفوظ کریں", fontFamily = UrduFontFamily)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("منسوخ", fontFamily = UrduFontFamily)
            }
        }
    )
}

@Composable
private fun UploadCoverDialog(
    novel: Novel,
    onDismiss: () -> Unit,
    onUpload: (ByteArray, String) -> Unit
) {
    var selectedSampleIndex by remember { mutableIntStateOf(0) }

    val sampleCovers = listOf(
        "کلاسک ادبی ڈیزائن (Classic Elegance)" to "image/jpeg",
        "طلوعِ سحر رومانوی سرورق (Golden Sunrise)" to "image/jpeg",
        "گہرے سبز نقوش (Emerald Patterns)" to "image/png"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "سرورق تصویر کا انتخاب (Supabase Storage)",
                fontFamily = UrduFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        },
        text = {
            Column {
                Text(
                    text = "ناول \"${novel.title}\" کے لیے سرورق منتخب کریں۔ تصویر کا سائز 5MB سے کم ہونا ضروری ہے۔",
                    fontFamily = UrduFontFamily,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                sampleCovers.forEachIndexed { index, (label, _) ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { selectedSampleIndex = index },
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedSampleIndex == index) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (selectedSampleIndex == index) Icons.Default.CheckCircle else Icons.Default.Image,
                                contentDescription = null,
                                tint = if (selectedSampleIndex == index) EmeraldPrimary else MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = label,
                                fontFamily = UrduFontFamily,
                                fontSize = 12.sp,
                                fontWeight = if (selectedSampleIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Generate placeholder bytes representing validated image
                    val dummyBytes = "URDU_NOVEL_COVER_IMAGE_DATA_BYTES".toByteArray()
                    onUpload(dummyBytes, sampleCovers[selectedSampleIndex].second)
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("اپ لوڈ کریں", fontFamily = UrduFontFamily)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("منسوخ", fontFamily = UrduFontFamily)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddChapterDialog(
    novels: List<Novel>,
    initialSelectedNovel: Novel,
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String) -> Unit
) {
    var selectedNovel by remember { mutableStateOf(initialSelectedNovel) }
    var chapterTitle by remember { mutableStateOf("") }
    var chapterContent by remember { mutableStateOf("") }
    var publicationStatus by remember { mutableStateOf("published") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "ناول میں نیا باب شامل کریں",
                fontFamily = UrduFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "منتخب ناول:",
                    fontFamily = UrduFontFamily,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedNovel.title,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor(androidx.compose.material3.MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        novels.forEach { novel ->
                            DropdownMenuItem(
                                text = { Text(novel.title, fontFamily = UrduFontFamily) },
                                onClick = {
                                    selectedNovel = novel
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = publicationStatus == "published",
                        onClick = { publicationStatus = "published" },
                        label = { Text("شائع کریں", fontFamily = UrduFontFamily, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = EmeraldPrimary)
                    )
                    FilterChip(
                        selected = publicationStatus == "draft",
                        onClick = { publicationStatus = "draft" },
                        label = { Text("ڈرافٹ", fontFamily = UrduFontFamily, fontSize = 11.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = chapterTitle,
                    onValueChange = { chapterTitle = it },
                    label = { Text("باب کا عنوان (مثلاً باب دوم)", fontFamily = UrduFontFamily) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = chapterContent,
                    onValueChange = { chapterContent = it },
                    label = { Text("باب کا مکمل اردو متن *", fontFamily = UrduFontFamily) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 5,
                    maxLines = 8
                )
                Text(
                    text = "الفاظ: ${if (chapterContent.isBlank()) 0 else chapterContent.trim().split(Regex("\\s+")).size}",
                    fontFamily = UrduFontFamily,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onAdd(selectedNovel.id, chapterTitle, chapterContent, publicationStatus) },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("شامل کریں", fontFamily = UrduFontFamily)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("منسوخ", fontFamily = UrduFontFamily)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TypeScriptGuideDialog(
    onDismiss: () -> Unit,
    onCopyCode: (String, String) -> Unit
) {
    var selectedCodeTab by remember { mutableStateOf(0) } // 0: SQL Schema, 1: Push Script, 2: Fetch Script

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Supabase SQL اسکیما اور کوڈ گائیڈ",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp)
            ) {
                PrimaryTabRow(
                    selectedTabIndex = selectedCodeTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Tab(
                        selected = selectedCodeTab == 0,
                        onClick = { selectedCodeTab = 0 },
                        text = { Text("SQL Schema", fontSize = 11.sp) }
                    )
                    Tab(
                        selected = selectedCodeTab == 1,
                        onClick = { selectedCodeTab = 1 },
                        text = { Text("Push Script", fontSize = 11.sp) }
                    )
                    Tab(
                        selected = selectedCodeTab == 2,
                        onClick = { selectedCodeTab = 2 },
                        text = { Text("Fetch Script", fontSize = 11.sp) }
                    )
                }

                val currentCode = when (selectedCodeTab) {
                    0 -> TypeScriptGuide.SUPABASE_SQL_SCHEMA
                    1 -> TypeScriptGuide.TYPESCRIPT_PUBLISH_SCRIPT
                    else -> TypeScriptGuide.TYPESCRIPT_FETCH_SCRIPT
                }

                val codeTitle = when (selectedCodeTab) {
                    0 -> "Supabase SQL Schema & RLS"
                    1 -> "TypeScript Push Script"
                    else -> "TypeScript Fetch Script"
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = { onCopyCode(currentCode, codeTitle) },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("کاپی کریں", fontFamily = UrduFontFamily, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1E1E1E),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(
                        text = currentCode,
                        color = Color(0xFFD4D4D4),
                        fontSize = 11.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        modifier = Modifier
                            .padding(10.dp)
                            .verticalScroll(rememberScrollState())
                            .horizontalScroll(rememberScrollState())
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("بند کریں", fontFamily = UrduFontFamily)
            }
        }
    )
}
