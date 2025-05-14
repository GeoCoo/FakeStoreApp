package com.android.core.core_domain.interactor

import com.android.core.core_data.repository.AuthRepository
import com.android.core.core_data.repository.AuthResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


interface UserAuthInteractor {
    suspend fun userLogin(username: String, password: String): Flow<AuthResponsePartialState>
}

class UserAuthInteractorImpl1 @Inject constructor(
    private val authRepository: AuthRepository

) : UserAuthInteractor {
    override suspend fun userLogin(username: String, password: String): Flow<AuthResponsePartialState> = flow {
        authRepository.userLogin(username,password).collect {
            when (it) {
                is AuthResponse.Failed -> {
                    emit(AuthResponsePartialState.Failed(it))
                }

                else -> {
                    emit(AuthResponsePartialState.Success(it))
                }
            }
        }
    }


}


sealed class AuthResponsePartialState {
    data class Success(val products: AuthResponse) : AuthResponsePartialState()
    data class Failed(val errorMessage: AuthResponse) : AuthResponsePartialState()
}
