package com.android.feature_login.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.android.api.AuthResponsePartialState
import com.android.api.PreferencesController
import com.android.api.UserAuthInteractor
import com.android.core_ui.base.MviViewModel
import com.android.core_ui.base.ViewEvent
import com.android.core_ui.base.ViewSideEffect
import com.android.core_ui.base.ViewState
import com.android.model.Preferences
import com.android.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID.randomUUID
import javax.inject.Inject


data class State(
    val isLoading: Boolean,
) : ViewState

sealed class Event : ViewEvent {
    data class UserLogin(val userNAme: String, val password: String) : Event()
}

sealed class Effect : ViewSideEffect {
    data object SuccessNavigate : Effect()
    data class ShowMessage(val message: String) : Effect()
}


@HiltViewModel
class LoginVIewModel @Inject constructor(
    private val userAuthInteractor: UserAuthInteractor,
    private val preferencesController: PreferencesController,
    private val sessionManager: SessionManager
) :
    MviViewModel<Event, State, Effect>() {
    override fun setInitialState(): State = State(
        isLoading = true,
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun handleEvents(event: Event) {
        when (event) {
            is Event.UserLogin -> {
                viewModelScope.launch {
                    userAuthInteractor.userLogin(event.userNAme, event.password).collect {
                        when (it) {
                            is AuthResponsePartialState.Failed -> {
                                setState { copy(isLoading = false) }
                                setEffect { Effect.ShowMessage(it.errorMessage) }
                            }

                            is AuthResponsePartialState.Success -> {

                                sessionManager.login(it.token)

                                setState { copy(isLoading = false) }

                                setEffect { Effect.SuccessNavigate }
                            }
                        }
                    }
                }
            }
        }
    }
}
