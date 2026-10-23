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
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FormatAlignJustify
import androidx.compose.material.icons.automirrored.filled.FormatAlignRight
import androidx.compose.material.icons.filled.FormatLineSpacing
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Margin
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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FontStyleType
import com.example.data.model.ReaderSettings
import com.example.data.model.ReaderTheme
import com.example.data.model.TextAlignmentType
import com.example.data.model.toUrduDigits
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.MehrNastaliqFontFamily
import com.example.ui.theme.NotoNastaliqUrduFontFamily
import com.example.ui.theme.UrduFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderSettingsSheet(
    settings: ReaderSettings,
    onSettingsChanged: (ReaderSettings) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val fontFam = com.example.ui.theme.getFontFamily(settings.fontStyle)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .verticalScroll(rememberScrollState())
                .testTag("reader_settings_sheet"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "کتابی صفحہ بندی اور قراءت کی ترتیبات",
                fontFamily = UrduFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            // Realistic Live Book Page Preview Card Resting on Desk Surface
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(settings.theme.deskColor)
                    .padding(6.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp, topEnd = 3.dp, bottomEnd = 3.dp),
                    color = settings.theme.backgroundColor,
                    border = if (settings.showBookFrame) androidx.compose.foundation.BorderStroke(0.8.dp, settings.theme.textColor.copy(alpha = 0.22f)) else null,
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                    // Preview Running Book Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "« پیرِ کامل »",
                            fontFamily = fontFam,
                            fontSize = 10.sp,
                            color = settings.theme.textColor.copy(alpha = 0.65f),
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(text = "❦", fontSize = 11.sp, color = GoldAccent)
                        Text(
                            text = "باب اول: آغاز",
                            fontFamily = fontFam,
                            fontSize = 10.sp,
                            color = settings.theme.textColor.copy(alpha = 0.65f)
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.75.dp)
                            .background(settings.theme.textColor.copy(alpha = 0.18f))
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Preview Book Text
                    Text(
                        text = "یہ اردو کتاب کے اصل ورق کا نمونہ ہے، جس پر اوپر کتاب کا عنوان اور نیچے صفحہ نمبر چھپا ہوتا ہے۔",
                        fontFamily = fontFam,
                        fontSize = (settings.fontSizeSp * 0.9f).sp,
                        lineHeight = ((settings.fontSizeSp * 0.9f) * settings.lineHeightMultiplier).sp,
                        color = settings.theme.textColor,
                        textAlign = if (settings.alignment == TextAlignmentType.JUSTIFIED) TextAlign.Justify else TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.75.dp)
                            .background(settings.theme.textColor.copy(alpha = 0.18f))
                    )
                    Spacer(modifier = Modifier.height(3.dp))

                    // Preview Running Book Folio / Page Number
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (settings.useUrduNumerals) "باب ۱ از ۱۰" else "باب 1 / 10",
                            fontFamily = fontFam,
                            fontSize = 9.sp,
                            color = settings.theme.textColor.copy(alpha = 0.45f)
                        )
                        Text(
                            text = if (settings.useUrduNumerals) "— ۱۲ —" else "— 12 —",
                            fontFamily = fontFam,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = settings.theme.textColor.copy(alpha = 0.85f)
                        )
                        Text(
                            text = if (settings.useUrduNumerals) "۱۲ / ۸۰" else "12 / 80",
                            fontFamily = fontFam,
                            fontSize = 9.sp,
                            color = settings.theme.textColor.copy(alpha = 0.45f)
                        )
                    }
                }
            }
        }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Reading Themes (4 Core Themes)
            Text(
                text = "مطالعہ کے پس منظر (Reading Themes)",
                fontFamily = UrduFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            val coreThemes = listOf(
                ReaderTheme.WARM_PAPER,
                ReaderTheme.SEPIA,
                ReaderTheme.DARK_MODE,
                ReaderTheme.PURE_WHITE
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                coreThemes.forEach { theme ->
                    val isSelected = settings.theme == theme
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onSettingsChanged(settings.copy(theme = theme)) }
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(theme.backgroundColor)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.outline,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "ا",
                                color = theme.textColor,
                                fontFamily = UrduFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .align(Alignment.BottomEnd)
                                        .clip(CircleShape)
                                        .background(EmeraldPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = theme.titleUrdu.substringBefore(" ("),
                            fontFamily = UrduFontFamily,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Font Size Slider (14sp - 36sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "فونٹ سائز: ${(settings.fontSizeSp).toInt()} pt",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            if (settings.fontSizeSp > 14f) {
                                onSettingsChanged(settings.copy(fontSizeSp = settings.fontSizeSp - 1f))
                            }
                        }
                    ) {
                        Text("A-", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    IconButton(
                        onClick = {
                            if (settings.fontSizeSp < 36f) {
                                onSettingsChanged(settings.copy(fontSizeSp = settings.fontSizeSp + 1f))
                            }
                        }
                    ) {
                        Text("A+", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }

            Slider(
                value = settings.fontSizeSp,
                onValueChange = { onSettingsChanged(settings.copy(fontSizeSp = it)) },
                valueRange = 14f..36f,
                steps = 21,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("font_size_slider"),
                colors = SliderDefaults.colors(thumbColor = EmeraldPrimary, activeTrackColor = EmeraldPrimary)
            )

            // 3. Font Family (PDMS Jauhar Default)
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "رسم الخط (Font Family)",
                        fontFamily = UrduFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = EmeraldPrimary.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "طے شدہ: PDMS Jauhar",
                            fontFamily = UrduFontFamily,
                            fontSize = 11.sp,
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FontStyleType.entries.forEach { style ->
                        FilterChip(
                            selected = settings.fontStyle == style,
                            onClick = { onSettingsChanged(settings.copy(fontStyle = style)) },
                            label = {
                                Text(
                                    text = style.urduLabel,
                                    fontFamily = com.example.ui.theme.getFontFamily(style),
                                    fontSize = 12.sp
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4. Text Alignment (Justified vs Right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "متن کی سیدھ (Alignment)",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = settings.alignment == TextAlignmentType.JUSTIFIED,
                        onClick = { onSettingsChanged(settings.copy(alignment = TextAlignmentType.JUSTIFIED)) },
                        leadingIcon = { Icon(Icons.Default.FormatAlignJustify, contentDescription = null, modifier = Modifier.size(14.dp)) },
                        label = { Text("برابر (Justified)", fontFamily = UrduFontFamily, fontSize = 12.sp) }
                    )
                    FilterChip(
                        selected = settings.alignment == TextAlignmentType.RIGHT_ALIGNED,
                        onClick = { onSettingsChanged(settings.copy(alignment = TextAlignmentType.RIGHT_ALIGNED)) },
                        leadingIcon = { Icon(Icons.AutoMirrored.Filled.FormatAlignRight, contentDescription = null, modifier = Modifier.size(14.dp)) },
                        label = { Text("دائیں جانب", fontFamily = UrduFontFamily, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 5. Line Spacing Multiplier (Fixed 1.2x)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "سطروں کا فاصلہ (Line Spacing): 1.2x (مقررہ)",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = EmeraldPrimary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "1.2x",
                        fontFamily = UrduFontFamily,
                        fontSize = 12.sp,
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // 6. Paragraph Spacing (4dp - 24dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "پیراگراف کے درمیان فاصلہ: ${settings.paragraphSpacingDp} dp",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }

            Slider(
                value = settings.paragraphSpacingDp.toFloat(),
                onValueChange = { onSettingsChanged(settings.copy(paragraphSpacingDp = it.toInt())) },
                valueRange = 4f..24f,
                steps = 9,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(thumbColor = EmeraldPrimary, activeTrackColor = EmeraldPrimary)
            )

            // 7. Page Margins (Horizontal 12-32dp, Vertical 8-28dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "صفحے کے حاشیے (Margins): ${settings.horizontalMarginDp} dp",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }

            Slider(
                value = settings.horizontalMarginDp.toFloat(),
                onValueChange = { onSettingsChanged(settings.copy(horizontalMarginDp = it.toInt(), verticalMarginDp = (it * 0.8f).toInt())) },
                valueRange = 12f..32f,
                steps = 9,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(thumbColor = EmeraldPrimary, activeTrackColor = EmeraldPrimary)
            )

            // 8. Book Page Border Frame (کتابی حاشیہ)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "کتابی حاشیہ (Book Frame)",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = settings.showBookFrame,
                        onClick = { onSettingsChanged(settings.copy(showBookFrame = true)) },
                        label = { Text("کتابی حاشیہ", fontFamily = UrduFontFamily, fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = !settings.showBookFrame,
                        onClick = { onSettingsChanged(settings.copy(showBookFrame = false)) },
                        label = { Text("سادہ ورق", fontFamily = UrduFontFamily, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 9. Page Numbering Digits (صفحہ نمبر کے ہندسے)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "صفحہ نمبر کے ہندسے",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = settings.useUrduNumerals,
                        onClick = { onSettingsChanged(settings.copy(useUrduNumerals = true)) },
                        label = { Text("اردو (— ۱۲ —)", fontFamily = UrduFontFamily, fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = !settings.useUrduNumerals,
                        onClick = { onSettingsChanged(settings.copy(useUrduNumerals = false)) },
                        label = { Text("انگریزی (— 12 —)", fontFamily = UrduFontFamily, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 10. Screen Brightness Control
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.BrightnessMedium, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "اسکرین کی چمک (Brightness): ${(settings.brightness * 100).toInt()}%",
                        fontFamily = UrduFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }

            Slider(
                value = settings.brightness,
                onValueChange = { onSettingsChanged(settings.copy(brightness = it)) },
                valueRange = 0.2f..1.0f,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("brightness_slider"),
                colors = SliderDefaults.colors(thumbColor = EmeraldPrimary, activeTrackColor = EmeraldPrimary)
            )

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
