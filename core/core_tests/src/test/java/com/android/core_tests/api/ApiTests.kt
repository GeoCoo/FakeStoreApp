package com.android.core_tests.api

import com.android.core.core_api.api.ApiClient
import com.android.core.core_api.api.ApiClientImpl
import com.android.core.core_api.api.ApiService
import com.android.core_model.LoginRequest
import com.android.core_model.UpdateProduct
import com.android.core_tests.CoroutineTestRule
import com.android.core_tests.runTest
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@OptIn(ExperimentalCoroutinesApi::class)
class ApiClientTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService
    private lateinit var apiClient: ApiClient

    @Before
    fun setUp() {
        mockWebServer = MockWebServer().apply { start() }
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)
        apiClient = ApiClientImpl(apiService)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        coroutineRule.cancelScopeAndDispatcher()
    }

    @Test
    fun `Given valid products JSON, When retrieveProducts is called, Then it returns parsed list`() =
        coroutineRule.runTest {
            // Given
            val json = """
                [
                  {
                    "id": 1,
                    "title": "Test Product",
                    "price": 9.99,
                    "description": "Desc",
                    "category": "Cat",
                    "image": "https://example.com/img.png",
                    "rating": {"rate":4.5,"count":10}
                  }
                ]
            """.trimIndent()
            mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(json))

            // When
            val response = apiClient.retrieveProducts()

            // Then
            assertTrue(response.isSuccessful)
            val list = response.body()
            assertNotNull(list)
            assertEquals(1, list!!.size)
            val dto = list[0]
            assertEquals(1, dto.id)
            assertEquals("Test Product", dto.title)
            assertEquals(9.99f, dto.price)
            assertEquals("Desc", dto.description)
            assertEquals("Cat", dto.category)
            assertEquals("https://example.com/img.png", dto.image)
        }

    @Test
    fun `Given error response for retrieveProducts, When retrieveProducts is called, Then response is not successful`() =
        coroutineRule.runTest {
            // Given
            mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("Not Found"))

            // When
            val response = apiClient.retrieveProducts()

            // Then
            assertFalse(response.isSuccessful)
            assertEquals(404, response.code())
        }

    @Test
    fun `Given valid single product JSON, When retrieveSingleProduct is called, Then it returns parsed product`() =
        coroutineRule.runTest {
            // Given
            val id = 42
            val json = """
                {
                  "id": $id,
                  "title": "Single",
                  "price": 19.99,
                  "description": "Desc2",
                  "category": "Cat2",
                  "image": "https://example.com/img2.png",
                  "rating": {"rate":3.2,"count":5}
                }
            """.trimIndent()
            mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(json))

            // When
            val response = apiClient.retrieveSingleProduct(id)

            // Then
            assertTrue(response.isSuccessful)
            val dto = response.body()
            assertNotNull(dto)
            assertEquals(id, dto!!.id)
            assertEquals("Single", dto.title)
        }

    @Test
    fun `Given error response for retrieveSingleProduct, When retrieveSingleProduct is called, Then response is not successful`() =
        coroutineRule.runTest {
            // Given
            mockWebServer.enqueue(MockResponse().setResponseCode(500))

            // When
            val response = apiClient.retrieveSingleProduct(1)

            // Then
            assertFalse(response.isSuccessful)
            assertEquals(500, response.code())
        }

    @Test
    fun `Given valid login JSON, When userLogin is called, Then it returns parsed AuthDto`() =
        coroutineRule.runTest {
            // Given
            val req = LoginRequest(username = "user", password = "pass")
            val json = """{"token":"abc123"}"""
            mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(json))

            // When
            val response = apiClient.userLogin(req)

            // Then
            assertTrue(response.isSuccessful)
            val auth = response.body()
            assertNotNull(auth)
            assertEquals("abc123", auth!!.token)
        }

    @Test
    fun `Given error response for userLogin, When userLogin is called, Then response is not successful`() =
        coroutineRule.runTest {
            // Given
            mockWebServer.enqueue(MockResponse().setResponseCode(401))

            // When
            val response = apiClient.userLogin(LoginRequest("u", "p"))

            // Then
            assertFalse(response.isSuccessful)
            assertEquals(401, response.code())
        }

    @Test
    fun `Given valid updateProduct JSON, When updateProduct is called, Then it returns parsed product`() =
        coroutineRule.runTest {
            // Given
            val id = 7
            val update = UpdateProduct(
                id = id,
                title = "Updated",
                price = 29.99f,
                description = "NewDesc",
                category = "NewCat",
                image = "https://example.com/new.png"
            )
            val json = """
                {
                  "id": $id,
                  "title": "${update.title}",
                  "price": ${update.price},
                  "description": "${update.description}",
                  "category": "${update.category}",
                  "image": "${update.image}",
                  "rating": {"rate":5.0,"count":1}
                }
            """.trimIndent()
            mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(json))

            // When
            val response = apiClient.updateProduct(id, update)

            // Then
            assertTrue(response.isSuccessful)
            val dto = response.body()
            assertNotNull(dto)
            assertEquals(id, dto!!.id)
            assertEquals("Updated", dto.title)
        }

    @Test
    fun `Given error response for updateProduct, When updateProduct is called, Then response is not successful`() =
        coroutineRule.runTest {
            // Given
            mockWebServer.enqueue(MockResponse().setResponseCode(400))

            // When
            val response = apiClient.updateProduct(1, UpdateProduct(1,"t",1f,null,null,null))

            // Then
            assertFalse(response.isSuccessful)
            assertEquals(400, response.code())
        }
}
