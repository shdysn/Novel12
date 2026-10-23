package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderSettingsSheet(
    settings: ReaderSettings,
    onSettingsChanged: (ReaderSettings) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

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
                text = "قراءت اور صفحہ بندی کی ترتیبات (Reading Settings)",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            // Live preview card with selected styling
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                color = settings.theme.backgroundColor,
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f)),
                shadowElevation = 2.dp
            ) {
                Text(
                    text = "یہ اردو صفحہ بندی کا براہِ راست نمونہ ہے، جمیل نوری نستعلیق خطاطی کے ساتھ۔",
                    fontFamily = if (settings.fontStyle == FontStyleType.NASTALEEQ) FontFamily.Serif else FontFamily.Default,
                    fontSize = settings.fontSizeSp.sp,
                    lineHeight = (settings.fontSizeSp * settings.lineHeightMultiplier).sp,
                    color = settings.theme.textColor,
                    textAlign = if (settings.alignment == TextAlignmentType.JUSTIFIED) TextAlign.Justify else TextAlign.Right,
                    modifier = Modifier.padding(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Reading Themes (4 Core Themes)
            Text(
                text = "مطالعہ کے پس منظر (Reading Themes)",
                fontFamily = FontFamily.Serif,
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
                                fontFamily = FontFamily.Serif,
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
                            fontFamily = FontFamily.Serif,
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
                    fontFamily = FontFamily.Serif,
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

            // 3. Font Family (Nastaliq, Mehr, Naskh)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "رسم الخط (Font Family)",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = settings.fontStyle == FontStyleType.NASTALEEQ,
                        onClick = { onSettingsChanged(settings.copy(fontStyle = FontStyleType.NASTALEEQ)) },
                        label = { Text("جمیل نوری نستعلیق", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = settings.fontStyle == FontStyleType.MEHR_NASTALEEQ,
                        onClick = { onSettingsChanged(settings.copy(fontStyle = FontStyleType.MEHR_NASTALEEQ)) },
                        label = { Text("مہر نستعلیق", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = settings.fontStyle == FontStyleType.NASKH,
                        onClick = { onSettingsChanged(settings.copy(fontStyle = FontStyleType.NASKH)) },
                        label = { Text("نسخ فونٹ", fontSize = 11.sp) }
                    )
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
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = settings.alignment == TextAlignmentType.JUSTIFIED,
                        onClick = { onSettingsChanged(settings.copy(alignment = TextAlignmentType.JUSTIFIED)) },
                        leadingIcon = { Icon(Icons.Default.FormatAlignJustify, contentDescription = null, modifier = Modifier.size(14.dp)) },
                        label = { Text("برابر (Justified)", fontFamily = FontFamily.Serif, fontSize = 12.sp) }
                    )
                    FilterChip(
                        selected = settings.alignment == TextAlignmentType.RIGHT_ALIGNED,
                        onClick = { onSettingsChanged(settings.copy(alignment = TextAlignmentType.RIGHT_ALIGNED)) },
                        leadingIcon = { Icon(Icons.AutoMirrored.Filled.FormatAlignRight, contentDescription = null, modifier = Modifier.size(14.dp)) },
                        label = { Text("دائیں جانب", fontFamily = FontFamily.Serif, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 5. Line Spacing Multiplier (1.4x - 2.4x)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "سطروں کا فاصلہ (Line Height): ${"%.1f".format(settings.lineHeightMultiplier)}x",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }

            Slider(
                value = settings.lineHeightMultiplier,
                onValueChange = { onSettingsChanged(settings.copy(lineHeightMultiplier = it)) },
                valueRange = 1.4f..2.4f,
                steps = 9,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("line_height_slider"),
                colors = SliderDefaults.colors(thumbColor = EmeraldPrimary, activeTrackColor = EmeraldPrimary)
            )

            // 6. Paragraph Spacing (4dp - 24dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "پیراگراف کے درمیان فاصلہ: ${settings.paragraphSpacingDp} dp",
                    fontFamily = FontFamily.Serif,
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
                    fontFamily = FontFamily.Serif,
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

            // 8. Screen Brightness Control
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
                        fontFamily = FontFamily.Serif,
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
