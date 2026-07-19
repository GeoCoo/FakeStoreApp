package com.android.feature_all_products.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.android.api.AllProductsPartialState
import com.android.api.FavoritesPartialState
import com.android.api.ProductsInteractor
import com.android.api.ResourceProvider
import com.android.core_ui.base.MviViewModel
import com.android.core_ui.base.ViewEvent
import com.android.core_ui.base.ViewSideEffect
import com.android.core_ui.base.ViewState
import com.android.fakestore.core.core_resources.R
import com.android.feature_all_products.ui.Effect.GetFavorites
import com.android.feature_all_products.ui.Effect.ShowMessage
import com.android.helpers.buildCategoryList
import com.android.model.Category
import com.android.model.ProductDomain
import com.android.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds


enum class ProductsViewMode {
    Grid, List
}

data class State(
    val isLoading: Boolean,
    val originalProducts: List<ProductDomain>? = listOf(),
    val filteredProducts: List<ProductDomain>? = listOf(),
    val categories: List<Category>? = listOf(),
    val selectedCategory: Category? = null,
    val searchQuery: String = "",
    val viewMode: ProductsViewMode = ProductsViewMode.Grid
) : ViewState

sealed class Event : ViewEvent {
    data object GetAllProducts : Event()
    data class OnCategoryCLick(val category: Category, val products: List<ProductDomain>?) : Event()
    data class OnSearch(val query: String, val allProducts: List<ProductDomain>?) : Event()
    data class GetFavorites(val userId: String?, val products: List<ProductDomain>?) : Event()
    data class HandleFavorites(val id: Int, val isFavorite: Boolean) : Event()
    data object ToggleViewMode : Event()

}

sealed class Effect : ViewSideEffect {
    data class ShowMessage(val msg: String) : Effect()
    data class GetFavorites(val userId: String?, val products: List<ProductDomain>?) : Effect()
}

@HiltViewModel
class AllProductsScreenViewModel @Inject constructor(
    private val productsInteractor: ProductsInteractor,
    private val resourcesProvider: ResourceProvider,
    private val sessionManager: SessionManager
) :
    MviViewModel<Event, State, Effect>() {

    // Only the latest search query, coalescing rapid keystrokes so a fast
    // typist doesn't trigger a filter pass per character.
    private val searchEvents = MutableStateFlow<Event.OnSearch?>(null)

    init {
        observeSearch()
    }

    override fun setInitialState(): State = State(
        isLoading = true
    )

    @OptIn(FlowPreview::class)
    private fun observeSearch() {
        viewModelScope.launch {
            searchEvents
                .filterNotNull()
                .debounce(300.milliseconds)
                .collectLatest { event ->
                    setState {
                        copy(
                            isLoading = false,
                            searchQuery = event.query,
                            filteredProducts = event.allProducts?.filter {
                                it.title?.startsWith(event.query, ignoreCase = true) == true
                            }
                        )
                    }
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun handleEvents(event: Event) {
        when (event) {
            is Event.GetAllProducts -> {
                viewModelScope.launch {
                    productsInteractor.getAllProducts().collect {
                        when (it) {
                            is AllProductsPartialState.Failed -> {
                                setState {
                                    copy(isLoading = false)
                                }

                                setEffect {
                                    ShowMessage(it.errorMessage)
                                }
                            }

                            is AllProductsPartialState.NoData -> {
                                setState {
                                    copy(isLoading = false)
                                }

                                setEffect {
                                    ShowMessage(it.errorMessage)
                                }
                            }

                            is AllProductsPartialState.Success -> {
                                val categories = it.products.buildCategoryList(
                                    allCategoryLabel = resourcesProvider.getString(R.string.all_category),
                                    allCategoryId = "all",
                                    categorySelector = { product -> product.category },
                                    categoryMapper = { label, id ->
                                        Category(
                                            label.replaceFirstChar { it.uppercase() },
                                            id
                                        )
                                    }
                                )
                                setState {
                                    copy(
                                        categories = categories,
                                        selectedCategory = categories.firstOrNull()
                                    )
                                }
                                val userId = sessionManager.getCurrentUserId()
                                setEffect { GetFavorites(userId = userId, products = it.products) }
                            }
                        }
                    }
                }
            }

            is Event.OnCategoryCLick -> {
                viewModelScope.launch {
                    setState {
                        copy(
                            isLoading = false,
                            selectedCategory = event.category,
                            filteredProducts = if (event.category.categpryId == "all") viewState.value.originalProducts else viewState.value.originalProducts?.filter { it.category == event.category.categpryId }
                        )
                    }

                }
            }

            is Event.OnSearch -> {
                searchEvents.value = event
            }

            is Event.ToggleViewMode -> {
                setState {
                    copy(
                        viewMode = if (viewMode == ProductsViewMode.Grid) ProductsViewMode.List else ProductsViewMode.Grid
                    )
                }
            }

            is Event.HandleFavorites -> {
                viewModelScope.launch {
                    productsInteractor.handleFavorites(
                        userID = sessionManager.getCurrentUserId(),
                        id = event.id,
                        isFavorite = event.isFavorite
                    ).collect {
                        when (it) {
                            is FavoritesPartialState.Failed -> {
                                setState {
                                    copy(
                                        isLoading = false
                                    )
                                }
                            }

                            is FavoritesPartialState.Success -> {
                                setState {
                                    copy(
                                        isLoading = false,
                                        filteredProducts = viewState.value.filteredProducts?.map { product ->
                                            product.copy(
                                                isFavorite = if (product.id == event.id) !event.isFavorite else product.isFavorite
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            is Event.GetFavorites -> {
                viewModelScope.launch {
                    productsInteractor.getFavorites(event.userId).collect {
                        when (it) {
                            is FavoritesPartialState.Failed -> {
                                setState {
                                    copy(
                                        isLoading = false,
                                        filteredProducts = event.products,
                                        originalProducts = event.products
                                    )
                                }
                            }

                            is FavoritesPartialState.Success -> {
                                setState {
                                    copy(
                                        filteredProducts = event.products?.map { product ->
                                            product.copy(
                                                isFavorite = it.products.contains(
                                                    product.id
                                                )
                                            )
                                        },
                                        originalProducts = event.products,
                                        isLoading = false
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }


}
