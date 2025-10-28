package com.android.feature_tests.viewModel

import com.android.api.AllProductsPartialState
import com.android.api.FavoritesPartialState
import com.android.api.ProductsInteractor
import com.android.api.ResourceProvider
import com.android.fakestore.core.core_resources.R
import com.android.feature_all_products.ui.AllProductsScreenViewModel
import com.android.feature_all_products.ui.Effect
import com.android.feature_all_products.ui.Event
import com.android.feature_all_products.ui.State
import com.android.feature_tests.CoroutineTestRule
import com.android.feature_tests.RobolectricTest
import com.android.feature_tests.runFlowTest
import com.android.feature_tests.runTest
import com.android.model.ProductDomain
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

class AllProductsScreenViewModelTest : RobolectricTest() {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private lateinit var productsInteractor: ProductsInteractor
    private lateinit var resourceProvider: ResourceProvider
    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: AllProductsScreenViewModel

    private val sampleList = listOf(
        ProductDomain(
            id = 1,
            title = "Apple",
            price = 1.0f,
            description = "",
            category = "cat1",
            image = "",
            rating = null
        ),
        ProductDomain(
            id = 2,
            title = "Banana",
            price = 2.0f,
            description = "",
            category = "cat2",
            image = "",
            rating = null
        )
    )

    private val initialState = State(isLoading = true, filteredProducts = sampleList)


    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        productsInteractor = spyk()
        resourceProvider = spyk()
        sessionManager = mockk()
        every { resourceProvider.getString(R.string.all_category) } returns "All"
        every { (sessionManager.getCurrentUserId()) }.returns("user123")

        viewModel = AllProductsScreenViewModel(productsInteractor, resourceProvider, sessionManager)
    }

    @After
    fun tearDown() {
        coroutineRule.cancelScopeAndDispatcher()
        clearAllMocks()
    }

    @Test
    fun `Given Success partial state When GetAllProducts Then updates state and emits GetFavorites effect`() =
        coroutineRule.runTest {
            val products = sampleList
            coEvery { productsInteractor.getAllProducts() } returns flowOf(
                AllProductsPartialState.Success(
                    products
                )
            )

            viewModel.setEvent(Event.GetAllProducts)

            viewModel.viewStateHistory.runFlowTest {
                val state = awaitItem()
                assertEquals(
                    state.categories?.map { it.categoryName },
                    listOf("All", "Cat1", "Cat2")
                )
            }
            viewModel.effect.runFlowTest {
                assertEquals(
                    Effect.GetFavorites("user123", products),
                    awaitItem()
                )
            }
        }

    @Test
    fun `Given Failed partial state When GetAllProducts Then sets loading false and emits ShowMessage`() =
        coroutineRule.runTest {
            val errorMsg = "error"
            coEvery { productsInteractor.getAllProducts() } returns flowOf(
                AllProductsPartialState.Failed(
                    errorMsg
                )
            )

            viewModel.setEvent(Event.GetAllProducts)

            viewModel.viewStateHistory.runFlowTest {
                val state = awaitItem()
                assertEquals(false, state.isLoading)
            }
            viewModel.effect.runFlowTest {
                assertEquals(Effect.ShowMessage(errorMsg), awaitItem())
            }
        }

    @Test
    fun `Given NoData partial state When GetAllProducts Then sets loading false and emits ShowMessage`() =
        coroutineRule.runTest {
            val errorMsg = "no data"
            coEvery { productsInteractor.getAllProducts() } returns flowOf(
                AllProductsPartialState.NoData(
                    errorMsg
                )
            )

            viewModel.setEvent(Event.GetAllProducts)

            viewModel.viewStateHistory.runFlowTest {
                val state = awaitItem()
                assertEquals(false, state.isLoading)
            }
            viewModel.effect.runFlowTest {
                assertEquals(Effect.ShowMessage(errorMsg), awaitItem())
            }
        }

    @Test
    fun `When OnSearch filters by prefix`() = coroutineRule.runTest {
        viewModel.setEvent(Event.OnSearch(query = "A", allProducts = sampleList))

        viewModel.viewStateHistory.runFlowTest {
            val state = awaitItem()
            assertEquals(
                state, initialState.copy(
                    isLoading = false,
                    searchQuery = "A",
                    filteredProducts = sampleList.filter { it.title?.startsWith("A") == true }
                )
            )
        }
    }

    @Test
    fun `Given Success partial state When GetFavorites Then updates state with favorites`() =
        coroutineRule.runTest {
            val userId = "user123"
            val products = sampleList
            coEvery { productsInteractor.getFavorites(userId) } returns flowOf(
                FavoritesPartialState.Success(
                    listOf(1)
                )
            )
            viewModel.setEvent(Event.GetFavorites(userId, products))
            viewModel.viewStateHistory.runFlowTest {
                val state = awaitItem()
                assertEquals(
                    state.filteredProducts?.find { it.id == 1 }?.isFavorite,
                    true
                )
            }
        }
}


