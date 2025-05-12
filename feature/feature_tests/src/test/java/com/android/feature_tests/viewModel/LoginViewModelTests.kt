package com.android.feature_tests.viewModel

import com.android.core.core_domain.controller.PreferencesController
import com.android.core.core_domain.interactor.AuthResponsePartialState
import com.android.core.core_domain.interactor.UserAuthInteractor
import com.android.feature_login.ui.Effect
import com.android.feature_login.ui.Event
import com.android.feature_login.ui.LoginVIewModel
import com.android.feature_tests.CoroutineTestRule
import com.android.feature_tests.RobolectricTest
import com.android.feature_tests.runFlowTest
import com.android.feature_tests.runTest
import com.android.feature_tests.toFlow
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.Spy

@OptIn(ExperimentalStdlibApi::class)
class LoginViewModelTest : RobolectricTest() {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    @Spy
    private lateinit var userAuthInteractor: UserAuthInteractor

    @Spy
    private lateinit var preferencesController: PreferencesController

    private lateinit var viewModel: LoginVIewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = LoginVIewModel(userAuthInteractor, preferencesController)
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
        Mockito.`when`(preferencesController.getString("user_token", ""))
            .thenReturn("")

        viewModel.setEvent(Event.UserLogin("user", "pass"))
        viewModel.viewStateHistory.runFlowTest {
            val state = awaitItem()
            assertEquals(state.copy(isLoading = false), state)
            Mockito.verify(preferencesController).setString("user_token", token)
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
            Mockito.`when`(preferencesController.getString("user_token", ""))
                .thenReturn(token)

            viewModel.setEvent(Event.UserLogin("u", "p"))
            viewModel.viewStateHistory.runFlowTest {
                val state = awaitItem()
                assertEquals(state.copy(isLoading = false), state)
                Mockito.verify(preferencesController, Mockito.never())
                    .setString(Mockito.anyString(), Mockito.anyString())
                viewModel.effect.runFlowTest {
                    assertEquals(Effect.SuccessNavigate, awaitItem())
                }
            }
        }
}
