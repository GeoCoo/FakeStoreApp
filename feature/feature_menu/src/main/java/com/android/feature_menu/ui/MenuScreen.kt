package com.android.feature_menu.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.android.core.core_design_system.FakeStoreTheme
import com.android.core_ui.component.organisms.AppLargeTopBar
import com.android.fakestore.core.core_resources.R
import com.android.feature_menu.ui.atoms.MenuRow
import com.android.feature_menu.ui.molecules.ThemeSection

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
