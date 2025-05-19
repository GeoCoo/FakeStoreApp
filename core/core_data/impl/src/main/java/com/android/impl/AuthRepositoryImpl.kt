package com.android.impl

import com.android.api.ApiClient
import com.android.api.AuthRepository
import com.android.api.AuthResponse
import com.android.api.ResourceProvider
import com.android.core_model.LoginRequest
import com.android.fakestore.core.core_resources.provider.impl.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

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