package com.android.feature_menu.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.android.core.core_design_system.FakeStoreTheme
import com.android.core.core_design_system.component.AppLargeTopBar
import com.android.fakestore.core.core_resources.R
import com.android.session.ThemeMode

@Composable
fun MenuScreen(onBack: () -> Unit, onLoggedOut: () -> Unit) {
    val viewModel = hiltViewModel<MenuScreenViewModel>()
    val state = viewModel.viewState
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.setEvent(Event.LoadMenu)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            AppLargeTopBar(
                title = state.value.username?.let { stringResource(id = R.string.menu_title, it) }
                    ?: stringResource(id = R.string.menu_title_fallback),
                onBackClick = onBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
                .padding(FakeStoreTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(FakeStoreTheme.spacing.md)
        ) {
            MenuRow(
                icon = Icons.Filled.Person,
                label = stringResource(id = R.string.menu_user_settings),
                onClick = { viewModel.setEvent(Event.OnUserSettingsClick) }
            )
            MenuRow(
                icon = Icons.Filled.Favorite,
                label = stringResource(id = R.string.menu_favorites),
                onClick = { viewModel.setEvent(Event.OnFavoritesClick) }
            )
            ThemeSection(
                selectedMode = state.value.themeMode,
                onModeSelected = { viewModel.setEvent(Event.OnThemeModeSelected(it)) }
            )
            MenuRow(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                label = stringResource(id = R.string.menu_logout),
                tint = MaterialTheme.colorScheme.error,
                onClick = { viewModel.setEvent(Event.OnLogoutClick) }
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .collect { effect ->
                when (effect) {
                    is Effect.ShowMessage -> {
                        Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                    }

                    is Effect.NavigateToLogin -> {
                        onLoggedOut()
                    }
                }
            }
    }
}

@Composable
private fun MenuRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    tint: Color = MaterialTheme.colorScheme.onBackground
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = FakeStoreTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FakeStoreTheme.spacing.md)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = tint
        )
    }
}

@Composable
private fun ThemeSection(
    selectedMode: ThemeMode,
    onModeSelected: (ThemeMode) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(FakeStoreTheme.spacing.xs)) {
        Text(
            text = stringResource(id = R.string.menu_theme),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Column(Modifier.selectableGroup()) {
            ThemeOptionRow(
                label = stringResource(id = R.string.menu_theme_light),
                selected = selectedMode == ThemeMode.LIGHT,
                onClick = { onModeSelected(ThemeMode.LIGHT) }
            )
            ThemeOptionRow(
                label = stringResource(id = R.string.menu_theme_dark),
                selected = selectedMode == ThemeMode.DARK,
                onClick = { onModeSelected(ThemeMode.DARK) }
            )
            ThemeOptionRow(
                label = stringResource(id = R.string.menu_theme_auto),
                selected = selectedMode == ThemeMode.SYSTEM,
                onClick = { onModeSelected(ThemeMode.SYSTEM) }
            )
        }
    }
}

@Composable
private fun ThemeOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
