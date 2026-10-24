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
    PURE_WHITE("خالص سفید", Color(0xFFFFFFFF), Color(0xFF000000), Color(0xFFFFFFFF)),
    WARM_PAPER("کاغذی (کلاسک)", ReaderPaperBg, ReaderPaperText, Color(0xFFE8DFC9)),
    SEPIA("سیپیا", ReaderSepiaBg, ReaderSepiaText, Color(0xFFD6C5A2)),
    DARK_MODE("نائٹ موڈ", ReaderNightBg, ReaderNightText, Color(0xFF0F1215)),
    OLED("گہرا سیاہ", ReaderOledBg, ReaderOledText, Color(0xFF080808)),
    EMERALD("زمردی", ReaderEmeraldBg, ReaderEmeraldText, Color(0xFF06140D))
}

enum class FontStyleType(val urduLabel: String) {
    PDMS_JAUHAR("پی ڈی ایم ایس جوہر (معیاری)"),
    NASTALEEQ("نوری نستعلیق"),
    MEHR_NASTALEEQ("مہر نستعلیق"),
    NASKH("جدید نسخ"),
    LATEEF("خطِ لطیف"),
    SCHEHERAZADE("خطِ شہزاد")
}

enum class TextAlignmentType(val urduLabel: String) {
    JUSTIFIED("برابر (Justified)"),
    RIGHT_ALIGNED("دائیں جانب (Right Aligned)")
}

data class ReaderSettings(
    val fontSizeSp: Float = 22f,
    val lineHeightMultiplier: Float = 1.2f,
    val paragraphSpacingDp: Int = 4,
    val horizontalMarginDp: Int = 12,
    val verticalMarginDp: Int = 8,
    val alignment: TextAlignmentType = TextAlignmentType.RIGHT_ALIGNED,
    val theme: ReaderTheme = ReaderTheme.PURE_WHITE,
    val fontStyle: FontStyleType = FontStyleType.PDMS_JAUHAR,
    val brightness: Float = 0.85f,
    val autoBookmark: Boolean = true,
    val showBookFrame: Boolean = false,
    val useUrduNumerals: Boolean = true,
    val autoScrollEnabled: Boolean = false,
    val autoScrollSpeed: Float = 1.0f
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
