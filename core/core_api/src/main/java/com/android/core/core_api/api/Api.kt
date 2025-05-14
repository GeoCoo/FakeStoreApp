package com.android.core.core_api.api

import com.android.core_model.AuthDto
import com.android.core_model.LoginRequest
import com.android.core_model.ProductDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import javax.inject.Inject


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
        @Body update: ProductDto
    ): Response<ProductDto>
}

interface ApiClient {
    suspend fun retrieveProducts(): Response<List<ProductDto>?>
    suspend fun retrieveSingleProduct(productId: Int): Response<ProductDto>
    suspend fun userLogin(user: LoginRequest): Response<AuthDto>
    suspend fun updateProduct(productId: Int, update: ProductDto): Response<ProductDto>
}

class ApiClientImpl @Inject constructor(private val apiService: ApiService) : ApiClient {
    override suspend fun retrieveProducts(): Response<List<ProductDto>?> =
        apiService.retrieveProducts()

    override suspend fun retrieveSingleProduct(productId: Int): Response<ProductDto> =
        apiService.retrieveSingleProduct(productId)

    override suspend fun userLogin(user: LoginRequest): Response<AuthDto> =
        apiService.userLogin(user)

    override suspend fun updateProduct(
        productId: Int,
        update: ProductDto
    ): Response<ProductDto> = apiService.updateProduct(productId, update)
}