package com.android.core_tests.repository

import com.android.api.ApiClient
import com.android.api.AuthRepository
import com.android.api.AuthResponse
import com.android.api.ResourceProvider
import com.android.core_model.AuthDto
import com.android.core_model.LoginRequest
import com.android.core_tests.CoroutineTestRule
import com.android.core_tests.runFlowTest
import com.android.core_tests.runTest
import com.android.impl.AuthRepositoryImpl
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.spyk
import junit.framework.TestCase.assertEquals
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

class TestAuthRepositoryImpl {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private lateinit var apiClient: ApiClient
    private lateinit var resourceProvider: ResourceProvider

    private lateinit var authRepository: AuthRepository

    private val genericError = "Generic Error"

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        apiClient = spyk()
        resourceProvider = spyk()
        authRepository = AuthRepositoryImpl(apiClient, resourceProvider)
        coEvery { resourceProvider.getString(any()) } returns genericError
    }

    @After
    fun tearDown() {
        coroutineRule.cancelScopeAndDispatcher()
    }

    @Test
    fun `userLogin emits Success when apiClient returns successful response with body`() =
        coroutineRule.runTest {
            // Given
            val token = "abc123"
            val authDto = AuthDto(token = token)
            val response = Response.success(authDto)
            coEvery { apiClient.userLogin(LoginRequest("u", "p")) } returns response

            // When / Then
            authRepository.userLogin("u", "p").runFlowTest {
                assertEquals(
                    AuthResponse.Success(token),
                    awaitItem()
                )
            }
        }

    @Test
    fun `userLogin emits Failed when apiClient response is not successful`() =
        coroutineRule.runTest {
            // Given
            val errorBody = "error".toResponseBody("text/plain".toMediaType())
            val response = Response.error<AuthDto>(400, errorBody)
            coEvery { apiClient.userLogin(LoginRequest("u", "p"))} returns response

            // When / Then
            authRepository.userLogin("u", "p").runFlowTest {
                assertEquals(
                    AuthResponse.Failed(genericError),
                    awaitItem()
                )
            }
        }

    @Test
    fun `userLogin emits Failed when apiClient returns successful response with null body`() =
        coroutineRule.runTest {
            // Given
            val response = Response.success<AuthDto>(null)
            coEvery { apiClient.userLogin(LoginRequest("u", "p"))} returns response

            // When / Then
            authRepository.userLogin("u", "p").runFlowTest {
                assertEquals(
                    AuthResponse.Failed(genericError),
                    awaitItem()
                )
            }
        }
}
