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
import com.android.model.Category
import com.android.model.ProductDomain
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
class AllProductsScreenViewModelTest : RobolectricTest() {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    @Spy
    private lateinit var productsInteractor: ProductsInteractor

    @Spy
    private lateinit var resourceProvider: ResourceProvider

    @Mock
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
        MockitoAnnotations.openMocks(this)
        Mockito.`when`(resourceProvider.getString(R.string.all_category)).thenReturn("All")
        viewModel = AllProductsScreenViewModel(productsInteractor, resourceProvider, sessionManager)
    }

    @After
    fun tearDown() {
        coroutineRule.cancelScopeAndDispatcher()
    }

    @Test
    fun `Given Success partial state When GetAllProducts Then updates state and emits GetFavorites effect`() =
        coroutineRule.runTest {
            val products = sampleList
            Mockito.`when`(productsInteractor.getAllProducts())
                .thenReturn(flowOf(AllProductsPartialState.Success(products)))
            Mockito.`when`(resourceProvider.getString(R.string.all_category)).thenReturn("All")
            Mockito.`when`(sessionManager.getCurrentUserId()).thenReturn("user123")

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
            Mockito.`when`(productsInteractor.getAllProducts())
                .thenReturn(flowOf(AllProductsPartialState.Failed(errorMsg)))

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
            Mockito.`when`(productsInteractor.getAllProducts())
                .thenReturn(flowOf(AllProductsPartialState.NoData(errorMsg)))

            viewModel.setEvent(Event.GetAllProducts)

            viewModel.viewStateHistory.runFlowTest {
                val state = awaitItem()
                assertEquals(false, state.isLoading)
            }
            viewModel.effect.runFlowTest {
                assertEquals(Effect.ShowMessage(errorMsg), awaitItem())
            }
        }

//    @Test
//    fun `When OnCategoryClick with 'all' category Then sets filteredProducts to all products and loading false`() = coroutineRule.runTest {
//        Mockito.`when`(productsInteractor.getAllProducts())
//            .thenReturn(flowOf(AllProductsPartialState.Success(sampleList)))
//        viewModel.setEvent(Event.GetAllProducts)
//
//        viewModel.viewStateHistory.runFlowTest {
//            awaitItem() // initial state
//            val loadedState = awaitItem() // state with products loaded
//
//            viewModel.setEvent(Event.OnCategoryCLick(Category("All", "all"), loadedState.originalProducts))
//            val afterClick = awaitItem()
//            assertEquals(
//                loadedState.copy(
//                    isLoading = false,
//                    filteredProducts = sampleList
//                ),
//                afterClick
//            )
//        }
//    }
//
//    @Test
//    fun `When OnCategoryClick with specific category Then sets filteredProducts to filtered list and loading false`() =
//        coroutineRule.runTest {
//            val category = Category("Cat1", "cat1")
//            val products = sampleList
//            viewModel.setEvent(Event.OnCategoryCLick(category, products))
//            viewModel.viewStateHistory.runFlowTest {
//                val state = awaitItem()
//                assertEquals(
//                    initialState.copy(
//                        filteredProducts = sampleList.filter { it.category == "cat1" },
//                        isLoading = false
//                    ),
//                    state
//                )
//            }
//        }

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
            Mockito.`when`(productsInteractor.getFavorites(userId))
                .thenReturn(flowOf(FavoritesPartialState.Success(listOf(1))))
            viewModel.setEvent(Event.GetFavorites(userId, products))
            viewModel.viewStateHistory.runFlowTest {
                val state = awaitItem()
                assertEquals(
                    state.filteredProducts?.find { it.id == 1 }?.isFavorite,
                    true
                )
            }
        }

//    @Test
//    fun `When HandleFavorites succeeds Then toggles isFavorite and sets loading false`() =
//        coroutineRule.runTest {
//            val userId = "user123"
//            Mockito.`when`(sessionManager.getCurrentUserId()).thenReturn(userId)
//            Mockito.`when`(productsInteractor.handleFavorites(userId, 1, false))
//                .thenReturn(flowOf(FavoritesPartialState.Success(listOf(1))))
//
//            viewModel.setEvent(Event.HandleFavorites(1, false))
//
//            viewModel.viewStateHistory.runFlowTest {
//                val state = awaitItem()
//                assertEquals(
//                    initialState.copy(
//                        isLoading = false,
//                        filteredProducts = sampleList.map {
//                            if (it.id == 1) it.copy(isFavorite = true) else it
//                        }
//                    ), state)
//            }
//        }
}


