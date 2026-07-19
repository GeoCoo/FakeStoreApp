package com.android.feature_menu.ui

import androidx.lifecycle.viewModelScope
import com.android.api.ResourceProvider
import com.android.core_ui.base.MviViewModel
import com.android.core_ui.base.ViewEvent
import com.android.core_ui.base.ViewSideEffect
import com.android.core_ui.base.ViewState
import com.android.fakestore.core.core_resources.R
import com.android.session.SessionManager
import com.android.session.ThemeManager
import com.android.session.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class State(
    val username: String? = null,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
) : ViewState

sealed class Event : ViewEvent {
    data object LoadMenu : Event()
    data class OnThemeModeSelected(val mode: ThemeMode) : Event()
    data object OnUserSettingsClick : Event()
    data object OnFavoritesClick : Event()
    data object OnLogoutClick : Event()
}

sealed class Effect : ViewSideEffect {
    data class ShowMessage(val message: String) : Effect()
    data object NavigateToLogin : Effect()
}

@HiltViewModel
class MenuScreenViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val themeManager: ThemeManager,
    private val resourceProvider: ResourceProvider,
) : MviViewModel<Event, State, Effect>() {

    override fun setInitialState(): State = State(
        username = sessionManager.getCurrentUsername(),
        themeMode = themeManager.themeMode.value,
    )

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.LoadMenu -> {
                setState { copy(username = sessionManager.getCurrentUsername()) }
                viewModelScope.launch {
                    themeManager.themeMode.collectLatest { mode ->
                        setState { copy(themeMode = mode) }
                    }
                }
            }

            is Event.OnThemeModeSelected -> {
                themeManager.setThemeMode(event.mode)
            }

            is Event.OnUserSettingsClick -> {
                setEffect { Effect.ShowMessage(resourceProvider.getString(R.string.menu_coming_soon)) }
            }

            is Event.OnFavoritesClick -> {
                setEffect { Effect.ShowMessage(resourceProvider.getString(R.string.menu_coming_soon)) }
            }

            is Event.OnLogoutClick -> {
                sessionManager.logout()
                setEffect { Effect.NavigateToLogin }
            }
        }
    }
}
