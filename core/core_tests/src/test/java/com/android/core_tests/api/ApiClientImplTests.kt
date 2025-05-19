package com.android.core_tests.api

import com.android.api.ApiClient
import com.android.api_service.ApiService
import com.android.core_model.AuthDto
import com.android.core_model.LoginRequest
import com.android.core_model.ProductDto
import com.android.core_model.UpdateProduct
import com.android.core_tests.CoroutineTestRule
import com.android.core_tests.runTest
import com.android.impl.ApiClientImpl
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.Response

class ApiClientImplTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private lateinit var apiService: ApiService
    private lateinit var apiClient: ApiClient

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        apiService = mock(ApiService::class.java)
        apiClient = ApiClientImpl(apiService)
    }

    @Test
    fun `retrieveProducts should call apiService and return its response`() = coroutineRule.runTest {
        // Given
        val expected = listOf(
            ProductDto(1, "Title", 1f, "Desc", "Cat", "img", null)
        )
        val response = Response.success(expected)
        `when`(apiService.retrieveProducts()).thenReturn(response)

        // When
        val result = apiClient.retrieveProducts()

        // Then
        verify(apiService, times(1)).retrieveProducts()
        assert(result === response)
    }

    @Test
    fun `retrieveSingleProduct should call apiService with id and return its response`() = coroutineRule.runTest {
        // Given
        val id = 42
        val dto = ProductDto(id, "Title", 2f, "Desc2", "Cat2", "img2", null)
        val response = Response.success(dto)
        `when`(apiService.retrieveSingleProduct(id)).thenReturn(response)

        // When
        val result = apiClient.retrieveSingleProduct(id)

        // Then
        verify(apiService, times(1)).retrieveSingleProduct(id)
        assert(result === response)
    }

    @Test
    fun `userLogin should call apiService with request and return its response`() = coroutineRule.runTest {
        // Given
        val request = LoginRequest("user", "pass")
        val dto = AuthDto("token123")
        val response = Response.success(dto)
        `when`(apiService.userLogin(request)).thenReturn(response)

        // When
        val result = apiClient.userLogin(request)

        // Then
        verify(apiService, times(1)).userLogin(request)
        assert(result === response)
    }

    @Test
    fun `updateProduct should call apiService with id and update and return its response`() = coroutineRule.runTest {
        // Given
        val id = 7
        val update = UpdateProduct(
            id = id,
            title = "New",
            price = 3.14f,
            description = "d",
            category = "c",
            image = "i"
        )
        val dto = ProductDto(id, update.title, update.price, update.description ?: "", update.category ?: "", update.image
            ?: "", null)
        val response = Response.success(dto)
        `when`(apiService.updateProduct(id, update)).thenReturn(response)

        // When
        val result = apiClient.updateProduct(id, update)

        // Then
        verify(apiService, times(1)).updateProduct(id, update)
        assert(result === response)
    }
}
