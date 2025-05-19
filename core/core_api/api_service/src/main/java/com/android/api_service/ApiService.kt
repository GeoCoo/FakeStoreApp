package com.android.api_service

import com.android.core_model.AuthDto
import com.android.core_model.LoginRequest
import com.android.core_model.ProductDto
import com.android.core_model.UpdateProduct
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


const val baseUrl = "https://fakestoreapi.com/"

interface ApiService {
    @GET("products")
    suspend fun retrieveProducts(): Response<List<ProductDto>?>

    @GET("products/{productId}")
    suspend fun retrieveSingleProduct(
        @Path("productId") productId: Int
    ): Response<ProductDto>

    @POST("auth/login")
    suspend fun userLogin(
        @Body request: LoginRequest
    ): Response<AuthDto>

    @PUT("products/{id}")
    suspend fun updateProduct(
        @Path("id") productId: Int,
        @Body update: UpdateProduct
    ): Response<ProductDto>
}