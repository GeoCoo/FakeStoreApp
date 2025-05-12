package com.android.feature_tests.viewModel

import com.android.core.core_domain.controller.PreferencesController
import com.android.feature_splash.ui.Effect
import com.android.feature_splash.ui.Event
import com.android.feature_splash.ui.SplashViewModel
import com.android.feature_splash.ui.State
import com.android.feature_tests.CoroutineTestRule
import com.android.feature_tests.RobolectricTest
import com.android.feature_tests.runFlowTest
import com.android.feature_tests.runTest
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.Spy
import org.mockito.kotlin.times

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest : RobolectricTest() {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    @Spy
    private lateinit var preferencesController: PreferencesController

    private lateinit var viewModel: SplashViewModel
    private val initialState = State(isLoading = true)

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = SplashViewModel(preferencesController)
    }

    @After
    fun tearDown() {
        coroutineRule.cancelScopeAndDispatcher()
    }

    @Test
    fun `Given empty token When CheckToken Then emits Navigate(false) effect`() =
        coroutineRule.runTest {
            Mockito.`when`(preferencesController.getString("user_token", "")).thenReturn("")
            viewModel.setEvent(Event.CheckToken)

            Mockito.verify(preferencesController, times(1)).getString("user_token", "")

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
            Mockito.`when`(preferencesController.getString("user_token", ""))
                .thenReturn("token_123")
            viewModel.setEvent(Event.CheckToken)
            Mockito.verify(preferencesController, times(1)).getString("user_token", "")

            viewModel.viewStateHistory.runFlowTest {
                val state = awaitItem()
                assertEquals(state, initialState.copy(isLoading = false))

                viewModel.effect.runFlowTest {
                    assertEquals(awaitItem(), Effect.Navigate(true))
                }
            }
        }
}
