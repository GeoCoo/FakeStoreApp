package com.android.core.core_design_system

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Raw color primitives. Nothing outside this file should reference these directly -
 * consume semantic roles from [ColorScheme] via `MaterialTheme.colorScheme` instead.
 */
private object Palette {
    // Brand
    val Coral10 = Color(0xFFFFE8EE)
    val Coral40 = Color(0xFFFF3366)
    val Coral50 = Color(0xFFE01F52)
    val Coral90 = Color(0xFF4A0016)

    val Indigo10 = Color(0xFFE9EDFF)
    val Indigo40 = Color(0xFF547AFF)
    val Indigo50 = Color(0xFF3A5CDB)
    val Indigo90 = Color(0xFF0B1440)

    // Neutrals
    val Neutral0 = Color(0xFFFFFFFF)
    val Neutral10 = Color(0xFFF7F7F8)
    val Neutral20 = Color(0xFFEDEDF0)
    val Neutral30 = Color(0xFFD8D8DE)
    val Neutral40 = Color(0xFFBDBDC6)
    val Neutral60 = Color(0xFF8A8A94)
    val Neutral80 = Color(0xFF48484F)
    val Neutral90 = Color(0xFF2A2A30)
    val Neutral95 = Color(0xFF1C1C21)
    val Neutral99 = Color(0xFF121215)

    // Semantic
    val Error40 = Color(0xFFB00020)
    val Error10 = Color(0xFFFFDAD9)
    val Error90 = Color(0xFF410003)
    val ErrorDark80 = Color(0xFFFFB4A9)
}

val LightColors: ColorScheme = lightColorScheme(
    primary = Palette.Coral40,
    onPrimary = Palette.Neutral0,
    primaryContainer = Palette.Coral10,
    onPrimaryContainer = Palette.Coral90,

    secondary = Palette.Indigo40,
    onSecondary = Palette.Neutral0,
    secondaryContainer = Palette.Indigo10,
    onSecondaryContainer = Palette.Indigo90,

    background = Palette.Neutral10,
    onBackground = Palette.Neutral90,

    surface = Palette.Neutral0,
    onSurface = Palette.Neutral90,
    surfaceVariant = Palette.Neutral20,
    onSurfaceVariant = Palette.Neutral80,

    error = Palette.Error40,
    onError = Palette.Neutral0,
    errorContainer = Palette.Error10,
    onErrorContainer = Palette.Error90,

    outline = Palette.Neutral40,
    outlineVariant = Palette.Neutral20,
)

val DarkColors: ColorScheme = darkColorScheme(
    primary = Palette.Coral40,
    onPrimary = Palette.Neutral0,
    primaryContainer = Palette.Coral50,
    onPrimaryContainer = Palette.Coral10,

    secondary = Palette.Indigo40,
    onSecondary = Palette.Neutral0,
    secondaryContainer = Palette.Indigo50,
    onSecondaryContainer = Palette.Indigo10,

    background = Palette.Neutral99,
    onBackground = Palette.Neutral10,

    surface = Palette.Neutral95,
    onSurface = Palette.Neutral10,
    surfaceVariant = Palette.Neutral90,
    onSurfaceVariant = Palette.Neutral30,

    error = Palette.ErrorDark80,
    onError = Palette.Error90,
    errorContainer = Palette.Error40,
    onErrorContainer = Palette.Error10,

    outline = Palette.Neutral60,
    outlineVariant = Palette.Neutral80,
)

data class ThemeColorsTemplate(
    private val darkTheme: Boolean, val light: ColorScheme, val dark: ColorScheme
) {
    val colors: ColorScheme
        get() = if (darkTheme) dark else light
}

@Composable
fun themeColors(darkTheme: Boolean): ThemeColorsTemplate {
    return ThemeColorsTemplate(darkTheme, LightColors, DarkColors)
}
