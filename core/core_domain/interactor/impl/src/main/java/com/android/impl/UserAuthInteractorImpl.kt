package com.android.impl

import com.android.api.AuthRepository
import com.android.api.AuthResponse
import com.android.api.AuthResponsePartialState
import com.android.api.ResourceProvider
import com.android.api.UserAuthInteractor
import com.android.fakestore.core.core_resources.R
import com.android.helpers.safeAsync
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserAuthInteractorImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val resourcesProvider: ResourceProvider

) : UserAuthInteractor {
    override suspend fun userLogin(
        username: String,
        password: String
    ): Flow<AuthResponsePartialState> = flow {
        authRepository.userLogin(username, password).collect {
            when (it) {
                is AuthResponse.Failed -> {
                    emit(AuthResponsePartialState.Failed(it.errorMsg))
                }

                is AuthResponse.Success -> {
                    emit(AuthResponsePartialState.Success(it.token))
                }
            }
        }
    }.safeAsync {
        AuthResponsePartialState.Failed(
            it.message ?: resourcesProvider.getString(R.string.generic_error_msg)
        )
    }
}
