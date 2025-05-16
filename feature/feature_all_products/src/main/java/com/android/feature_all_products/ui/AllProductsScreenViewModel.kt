package com.android.feature_all_products.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.android.core.core_domain.interactor.AllProductsPartialState
import com.android.core.core_domain.interactor.ProductsInteractor
import com.android.core.core_domain.model.Category
import com.android.core.core_domain.model.ProductDomain
import com.android.core_resources.provider.ResourceProvider
import com.android.core_ui.base.MviViewModel
import com.android.core_ui.base.ViewEvent
import com.android.core_ui.base.ViewSideEffect
import com.android.core_ui.base.ViewState
import com.android.fakestore.core.core_resources.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Locale
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

}

sealed class Effect : ViewSideEffect {
    data class ShowMessage(val msg: String) : Effect()
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
                                    Effect.ShowMessage(it.errorMessage)
                                }
                            }

                            is AllProductsPartialState.NoData -> {
                                setState {
                                    copy(isLoading = false)
                                }

                                setEffect {
                                    Effect.ShowMessage(it.errorMessage)
                                }
                            }

                            is AllProductsPartialState.Success -> {
                                setState {
                                    copy(
                                        originalProducts = it.products,
                                        filteredProducts = it.products,
                                        categories = buildCategoryList(it.products),
                                        isLoading = false,
                                    )
                                }
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
        }
    }


    private fun buildCategoryList(products: List<ProductDomain>?): List<Category> = buildList {
        if (products.isNullOrEmpty()) return@buildList
        val allCategory = resourcesProvider.getString(R.string.all_category)
        add(
            Category(
                allCategory,
                allCategory.toLowerCase()
            )
        )
        products.mapNotNull { it.category }.distinct().forEach { category ->
            add(Category(category.replaceFirstChar { it.uppercase() }, category))
        }
    }
}
