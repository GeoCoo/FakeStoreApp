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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


data class State(
    val isLoading: Boolean,
    val originalProducts: List<ProductDomain>? = listOf(),
    val filteredProducts: List<ProductDomain>? = listOf(),
    val categories: List<Category>? = listOf(),
    val searchQuery: String = ""
) : ViewState

sealed class Event : ViewEvent {
    data object GetAllProducts : Event()
    data class OnCategoryCLick(val category: Category, val products: List<ProductDomain>?) : Event()
    data class OnSearch(val query: String, val allProducts: List<ProductDomain>?) : Event()
    data class GetFavorites(val userId: Int, val products: List<ProductDomain>?) : Event()
    data class HandleFavorites(val userId: Int, val id: Int, val isFavorite: Boolean) : Event()

}

sealed class Effect : ViewSideEffect {
    data class ShowMessage(val msg: String) : Effect()
    data class GetFavorites(val userId: Int, val products: List<ProductDomain>?) : Effect()
}

@HiltViewModel
class AllProductsScreenViewModel @Inject constructor(
    private val productsInteractor: ProductsInteractor,
    private val resourcesProvider: ResourceProvider
) :
    MviViewModel<Event, State, Effect>() {
    override fun setInitialState(): State = State(
        isLoading = true
    )

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
                                setState {
                                    copy(
                                        categories = it.products.buildCategoryList(
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
                                    )
                                }
                                setEffect { GetFavorites(userId = 1, products = it.products) }
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
                            filteredProducts = if (event.category.categpryId == "all") viewState.value.originalProducts else viewState.value.originalProducts?.filter { it.category == event.category.categpryId }
                        )
                    }

                }
            }

            is Event.OnSearch -> {
                viewModelScope.launch {
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

            is Event.HandleFavorites -> {
                viewModelScope.launch {
                    productsInteractor.handleFavorites(
                        userID = event.userId,
                        id = event.id,
                        isFavorite = event.isFavorite
                    ).collect {
                        when (it) {
                            is FavoritesPartialState.Failed -> {

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
