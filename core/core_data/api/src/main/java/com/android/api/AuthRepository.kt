package com.android.api

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun userLogin(username: String,password: String): Flow<AuthResponse>
}

sealed class AuthResponse {
    data class Success(val token: String) : AuthResponse()
    data class Failed(val errorMsg: String) : AuthResponse()

}