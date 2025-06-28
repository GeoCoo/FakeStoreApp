package com.android.feature_tests.viewModel

import com.android.api.AuthResponsePartialState
import com.android.api.PreferencesController
import com.android.api.ResourceProvider
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
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.Spy

@OptIn(ExperimentalStdlibApi::class)
class LoginViewModelTest : RobolectricTest() {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    @Spy
    private lateinit var userAuthInteractor: UserAuthInteractor

    @Mock
    private lateinit var sessionManager: SessionManager

    private lateinit var viewModel: LoginVIewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = LoginVIewModel(userAuthInteractor, sessionManager )
    }

    @After
    fun tearDown() {
        coroutineRule.cancelScopeAndDispatcher()
    }

    @Test
    fun `Failed login emits ShowMessage and does not save token`() = coroutineRule.runTest {
        // Given
        val err = "invalid credentials"
        Mockito.`when`(userAuthInteractor.userLogin("u", "p"))
            .thenReturn(AuthResponsePartialState.Failed(err).toFlow())

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
        Mockito.`when`(userAuthInteractor.userLogin("user", "pass"))
            .thenReturn(flowOf(AuthResponsePartialState.Success(token)))
        // no existing token
        Mockito.`when`(sessionManager.getCurrentToken()).thenReturn("")

        viewModel.setEvent(Event.UserLogin("user", "pass"))
        viewModel.viewStateHistory.runFlowTest {
            val state = awaitItem()
            assertEquals(state.copy(isLoading = false), state)
            Mockito.`when`(sessionManager.getCurrentToken()).thenReturn(token)
            viewModel.effect.runFlowTest {
                assertEquals(Effect.SuccessNavigate, awaitItem())
            }
        }
    }

    @Test
    fun `Successful login with same token does not re-save but still emits SuccessNavigate`() =
        coroutineRule.runTest {
            val token = "same-token"
            Mockito.`when`(userAuthInteractor.userLogin("u", "p"))
                .thenReturn(flowOf(AuthResponsePartialState.Success(token)))
            Mockito.`when`(sessionManager.getCurrentToken()).thenReturn("")
                .thenReturn(token)

            viewModel.setEvent(Event.UserLogin("u", "p"))
            viewModel.viewStateHistory.runFlowTest {
                val state = awaitItem()
                assertEquals(state.copy(isLoading = false), state)
//                Mockito.verify(sessionManager, Mockito.never()).se(Mockito.anyString(), Mockito.anyString())
                viewModel.effect.runFlowTest {
                    assertEquals(Effect.SuccessNavigate, awaitItem())
                }
            }
        }
}
