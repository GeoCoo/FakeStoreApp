package com.android.core_tests.interactor

import com.android.api.AuthRepository
import com.android.api.AuthResponse
import com.android.api.AuthResponsePartialState
import com.android.api.ResourceProvider
import com.android.api.UserAuthInteractor
import com.android.core_tests.CoroutineTestRule
import com.android.core_tests.runFlowTest
import com.android.core_tests.runTest
import com.android.core_tests.toFlow
import com.android.impl.UserAuthInteractorImpl
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.Spy

@OptIn(ExperimentalCoroutinesApi::class)
class UserAuthInteractorTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    @Spy
    private lateinit var authRepository: AuthRepository

    @Spy
    private lateinit var resourcesProvider: ResourceProvider

    private lateinit var interactor: UserAuthInteractor

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        interactor = UserAuthInteractorImpl(authRepository,resourcesProvider)
    }

    @After
    fun tearDown() {
        coroutineRule.cancelScopeAndDispatcher()
    }

    @Test
    fun `Given Success from authRepository, When userLogin is called, Then emit Success state`() = coroutineRule.runTest {
        // Given
        val token = "abc123"
        Mockito.`when`(authRepository.userLogin(anyString(), anyString()))
            .thenReturn(AuthResponse.Success(token).toFlow())

        // When / Then
        interactor.userLogin("user", "pass").runFlowTest {
            assertEquals(
                AuthResponsePartialState.Success(token),
                awaitItem()
            )
        }
    }

    @Test
    fun `Given Failed from authRepository, When userLogin is called, Then emit Failed state`() = coroutineRule.runTest {
        // Given
        val error = "login failed"
        Mockito.`when`(authRepository.userLogin(anyString(), anyString()))
            .thenReturn(AuthResponse.Failed(error).toFlow())

        // When / Then
        interactor.userLogin("user", "pass").runFlowTest {
            assertEquals(
                AuthResponsePartialState.Failed(error),
                awaitItem()
            )
        }
    }
}