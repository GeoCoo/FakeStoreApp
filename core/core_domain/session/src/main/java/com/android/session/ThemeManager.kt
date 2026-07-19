package com.android.session

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.android.api.ResourceProvider
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

@Singleton
class ThemeManager @Inject constructor(resourceProvider: ResourceProvider) {

    private val prefs: SharedPreferences = resourceProvider.provideContext()
        .getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

    private val THEME_MODE_KEY = "theme_mode"

    private val _themeMode = MutableStateFlow(
        prefs.getString(THEME_MODE_KEY, null)?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
            ?: ThemeMode.SYSTEM
    )
    val themeMode: StateFlow<ThemeMode> = _themeMode

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit { putString(THEME_MODE_KEY, mode.name) }
        _themeMode.value = mode
    }
}
