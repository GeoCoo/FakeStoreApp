package com.android.api

import kotlinx.coroutines.flow.Flow


interface UserAuthInteractor {
    suspend fun userLogin(username: String, password: String): Flow<AuthResponsePartialState>
}

sealed class AuthResponsePartialState {
    data class Success(val token: String) : AuthResponsePartialState()
    data class Failed(val errorMessage: String) : AuthResponsePartialState()
}
