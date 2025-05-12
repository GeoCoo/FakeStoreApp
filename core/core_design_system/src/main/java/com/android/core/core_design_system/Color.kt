package com.android.core.core_design_system

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


data class ThemeColorsTemplate(
    private val darkTheme: Boolean, val light: ColorScheme, val dark: ColorScheme
) {
    val colors: ColorScheme
        get() {
            return when (darkTheme) {
                true -> dark
                false -> light
            }
        }
}

private val LightColors = lightColorScheme()

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFFAB30),
    onPrimary = Color(0xFF000000),

    secondary = Color(0xFFE7410F),
    onSecondary = Color(0xFFFFFFFF),

    tertiary = Color(0xFF0094FF),
    onTertiary = Color(0xFFFFFFFF),

    background = Color(0xFF343434),
    onBackground = Color(0xFFFFFFFF),

    surface = Color(0xFF343434),
    onSurface = Color(0xFFFFFFFF),

    surfaceVariant = Color(0xFF000000),
    onSurfaceVariant = Color(0xFFFFFFFF),
)

@Composable
fun themeColors(darkTheme: Boolean): ThemeColorsTemplate {
    return ThemeColorsTemplate(darkTheme, LightColors, DarkColors)
}
