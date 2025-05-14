package com.android.core.core_domain.interactor

import com.android.core.core_data.repository.AuthRepository
import com.android.core.core_data.repository.AuthResponse
import com.android.core_model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


interface AuthInteractor {
    suspend fun userLogin(user: User): Flow<AuthResponsePartialState>
}

class AuthInteractorImpl @Inject constructor(
    private val authRepository: AuthRepository

) : AuthInteractor {
    override suspend fun userLogin(user: User): Flow<AuthResponsePartialState> = flow {
        authRepository.userLogin(user).collect {
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
