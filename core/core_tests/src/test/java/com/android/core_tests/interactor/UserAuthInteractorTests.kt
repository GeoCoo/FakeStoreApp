package com.android.core_tests.interactor

import com.android.core.core_data.repository.AuthRepository
import com.android.core.core_data.repository.AuthResponse
import com.android.core.core_domain.interactor.AuthResponsePartialState
import com.android.core.core_domain.interactor.UserAuthInteractor
import com.android.core.core_domain.interactor.UserAuthInteractorImpl
import com.android.core_tests.CoroutineTestRule
import com.android.core_tests.runFlowTest
import com.android.core_tests.runTest
import com.android.core_tests.toFlow
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

    private lateinit var interactor: UserAuthInteractor

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        interactor = UserAuthInteractorImpl(authRepository)
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