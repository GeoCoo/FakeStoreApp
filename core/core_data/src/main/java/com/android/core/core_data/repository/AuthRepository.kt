package com.android.core.core_data.repository

import com.android.core.core_api.api.ApiClient
import com.android.core_model.LoginRequest
import com.android.core_resources.provider.ResourceProvider
import com.android.fakestore.core.core_resources.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


interface AuthRepository {
    fun userLogin(username: String,password: String): Flow<AuthResponse>
}

class AuthRepositoryImpl @Inject constructor(
    private val apiClient: ApiClient, private val resourceProvider: ResourceProvider
) : AuthRepository {
    override fun userLogin(username: String,password: String): Flow<AuthResponse> = flow {
        val user = LoginRequest(username, password)
        val response = apiClient.userLogin(user)
        when {
            response.isSuccessful && response.body() != null -> {
                emit(AuthResponse.Success(response.body()?.token ?: ""))
            }
            else -> {
                emit(AuthResponse.Failed(resourceProvider.getString(R.string.generic_error_msg)))
            }
        }
    }
}


sealed class AuthResponse {
    data class Success(val token: String) : AuthResponse()
    data class Failed(val errorMsg: String) : AuthResponse()

}