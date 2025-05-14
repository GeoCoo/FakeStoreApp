package com.android.feature_all_products.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.android.core.core_domain.model.ProductDomain
import com.android.core.core_domain.interactor.AllProductsPartialState
import com.android.core.core_domain.interactor.ProductsInteractor
import com.android.core_ui.base.ViewEvent
import com.android.core_ui.base.ViewState
import com.android.core_ui.base.ViewSideEffect
import com.android.core_ui.base.MviViewModel

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


data class State(
    val isLoading: Boolean,
    val originalProducts: List<ProductDomain>? = listOf(),
    val filteredProducts:List<ProductDomain>? = listOf(),
    val categories: List<String>? = listOf()
) : ViewState

sealed class Event : ViewEvent {
    data object GetAllProducts : Event()
    data class OnCategoryCLick(val category: String?,val products: List<ProductDomain>?) : Event()
}

sealed class Effect : ViewSideEffect {}


@HiltViewModel
class AllProductsScreenViewModel @Inject constructor(private val productsInteractor: ProductsInteractor) :
    MviViewModel<Event, State, Effect>() {
    override fun setInitialState(): State = State(
        isLoading = true,
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun handleEvents(event: Event) {
        when (event) {
            is Event.GetAllProducts -> {
                viewModelScope.launch {
                    productsInteractor.getAllProducts().collect {
                        when (it) {
                            is AllProductsPartialState.Failed -> TODO()
                            is AllProductsPartialState.NoData -> TODO()
                            is AllProductsPartialState.Success -> {
                                setState {
                                    copy(
                                        isLoading = false,
                                        originalProducts = it.products,
                                        categories = buildList<String> {
                                            add("All")
                                            it.products
                                                ?.mapNotNull { prod -> prod.category }
                                                ?.distinct()
                                                ?.forEach { add(it) }
                                        }
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
                            isLoading = true,
                            filteredProducts = if (event.category == "All") viewState.value.originalProducts else viewState.value.originalProducts?.filter { it.category == event.category }
                        )
                    }

                }
            }
        }
    }
}
