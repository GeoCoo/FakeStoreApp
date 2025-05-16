package com.android.core.core_domain.interactor

import com.android.core.core_data.repository.AuthRepository
import com.android.core.core_data.repository.AuthResponse
import com.android.core.core_domain.controller.PreferencesController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


interface UserAuthInteractor {
    suspend fun userLogin(username: String, password: String): Flow<AuthResponsePartialState>
}

class UserAuthInteractorImpl @Inject constructor(
    private val authRepository: AuthRepository,

) : UserAuthInteractor {
    override suspend fun userLogin(username: String, password: String): Flow<AuthResponsePartialState> = flow {
        authRepository.userLogin(username,password).collect {
            when (it) {
                is AuthResponse.Failed -> {
                    emit(AuthResponsePartialState.Failed(it.errorMsg))
                }
                is AuthResponse.Success -> {
                    emit(AuthResponsePartialState.Success(it.token))
                }
            }
        }
    }
}

sealed class AuthResponsePartialState {
    data class Success(val token: String) : AuthResponsePartialState()
    data class Failed(val errorMessage: String) : AuthResponsePartialState()
}
