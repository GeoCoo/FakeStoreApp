package com.android.feature_login.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.android.core.core_domain.controller.PreferencesController
import com.android.core.core_domain.interactor.AuthResponsePartialState
import com.android.core.core_domain.interactor.UserAuthInteractor
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
    data class UserLogin(val userNAme: String, val password: String) : Event()
}

sealed class Effect : ViewSideEffect {
    data object SuccessNavigate : Effect()
}


@HiltViewModel
class LoginVIewModel @Inject constructor(
    private val userAuthInteractor: UserAuthInteractor,
    private val preferencesController: PreferencesController
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
                            is AuthResponsePartialState.Failed -> TODO()
                            is AuthResponsePartialState.Success -> {
                                val token = preferencesController.getString("user_token", "")

                                if (token != it.token) preferencesController.setString(
                                    "user_token",
                                    it.token
                                )


                                setEffect { Effect.SuccessNavigate }


                            }
                        }
                    }
                }
            }
        }
    }
}
