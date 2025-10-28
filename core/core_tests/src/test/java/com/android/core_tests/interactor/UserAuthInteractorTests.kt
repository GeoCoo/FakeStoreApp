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
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.spyk
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class UserAuthInteractorTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private lateinit var authRepository: AuthRepository
    private lateinit var resourcesProvider: ResourceProvider

    private lateinit var interactor: UserAuthInteractor

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        authRepository = spyk()
        resourcesProvider = spyk()
        interactor = UserAuthInteractorImpl(authRepository,resourcesProvider)
    }

    @After
    fun tearDown() {
        coroutineRule.cancelScopeAndDispatcher()
        clearAllMocks()
    }

    @Test
    fun `Given Success from authRepository, When userLogin is called, Then emit Success state`() = coroutineRule.runTest {
        // Given
        val token = "abc123"
        every {  authRepository.userLogin(any(), any())} returns (AuthResponse.Success(token).toFlow())

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
        every { authRepository.userLogin(any(), any())} returns AuthResponse.Failed(error).toFlow()

        // When / Then
        interactor.userLogin("user", "pass").runFlowTest {
            assertEquals(
                AuthResponsePartialState.Failed(error),
                awaitItem()
            )
        }
    }
}