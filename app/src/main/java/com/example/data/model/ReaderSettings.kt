package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.ReaderEmeraldBg
import com.example.ui.theme.ReaderEmeraldText
import com.example.ui.theme.ReaderNightBg
import com.example.ui.theme.ReaderNightText
import com.example.ui.theme.ReaderOledBg
import com.example.ui.theme.ReaderOledText
import com.example.ui.theme.ReaderPaperBg
import com.example.ui.theme.ReaderPaperText
import com.example.ui.theme.ReaderSepiaBg
import com.example.ui.theme.ReaderSepiaText

enum class ReaderTheme(
    val titleUrdu: String,
    val backgroundColor: Color,
    val textColor: Color
) {
    WARM_PAPER("کاغذی (Warm Paper)", ReaderPaperBg, ReaderPaperText),
    SEPIA("سیپیا (Sepia)", ReaderSepiaBg, ReaderSepiaText),
    DARK_MODE("نائٹ موڈ (Dark Mode)", ReaderNightBg, ReaderNightText),
    PURE_WHITE("خالص سفید (Pure White)", Color(0xFFFFFFFF), Color(0xFF141414)),
    OLED("گہرا سیاہ (OLED)", ReaderOledBg, ReaderOledText),
    EMERALD("زمردی (Emerald)", ReaderEmeraldBg, ReaderEmeraldText)
}

enum class FontStyleType(val urduLabel: String) {
    NASTALEEQ("نوری نستعلیق (Jameel / Noto)"),
    MEHR_NASTALEEQ("مہر نستعلیق (Mehr / Gulzar)"),
    NASKH("جدید نسخ (Amiri Naskh)")
}

enum class TextAlignmentType(val urduLabel: String) {
    JUSTIFIED("برابر (Justified)"),
    RIGHT_ALIGNED("دائیں جانب (Right Aligned)")
}

data class ReaderSettings(
    val fontSizeSp: Float = 22f,
    val lineHeightMultiplier: Float = 1.8f,
    val paragraphSpacingDp: Int = 12,
    val horizontalMarginDp: Int = 20,
    val verticalMarginDp: Int = 16,
    val alignment: TextAlignmentType = TextAlignmentType.JUSTIFIED,
    val theme: ReaderTheme = ReaderTheme.WARM_PAPER,
    val fontStyle: FontStyleType = FontStyleType.NASTALEEQ,
    val brightness: Float = 0.85f,
    val autoBookmark: Boolean = true
)
