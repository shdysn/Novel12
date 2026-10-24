package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatAlignRight
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatAlignJustify
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FontStyleType
import com.example.data.model.ReaderSettings
import com.example.data.model.ReaderTheme
import com.example.data.model.TextAlignmentType
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.MidnightDarkSurface
import com.example.ui.theme.MidnightDarkSurfaceElevated
import com.example.ui.theme.TextMutedPurple
import com.example.ui.theme.UrduFontFamily

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/**
 * Screen 4: ٹیکنیکل ریڈر موڈ (Reader Overlay)
 * Strictly matches Screen 4 of the reference mockup in Dark Midnight Purple & Gold.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderSettingsSheet(
    settings: ReaderSettings,
    currentPageText: String = "",
    currentPageNumber: Int = 1,
    totalPages: Int = 1,
    onSettingsChanged: (ReaderSettings) -> Unit,
    onOpenBookmarks: () -> Unit = {},
    onPrevPage: () -> Unit = {},
    onNextPage: () -> Unit = {},
    onSaveBookmark: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val context = LocalContext.current
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    var isAudioPlaying by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        var localTts: TextToSpeech? = null
        localTts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val urduLocale = Locale("ur", "PK")
                val res = localTts?.setLanguage(urduLocale)
                if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                    localTts?.setLanguage(Locale.getDefault())
                }
            }
        }
        tts = localTts
        onDispose {
            localTts.stop()
            localTts.shutdown()
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            tts?.stop()
            onDismiss()
        },
        sheetState = sheetState,
        containerColor = MidnightDarkSurface,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .verticalScroll(rememberScrollState())
                .testTag("reader_settings_sheet"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ---------------------------------------------------------
            // 1. HEADER: "مطالعہ کی سیٹنگز"
            // ---------------------------------------------------------
            Text(
                text = "مطالعہ کی سیٹنگز",
                fontFamily = UrduFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = GoldAccent,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // ---------------------------------------------------------
            // 2. FONT SELECTION: "فونٹ کا انتخاب" (Nastaliq, Naskh, etc.)
            // ---------------------------------------------------------
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "فونٹ کا انتخاب (Font Adjustments)",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FontStyleType.entries.forEach { style ->
                        val isSelected = settings.fontStyle == style
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSettingsChanged(settings.copy(fontStyle = style)) },
                            label = {
                                Text(
                                    text = style.urduLabel.split(" ").firstOrNull() ?: "نستعلیق",
                                    fontFamily = UrduFontFamily,
                                    fontSize = 12.sp,
                                    color = if (isSelected) Color.Black else Color.White
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldAccent,
                                containerColor = MidnightDarkSurfaceElevated
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) GoldAccent else Color(0xFF2E2748)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ---------------------------------------------------------
            // 3. FONT SIZE & ALIGNMENT: "سائز"
            // ---------------------------------------------------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "سائز: ${(settings.fontSizeSp).toInt()} pt",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.White
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = { onSettingsChanged(settings.copy(alignment = TextAlignmentType.JUSTIFIED)) },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (settings.alignment == TextAlignmentType.JUSTIFIED) GoldAccent else MidnightDarkSurfaceElevated)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatAlignJustify,
                            contentDescription = "برابر",
                            tint = if (settings.alignment == TextAlignmentType.JUSTIFIED) Color.Black else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = { onSettingsChanged(settings.copy(alignment = TextAlignmentType.RIGHT_ALIGNED)) },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (settings.alignment == TextAlignmentType.RIGHT_ALIGNED) GoldAccent else MidnightDarkSurfaceElevated)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.FormatAlignRight,
                            contentDescription = "دائیں",
                            tint = if (settings.alignment == TextAlignmentType.RIGHT_ALIGNED) Color.Black else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Slider(
                value = settings.fontSizeSp,
                onValueChange = { onSettingsChanged(settings.copy(fontSizeSp = it)) },
                valueRange = 14f..36f,
                steps = 21,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = GoldAccent,
                    activeTrackColor = GoldAccent,
                    inactiveTrackColor = Color(0xFF2C2448)
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // ---------------------------------------------------------
            // 4. THEMES: Sepia, Night, Paper, Day, Dark
            // ---------------------------------------------------------
            Text(
                text = "رنگ (Theme)",
                fontFamily = UrduFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(6.dp))

            val allThemes = listOf(
                "Sepia" to ReaderTheme.SEPIA,
                "Night" to ReaderTheme.OLED,
                "Paper" to ReaderTheme.WARM_PAPER,
                "Day" to ReaderTheme.PURE_WHITE,
                "Dark" to ReaderTheme.DARK_MODE
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                allThemes.forEach { (label, theme) ->
                    val isSelected = settings.theme == theme
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSettingsChanged(settings.copy(theme = theme)) },
                        label = {
                            Text(
                                text = label,
                                fontFamily = UrduFontFamily,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp,
                                color = if (isSelected) Color.Black else Color.White
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldAccent,
                            containerColor = MidnightDarkSurfaceElevated
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) GoldAccent else Color(0xFF2E2748)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ---------------------------------------------------------
            // 5. AUTO SCROLL: "آٹو اسکرول" with Switch & Speed Slider
            // ---------------------------------------------------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "آٹو اسکرول (Auto Page Turn)",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.White
                )

                Switch(
                    checked = settings.autoScrollEnabled,
                    onCheckedChange = { onSettingsChanged(settings.copy(autoScrollEnabled = it)) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = GoldAccent
                    )
                )
            }

            if (settings.autoScrollEnabled) {
                Text(
                    text = "اسپیڈ: ${"%.1f".format(settings.autoScrollSpeed)}x (ہر ${((15f / settings.autoScrollSpeed.coerceAtLeast(0.5f)).toInt())} سیکنڈ بعد)",
                    fontFamily = UrduFontFamily,
                    fontSize = 11.sp,
                    color = TextMutedPurple,
                    modifier = Modifier.align(Alignment.Start)
                )
                Slider(
                    value = settings.autoScrollSpeed,
                    onValueChange = { onSettingsChanged(settings.copy(autoScrollSpeed = it)) },
                    valueRange = 0.5f..3.0f,
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = GoldAccent,
                        activeTrackColor = GoldAccent,
                        inactiveTrackColor = Color(0xFF2C2448)
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ---------------------------------------------------------
            // 6. BOOKMARK & NOTES BUTTONS
            // ---------------------------------------------------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        tts?.stop()
                        onDismiss()
                        onOpenBookmarks()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MidnightDarkSurfaceElevated,
                        contentColor = GoldAccent
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .border(1.dp, GoldAccent.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                ) {
                    Icon(imageVector = Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "نشانات اور نوٹس", fontFamily = UrduFontFamily, fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        tts?.stop()
                        onDismiss()
                        onOpenBookmarks()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MidnightDarkSurfaceElevated,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .border(1.dp, Color(0xFF2E2748), RoundedCornerShape(12.dp))
                ) {
                    Text(text = "Bookmark list", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Reading Progress Indicator (Dynamic percent based on actual pages)
            val dynamicPercent = if (totalPages > 0) ((currentPageNumber.toFloat() / totalPages) * 100).toInt().coerceIn(0, 100) else 0
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "مطالعہ کی پیشرفت (صفحہ $currentPageNumber از $totalPages)", fontFamily = UrduFontFamily, fontSize = 12.sp, color = TextMutedPurple)
                Text(text = "$dynamicPercent%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ---------------------------------------------------------
            // 7. AUDIO READING BAR: "آڈیو پڑھائی" (Real Android Urdu TTS)
            // ---------------------------------------------------------
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightDarkSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF382F58))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Headphones, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAudioPlaying) "آڈیو پڑھائی جاری ہے..." else "آڈیو پڑھائی (Text-to-Speech)",
                                fontFamily = UrduFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isAudioPlaying) GoldAccent else Color.White
                            )
                        }

                        IconButton(
                            onClick = {
                                tts?.stop()
                                isAudioPlaying = false
                                onDismiss()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "بند کریں", tint = TextMutedPurple, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Player Controls Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Loop / Replay from start of page
                        IconButton(onClick = {
                            if (tts != null && currentPageText.isNotBlank()) {
                                tts?.stop()
                                tts?.speak(currentPageText, TextToSpeech.QUEUE_FLUSH, null, "reader_tts")
                                isAudioPlaying = true
                            }
                        }) {
                            Icon(imageVector = Icons.Default.Repeat, contentDescription = "دوبارہ سنیں", tint = GoldAccent, modifier = Modifier.size(20.dp))
                        }

                        // Previous Page Audio
                        IconButton(onClick = {
                            tts?.stop()
                            isAudioPlaying = false
                            onPrevPage()
                        }) {
                            Icon(imageVector = Icons.Default.SkipPrevious, contentDescription = "پچھلا صفحہ", tint = Color.White, modifier = Modifier.size(22.dp))
                        }

                        // Play / Pause Gold Circle Button
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(GoldAccent)
                                .clickable {
                                    if (isAudioPlaying) {
                                        tts?.stop()
                                        isAudioPlaying = false
                                    } else {
                                        val textToRead = currentPageText.ifBlank { "کوئی متن دستیاب نہیں ہے" }
                                        tts?.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, "reader_tts")
                                        isAudioPlaying = true
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isAudioPlaying) "Pause" else "Play",
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Next Page Audio
                        IconButton(onClick = {
                            tts?.stop()
                            isAudioPlaying = false
                            onNextPage()
                        }) {
                            Icon(imageVector = Icons.Default.SkipNext, contentDescription = "اگلا صفحہ", tint = Color.White, modifier = Modifier.size(22.dp))
                        }

                        // Save Bookmark
                        IconButton(onClick = {
                            onSaveBookmark()
                        }) {
                            Icon(imageVector = Icons.Default.Bookmark, contentDescription = "محفوظ کریں", tint = GoldAccent, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
