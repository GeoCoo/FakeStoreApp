package com.android.feature_tests.viewModel

import com.android.api.AuthResponsePartialState
import com.android.api.UserAuthInteractor
import com.android.feature_login.ui.Effect
import com.android.feature_login.ui.Event
import com.android.feature_login.ui.LoginVIewModel
import com.android.feature_tests.CoroutineTestRule
import com.android.feature_tests.RobolectricTest
import com.android.feature_tests.runFlowTest
import com.android.feature_tests.runTest
import com.android.feature_tests.toFlow
import com.android.session.SessionManager
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest : RobolectricTest() {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private lateinit var userAuthInteractor: UserAuthInteractor
    private lateinit var sessionManager: SessionManager

    private lateinit var viewModel: LoginVIewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        userAuthInteractor = spyk()
        sessionManager = mockk()
        viewModel = LoginVIewModel(userAuthInteractor, sessionManager)
    }

    @After
    fun tearDown() {
        coroutineRule.cancelScopeAndDispatcher()
        clearAllMocks()
    }

    @Test
    fun `Failed login emits ShowMessage and does not save token`() = coroutineRule.runTest {
        // Given
        val err = "invalid credentials"
        coEvery {
            (userAuthInteractor.userLogin(
                "u",
                "p"
            ))
        } returns (AuthResponsePartialState.Failed(err).toFlow())

        viewModel.setEvent(Event.UserLogin("u", "p"))

        viewModel.viewStateHistory.runFlowTest {
            val state = awaitItem()
            assertEquals(state.copy(isLoading = false), state)
            viewModel.effect.runFlowTest {
                assertEquals(Effect.ShowMessage(err), awaitItem())
            }
        }
    }

    @Test
    fun `Successful login saves new token and emits SuccessNavigate`() = coroutineRule.runTest {
        // Given
        val token = "abc123"
        coEvery { (userAuthInteractor.userLogin("user", "pass")) } returns flowOf(
            AuthResponsePartialState.Success(token)
        )
        every { sessionManager.getCurrentToken() } returns ""
        every { sessionManager.login(token) } returns token
        viewModel.setEvent(Event.UserLogin("user", "pass"))

        viewModel.viewStateHistory.runFlowTest {
            val state = awaitItem()
            assertEquals(state.copy(isLoading = false), state)
            every { sessionManager.getCurrentToken() } returns token
            viewModel.effect.runFlowTest {
                assertEquals(Effect.SuccessNavigate, awaitItem())
            }
        }
    }

    @Test
    fun `Successful login with same token does not re-save but still emits SuccessNavigate`() =
        coroutineRule.runTest {
            val token = "same-token"
            coEvery { userAuthInteractor.userLogin("u", "p") } returns flowOf(
                AuthResponsePartialState.Success(token)
            )
            coEvery { sessionManager.getCurrentToken() } returns token
            every { sessionManager.login(token) } returns token

            viewModel.setEvent(Event.UserLogin("u", "p"))

            viewModel.viewStateHistory.runFlowTest {
                val state = awaitItem()
                assertEquals(state.copy(isLoading = false), state)
                viewModel.effect.runFlowTest {
                    assertEquals(Effect.SuccessNavigate, awaitItem())
                }
            }
        }
}
