package com.android.core_model

data class AuthDto(
    val token: String
)

data class LoginRequest(
    val username: String,
    val password: String
)