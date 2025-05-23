package com.android.feature_single_product.ui


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.android.api.FavoritesPartialState
import com.android.api.ProductsInteractor
import com.android.api.SingleProductsPartialState
import com.android.core_ui.base.MviViewModel
import com.android.core_ui.base.ViewEvent
import com.android.core_ui.base.ViewSideEffect
import com.android.core_ui.base.ViewState
import com.android.feature_single_product.ui.Effect.*
import com.android.model.ProductDomain

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class State(
    val isLoading: Boolean,
    val product: ProductDomain?,
) : ViewState

sealed class Event : ViewEvent {
    data class GetProduct(val isFavorite: Boolean, val productId: Int) : Event()
    data class HandleFavorite(val isFavorite: Boolean, val product: ProductDomain) : Event()
}

sealed class Effect : ViewSideEffect {
    data class ShowMessage(val message: String) : Effect()
}


@HiltViewModel
class SingleProductVIewModel @Inject constructor(
    private val poroductsInteractor: ProductsInteractor

) : MviViewModel<Event, State, Effect>() {
    override fun setInitialState(): State = State(
        isLoading = true,
        product=null
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun handleEvents(event: Event) {
        when (event) {
            is Event.GetProduct -> {
                viewModelScope.launch {
                    poroductsInteractor.getSingleProduct(event.productId).collect {
                        when (it) {
                            is SingleProductsPartialState.Failed -> {
                                setState {
                                    copy(
                                        isLoading = false,
                                    )
                                }
                                setEffect {
                                    ShowMessage(it.errorMessage)
                                }
                            }

                            is SingleProductsPartialState.Success -> {
                                setState {
                                    copy(
                                        isLoading = false,
                                        product = it.product?.copy(isFavorite = event.isFavorite)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            is Event.HandleFavorite -> {
                viewModelScope.launch {
                    poroductsInteractor.handleFavorites(
                        userID = 1,
                        id = event.product.id,
                        isFavorite = event.product.isFavorite == true
                    )
                        .collect {
                            when (it) {
                                is FavoritesPartialState.Failed -> {
                                    setEffect { ShowMessage(it.errorMessage) }
                                }

                                is FavoritesPartialState.Success -> {
                                    setState {
                                        copy(
                                            product = event.product.copy(isFavorite = !event.product.isFavorite)
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