package com.android.feature_tests.viewModel

import com.android.api.ProductsInteractor
import com.android.api.ResourceProvider
import com.android.api.SingleProductsPartialState
import com.android.feature_single_product.ui.Effect
import com.android.feature_single_product.ui.Event
import com.android.feature_single_product.ui.SingleProductVIewModel
import com.android.feature_single_product.ui.State
import com.android.feature_tests.CoroutineTestRule
import com.android.feature_tests.RobolectricTest
import com.android.feature_tests.runFlowTest
import com.android.feature_tests.runTest
import com.android.feature_tests.toFlow
import com.android.model.ProductDomain
import com.android.session.SessionManager
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.Spy

@OptIn(ExperimentalStdlibApi::class)
class SingleProductViewModelTest : RobolectricTest() {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    @Spy
    private lateinit var productsInteractor: ProductsInteractor

    @Mock
    private lateinit var sessionManager: SessionManager


    private lateinit var viewModel: SingleProductVIewModel
    private val initialState = State(isLoading = true, product = null)

    private val sampleProduct = ProductDomain(
        id = 123,
        title = "Test",
        price = 4.5f,
        description = "Desc",
        category = "cat",
        image = "img",
        rating = null
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = SingleProductVIewModel(productsInteractor,sessionManager)
    }

    @After
    fun tearDown() {
        coroutineRule.cancelScopeAndDispatcher()
    }

    @Test
    fun `Given Success partial state When GetProduct Then assert state and effects`() =
        coroutineRule.runTest {
            Mockito.`when`(productsInteractor.getSingleProduct(anyInt())).thenReturn(
                SingleProductsPartialState.Success(sampleProduct.copy(isFavorite = true)).toFlow()
            )

            viewModel.setEvent(Event.GetProduct(true, anyInt()))

            viewModel.viewStateHistory.runFlowTest {
                val state = awaitItem()
                assertEquals(state, initialState.copy(isLoading = false, product = sampleProduct.copy(isFavorite = true)))
            }
        }
    @Test
    fun `Given Failed partial state When GetProduct Then assert state and effects`() =
        coroutineRule.runTest {
            val err = "error!"
            Mockito.`when`(productsInteractor.getSingleProduct(anyInt())).thenReturn(
                SingleProductsPartialState.Failed(err).toFlow()
            )
            viewModel.setEvent(Event.GetProduct(true,anyInt()))

            viewModel.viewStateHistory.runFlowTest {
                val state = awaitItem()
                assertEquals(state, initialState.copy(isLoading = false))
            }
            viewModel.effect.runFlowTest {
                assertEquals(Effect.ShowMessage(err), awaitItem())
            }
        }
}
