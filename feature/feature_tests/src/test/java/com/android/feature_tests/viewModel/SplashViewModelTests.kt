package com.android.feature_tests.viewModel

import com.android.feature_splash.ui.Effect
import com.android.feature_splash.ui.Event
import com.android.feature_splash.ui.SplashViewModel
import com.android.feature_splash.ui.State
import com.android.feature_tests.CoroutineTestRule
import com.android.feature_tests.RobolectricTest
import com.android.feature_tests.runFlowTest
import com.android.feature_tests.runTest
import com.android.session.SessionManager
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest : RobolectricTest() {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private lateinit var sessionManager: SessionManager

    private lateinit var viewModel: SplashViewModel
    private val initialState = State(isLoading = true)

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        sessionManager = mockk()
        viewModel = SplashViewModel(sessionManager)
    }

    @After
    fun tearDown() {
        coroutineRule.cancelScopeAndDispatcher()
    }

    @Test
    fun `Given empty token When CheckToken Then emits Navigate(false) effect`() =
        coroutineRule.runTest {
            every {  sessionManager.getCurrentToken()} returns ("")
            viewModel.setEvent(Event.CheckToken)

            verify(exactly = 1) { sessionManager.getCurrentToken() }

            viewModel.viewStateHistory.runFlowTest {
                val state = awaitItem()
                assertEquals(state, initialState.copy(isLoading = false))

                viewModel.effect.runFlowTest {
                    assertEquals(awaitItem(), Effect.Navigate(false))
                }
            }
        }

    @Test
    fun `Given non-empty token When CheckToken Then emits Navigate(true) effect`() =
        coroutineRule.runTest {
            val token = "token_123"
            every { sessionManager.getCurrentToken()} returns token

            viewModel.setEvent(Event.CheckToken)
            verify(exactly = 1) { sessionManager.getCurrentToken() }

            viewModel.viewStateHistory.runFlowTest {
                val state = awaitItem()
                assertEquals(state, initialState.copy(isLoading = false))

                viewModel.effect.runFlowTest {
                    assertEquals(awaitItem(), Effect.Navigate(true))
                }
            }
        }
}
