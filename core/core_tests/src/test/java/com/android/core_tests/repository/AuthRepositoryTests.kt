package com.android.core_tests.repository

import com.android.core.core_api.api.ApiClient
import com.android.core.core_data.repository.AuthRepository
import com.android.core.core_data.repository.AuthRepositoryImpl
import com.android.core.core_data.repository.AuthResponse
import com.android.core_model.AuthDto
import com.android.core_model.LoginRequest
import com.android.core_resources.provider.ResourceProvider
import com.android.core_tests.CoroutineTestRule
import com.android.core_tests.runFlowTest
import com.android.core_tests.runTest
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.Spy
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class TestAuthRepositoryImpl {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    @Spy
    private lateinit var apiClient: ApiClient

    @Spy
    private lateinit var resourceProvider: ResourceProvider

    private lateinit var authRepository: AuthRepository

    private val genericError = "Generic Error"

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        authRepository = AuthRepositoryImpl(apiClient, resourceProvider)
        Mockito.`when`(resourceProvider.getString(anyInt()))
            .thenReturn(genericError)
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
            Mockito.`when`(apiClient.userLogin(LoginRequest("u", "p")))
                .thenReturn(response)

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
            Mockito.`when`(apiClient.userLogin(LoginRequest("u", "p")))
                .thenReturn(response)

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
            val response = Response.success<AuthDto?>(null)
            Mockito.`when`(apiClient.userLogin(LoginRequest("u", "p")))
                .thenReturn(response)

            // When / Then
            authRepository.userLogin("u", "p").runFlowTest {
                assertEquals(
                    AuthResponse.Failed(genericError),
                    awaitItem()
                )
            }
        }
}
