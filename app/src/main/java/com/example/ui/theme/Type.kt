package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.FontStyleType

// Bundled Urdu Nastaliq & Naskh Fonts
val PdmsJauharFontFamily = FontFamily(
    Font(R.font.pdms_jauhar, FontWeight.Normal)
)

val NotoNastaliqUrduFontFamily = FontFamily(
    Font(R.font.noto_nastaliq_urdu, FontWeight.Normal),
    Font(R.font.noto_nastaliq_urdu_bold, FontWeight.Bold)
)

val MehrNastaliqFontFamily = FontFamily(
    Font(R.font.gulzar, FontWeight.Normal),
    Font(R.font.gulzar, FontWeight.Bold)
)

val AmiriFontFamily = FontFamily(
    Font(R.font.amiri, FontWeight.Normal),
    Font(R.font.amiri, FontWeight.Bold)
)

val LateefFontFamily = FontFamily(
    Font(R.font.lateef, FontWeight.Normal),
    Font(R.font.lateef_bold, FontWeight.Bold)
)

val ScheherazadeFontFamily = FontFamily(
    Font(R.font.scheherazade_new, FontWeight.Normal),
    Font(R.font.scheherazade_new, FontWeight.Bold)
)

/**
 * Returns the resolved FontFamily for the given FontStyleType.
 */
fun getFontFamily(fontStyle: FontStyleType): FontFamily {
    return when (fontStyle) {
        FontStyleType.PDMS_JAUHAR -> PdmsJauharFontFamily
        FontStyleType.NASTALEEQ -> NotoNastaliqUrduFontFamily
        FontStyleType.MEHR_NASTALEEQ -> MehrNastaliqFontFamily
        FontStyleType.NASKH -> AmiriFontFamily
        FontStyleType.LATEEF -> LateefFontFamily
        FontStyleType.SCHEHERAZADE -> ScheherazadeFontFamily
    }
}

/**
 * CompositionLocal providing the currently active Urdu font family across all screens.
 */
val LocalUrduFontFamily = compositionLocalOf<FontFamily> { PdmsJauharFontFamily }

/**
 * Dynamic UrduFontFamily that resolves to the currently selected font family from LocalUrduFontFamily.
 * Any Composable referencing UrduFontFamily will automatically update whenever the active font changes.
 */
val UrduFontFamily: FontFamily
    @Composable
    @ReadOnlyComposable
    get() = LocalUrduFontFamily.current

/**
 * Factory function creating Material3 Typography styled with the specified Urdu FontFamily.
 * Covers all 15 Material 3 typography tokens to guarantee that every text, label, button,
 * card, and dialog across the entire application renders in authentic Nastaliq script.
 */
fun createUrduTypography(fontFamily: FontFamily = PdmsJauharFontFamily): Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 38.4.sp, // 1.2x line spacing
        letterSpacing = 0.sp
    ),
    displayMedium = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 33.6.sp, // 1.2x line spacing
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 28.8.sp, // 1.2x line spacing
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 26.4.sp, // 1.2x line spacing
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 24.sp, // 1.2x line spacing
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 21.6.sp, // 1.2x line spacing
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 24.sp, // 1.2x line spacing
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 20.4.sp, // 1.2x line spacing
        letterSpacing = 0.sp
    ),
    titleSmall = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 18.sp, // 1.2x line spacing
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        lineHeight = 20.4.sp, // 1.2x line spacing
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 16.8.sp, // 1.2x line spacing
        letterSpacing = 0.sp
    ),
    bodySmall = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 14.4.sp, // 1.2x line spacing
        letterSpacing = 0.sp
    ),
    labelLarge = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 16.8.sp, // 1.2x line spacing
        letterSpacing = 0.sp
    ),
    labelMedium = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 14.4.sp, // 1.2x line spacing
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 13.2.sp, // 1.2x line spacing
        letterSpacing = 0.sp
    )
)

val Typography = createUrduTypography(PdmsJauharFontFamily)
