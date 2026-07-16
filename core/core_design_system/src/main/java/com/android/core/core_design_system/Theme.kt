package com.android.core.core_design_system

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController

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

    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !darkTheme
    val background = colors.background
    DisposableEffect(systemUiController, useDarkIcons) {
        systemUiController.setSystemBarsColor(
            color = background,
            darkIcons = useDarkIcons
        )
        systemUiController.setNavigationBarColor(
            color = background,
            darkIcons = useDarkIcons
        )

        onDispose {}
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
