package com.android.feature_tests.viewModel

import com.android.api.PreferencesController
import com.android.api.ResourceProvider
import com.android.feature_splash.ui.Effect
import com.android.feature_splash.ui.Event
import com.android.feature_splash.ui.SplashViewModel
import com.android.feature_splash.ui.State
import com.android.feature_tests.CoroutineTestRule
import com.android.feature_tests.RobolectricTest
import com.android.feature_tests.runFlowTest
import com.android.feature_tests.runTest
import com.android.session.SessionManager
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.Spy
import org.mockito.kotlin.times

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest : RobolectricTest() {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    @Mock
    private lateinit var sessionManager: SessionManager

    private lateinit var viewModel: SplashViewModel
    private val initialState = State(isLoading = true)

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = SplashViewModel(sessionManager)
    }

    @After
    fun tearDown() {
        coroutineRule.cancelScopeAndDispatcher()
    }

    @Test
    fun `Given empty token When CheckToken Then emits Navigate(false) effect`() =
        coroutineRule.runTest {
            Mockito.`when`(sessionManager.getCurrentToken()).thenReturn("")
            viewModel.setEvent(Event.CheckToken)

            Mockito.verify(sessionManager, times(1)).getCurrentToken()

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
            Mockito.`when`(sessionManager.getCurrentToken()).thenReturn("token_123")

            viewModel.setEvent(Event.CheckToken)
            Mockito.verify(sessionManager, times(1)).getCurrentToken()

            viewModel.viewStateHistory.runFlowTest {
                val state = awaitItem()
                assertEquals(state, initialState.copy(isLoading = false))

                viewModel.effect.runFlowTest {
                    assertEquals(awaitItem(), Effect.Navigate(true))
                }
            }
        }
}
