package com.android.core_model

import com.google.gson.annotations.SerializedName

data class AuthDto(
    @SerializedName("token") val token: String
)

data class LoginRequest(
    val username: String,
    val password: String
)