package com.android.api

import com.android.core_model.AuthDto
import com.android.core_model.LoginRequest
import com.android.core_model.ProductDto
import com.android.core_model.UpdateProduct
import retrofit2.Response

interface ApiClient {
    suspend fun retrieveProducts(): Response<List<ProductDto>?>
    suspend fun retrieveSingleProduct(productId: Int): Response<ProductDto>
    suspend fun userLogin(user: LoginRequest): Response<AuthDto>
    suspend fun updateProduct(productId: Int, updateProduct: UpdateProduct): Response<ProductDto>
}