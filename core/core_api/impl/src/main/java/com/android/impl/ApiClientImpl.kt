package com.android.impl

import com.android.api.ApiClient
import com.android.api_service.ApiService
import com.android.core_model.AuthDto
import com.android.core_model.LoginRequest
import com.android.core_model.ProductDto
import com.android.core_model.UpdateProduct
import retrofit2.Response
import javax.inject.Inject

class ApiClientImpl @Inject constructor(private val apiService: ApiService) : ApiClient {
    override suspend fun retrieveProducts(): Response<List<ProductDto>?> =
        apiService.retrieveProducts()

    override suspend fun retrieveSingleProduct(productId: Int): Response<ProductDto> =
        apiService.retrieveSingleProduct(productId)

    override suspend fun userLogin(user: LoginRequest): Response<AuthDto> =
        apiService.userLogin(user)

    override suspend fun updateProduct(
        productId: Int,
        updateProduct: UpdateProduct
    ): Response<ProductDto> = apiService.updateProduct(productId, updateProduct)
}