package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.example.data.model.FontStyleType

private val MidnightDarkColorScheme = darkColorScheme(
    primary = GoldAccent,
    onPrimary = Color(0xFF1E1700),
    primaryContainer = GoldContainer,
    onPrimaryContainer = OnGoldContainer,
    secondary = GoldDark,
    onSecondary = Color(0xFF1E1700),
    secondaryContainer = MidnightDarkCardElevated,
    onSecondaryContainer = TextWhitePrimary,
    background = MidnightDarkBg,
    onBackground = TextWhitePrimary,
    surface = MidnightDarkSurface,
    onSurface = TextWhitePrimary,
    surfaceVariant = MidnightDarkCard,
    onSurfaceVariant = TextMutedPurple,
    outline = MidnightDarkBorder
)

@Composable
fun UrduNovelReaderTheme(
    darkTheme: Boolean = true, // Force Dark Midnight Luxury Theme matching user reference UI
    dynamicColor: Boolean = false,
    fontStyle: FontStyleType = FontStyleType.PDMS_JAUHAR,
    content: @Composable () -> Unit
) {
    val activeFontFamily = getFontFamily(fontStyle)
    val colorScheme = MidnightDarkColorScheme

    CompositionLocalProvider(
        LocalUrduFontFamily provides activeFontFamily
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = createUrduTypography(activeFontFamily)
        ) {
            androidx.compose.runtime.CompositionLocalProvider(
                androidx.compose.material3.LocalTextStyle provides androidx.compose.material3.LocalTextStyle.current.copy(
                    fontFamily = activeFontFamily
                ),
                content = content
            )
        }
    }
}

