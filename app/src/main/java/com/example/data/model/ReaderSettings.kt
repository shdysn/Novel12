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
    val textColor: Color,
    val deskColor: Color
) {
    WARM_PAPER("کاغذی (Warm Paper)", ReaderPaperBg, ReaderPaperText, Color(0xFFE8DFC9)),
    SEPIA("سیپیا (Sepia)", ReaderSepiaBg, ReaderSepiaText, Color(0xFFD6C5A2)),
    DARK_MODE("نائٹ موڈ (Dark Mode)", ReaderNightBg, ReaderNightText, Color(0xFF0F1215)),
    PURE_WHITE("خالص سفید (Pure White)", Color(0xFFFFFFFF), Color(0xFF141414), Color(0xFFE2E6EC)),
    OLED("گہرا سیاہ (OLED)", ReaderOledBg, ReaderOledText, Color(0xFF080808)),
    EMERALD("زمردی (Emerald)", ReaderEmeraldBg, ReaderEmeraldText, Color(0xFF06140D))
}

enum class FontStyleType(val urduLabel: String) {
    PDMS_JAUHAR("پی ڈی ایم ایس جوہر (PDMS Johur Nastaliq — معیاری)"),
    NASTALEEQ("نوری نستعلیق (Jameel / Noto)"),
    MEHR_NASTALEEQ("مہر نستعلیق (Mehr / Gulzar)"),
    NASKH("جدید نسخ (Amiri Naskh)"),
    LATEEF("خطِ لطیف (Lateef)"),
    SCHEHERAZADE("خطِ شہزاد (Scheherazade / روایتی نسخ)")
}

enum class TextAlignmentType(val urduLabel: String) {
    JUSTIFIED("برابر (Justified)"),
    RIGHT_ALIGNED("دائیں جانب (Right Aligned)")
}

data class ReaderSettings(
    val fontSizeSp: Float = 22f,
    val lineHeightMultiplier: Float = 1.6f,
    val paragraphSpacingDp: Int = 4,
    val horizontalMarginDp: Int = 12,
    val verticalMarginDp: Int = 8,
    val alignment: TextAlignmentType = TextAlignmentType.JUSTIFIED,
    val theme: ReaderTheme = ReaderTheme.WARM_PAPER,
    val fontStyle: FontStyleType = FontStyleType.PDMS_JAUHAR,
    val brightness: Float = 0.85f,
    val autoBookmark: Boolean = true,
    val showBookFrame: Boolean = true,
    val useUrduNumerals: Boolean = true
)

/**
 * Converts Western digits (0-9) to Eastern/Urdu digits (۰-۹) for authentic book folios.
 */
fun Int.toUrduDigits(): String {
    val urduDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    return this.toString().map { ch ->
        if (ch in '0'..'9') urduDigits[ch - '0'] else ch
    }.joinToString("")
}
