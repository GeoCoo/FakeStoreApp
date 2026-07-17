package com.android.core.core_design_system

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun FakeStoreTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val useDynamicColors = false
    val colors = when {
        useDynamicColors && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
        useDynamicColors && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
        else -> themeColors(darkTheme = darkTheme).colors
    }

    val useDarkIcons = !darkTheme
    val background = colors.background
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = background.toArgb()
            window.navigationBarColor = background.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = useDarkIcons
            insetsController.isAppearanceLightNavigationBars = useDarkIcons
        }
    }

    CompositionLocalProvider(
        LocalSpacing provides Spacing(),
        LocalCorners provides AppCorners(),
    ) {
        MaterialTheme(
            colorScheme = colors,
            typography = FakeStoreTypography,
            shapes = FakeStoreShapes,
            content = content
        )
    }
}

/**
 * Convenience accessor mirroring `MaterialTheme.*`, so components can pull
 * spacing/corner tokens without threading `LocalSpacing.current` everywhere.
 */
object FakeStoreTheme {
    val spacing: Spacing
        @Composable get() = LocalSpacing.current

    val corners: AppCorners
        @Composable get() = LocalCorners.current

    val colorScheme
        @Composable get() = MaterialTheme.colorScheme

    val typography
        @Composable get() = MaterialTheme.typography

    val shapes
        @Composable get() = MaterialTheme.shapes
}
