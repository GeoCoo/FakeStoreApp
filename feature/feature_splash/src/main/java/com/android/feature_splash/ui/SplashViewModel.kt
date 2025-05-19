package com.android.feature_splash.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.android.api.PreferencesController
import com.android.core_ui.base.MviViewModel
import com.android.core_ui.base.ViewEvent
import com.android.core_ui.base.ViewSideEffect
import com.android.core_ui.base.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


data class State(
    val isLoading: Boolean,
    ) : ViewState

sealed class Event : ViewEvent {
    data object CheckToken : Event()
}

sealed class Effect : ViewSideEffect {
    data class Navigate(val isLogged: Boolean) : Effect()
}


@HiltViewModel
class SplashViewModel @Inject constructor(
    private val preferencesController: PreferencesController
) :
    MviViewModel<Event, State, Effect>() {
    override fun setInitialState(): State = State(
        isLoading = true,
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun handleEvents(event: Event) {
        when (event) {
            is Event.CheckToken -> {
                viewModelScope.launch {
                    val token = preferencesController.getString("user_token", "")
                    setState {
                        copy(
                            isLoading = false,
                            )
                    }

                    setEffect{
                        Effect.Navigate(token.isNotEmpty())
                    }

                }
            }
        }
    }
}