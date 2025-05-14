package com.android.core_model

data class AuthDto(
    val token: String
)

data class User(
    val userName: String,
    val userPassword: String
)