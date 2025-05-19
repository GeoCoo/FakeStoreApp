package com.android.impl

import com.android.api.AuthRepository
import com.android.api.AuthResponse
import com.android.api.AuthResponsePartialState
import com.android.api.UserAuthInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

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
