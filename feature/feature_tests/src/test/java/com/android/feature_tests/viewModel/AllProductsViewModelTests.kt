package com.android.feature_tests.viewModel

import com.android.core.core_domain.interactor.AllProductsPartialState
import com.android.core.core_domain.interactor.ProductsInteractor
import com.android.core.core_domain.model.Category
import com.android.core.core_domain.model.ProductDomain
import com.android.core_resources.provider.ResourceProvider
import com.android.fakestore.core.core_resources.R
import com.android.feature_all_products.ui.AllProductsScreenViewModel
import com.android.feature_all_products.ui.Effect
import com.android.feature_all_products.ui.Event
import com.android.feature_all_products.ui.State
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
import org.mockito.kotlin.times


@OptIn(ExperimentalStdlibApi::class)
class AllProductsScreenViewModelTest : RobolectricTest() {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    @Spy
    private lateinit var productsInteractor: ProductsInteractor

    @Spy
    private lateinit var resourceProvider: ResourceProvider

    private lateinit var viewModel: AllProductsScreenViewModel
    private val initialState = State(isLoading = true)

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

    private fun getMockCategories(): List<Category> = buildList {
        add(Category("All", "all"))
        add(Category("Cat1", "cat1"))
        add(Category("Cat2", "cat2"))
    }

    private val allCategoryName = Category("All", "all")

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        // stub the "All" label
        Mockito.`when`(resourceProvider.getString(R.string.all_category))
            .thenReturn("All")
        viewModel = AllProductsScreenViewModel(productsInteractor, resourceProvider)
    }

    @After
    fun tearDown() {
        coroutineRule.cancelScopeAndDispatcher()
    }

    @Test
    fun `Given Success partial state When GetAllProducts Then then Assert State and Effects`() =
        coroutineRule.runTest {
            Mockito.`when`(productsInteractor.getAllProducts())
                .thenReturn(AllProductsPartialState.Success(sampleList).toFlow())
            viewModel.setEvent(Event.GetAllProducts)

            Mockito.verify(productsInteractor, times(1)).getAllProducts()

            viewModel.viewStateHistory.runFlowTest {
                val state = awaitItem()
                assertEquals(
                    state, initialState.copy(
                        originalProducts = sampleList,
                        filteredProducts = sampleList,
                        categories = getMockCategories(),
                        isLoading = false
                    )
                )
            }
        }

    @Test
    fun `Given Failed partial state When GetAllProducts Then emit loading false and ShowMessage`() =
        coroutineRule.runTest {
            val errMsg = "network error"
            Mockito.`when`(productsInteractor.getAllProducts())
                .thenReturn(flowOf(AllProductsPartialState.Failed(errMsg)))

            viewModel.setEvent(Event.GetAllProducts)

            Mockito.verify(productsInteractor, times(1)).getAllProducts()

            viewModel.viewStateHistory.runFlowTest {
                val state = awaitItem()
                assertEquals(
                    state, initialState.copy(
                        isLoading = false,
                    )
                )

                viewModel.effect.runFlowTest {
                    assertEquals(Effect.ShowMessage(errMsg), awaitItem())
                }
            }

        }

    @Test
    fun `Given NoData partial state When GetAllProducts Then emit loading false and ShowMessage`() =
        coroutineRule.runTest {
            val noDataMsg = "nothing here"
            Mockito.`when`(productsInteractor.getAllProducts())
                .thenReturn(flowOf(AllProductsPartialState.NoData(noDataMsg)))
            viewModel.setEvent(Event.GetAllProducts)

            Mockito.verify(productsInteractor, times(1)).getAllProducts()

            viewModel.viewStateHistory.runFlowTest {
                val state = awaitItem()
                assertEquals(
                    state, initialState.copy(
                        isLoading = false,
                    )
                )
                viewModel.effect.runFlowTest {
                    assertEquals(Effect.ShowMessage(noDataMsg), awaitItem())
                }
            }
        }

    @Test
    fun `OnCategoryClick with 'all' returns all products`() = coroutineRule.runTest {
        Mockito.`when`(productsInteractor.getAllProducts())
            .thenReturn(flowOf(AllProductsPartialState.Success(sampleList)))

        viewModel.viewStateHistory.runFlowTest {
            viewModel.setEvent(Event.GetAllProducts)
            val state = awaitItem()

            viewModel.setEvent(Event.OnCategoryCLick(allCategoryName, state.originalProducts))
            val afterClick = awaitItem()
            assertEquals(
                state.copy(
                    isLoading = false,
                    filteredProducts = sampleList
                ),
                afterClick
            )
        }
    }


    @Test
    fun `OnCategoryClick with other than all category filters correctly`() = coroutineRule.runTest {
        Mockito.`when`(productsInteractor.getAllProducts())
            .thenReturn(flowOf(AllProductsPartialState.Success(sampleList)))

        viewModel.viewStateHistory.runFlowTest {
            viewModel.setEvent(Event.GetAllProducts)
            val state = awaitItem()

            viewModel.setEvent(Event.OnCategoryCLick(Category("Cat1","cat1"), state.originalProducts))
            val afterClick = awaitItem()
            assertEquals(
                state.copy(
                    isLoading = false,
                    filteredProducts = sampleList.filter { it.category == "cat1" }
                ),
                afterClick
            )
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
}
