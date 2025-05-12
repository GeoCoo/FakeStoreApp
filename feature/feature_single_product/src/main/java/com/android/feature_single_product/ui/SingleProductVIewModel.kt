package com.android.feature_single_product.ui


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.android.core.core_domain.interactor.ProductsInteractor
import com.android.core.core_domain.interactor.SingleProductsPartialState
import com.android.core.core_domain.model.ProductDomain
import com.android.core_ui.base.MviViewModel
import com.android.core_ui.base.ViewEvent
import com.android.core_ui.base.ViewSideEffect
import com.android.core_ui.base.ViewState

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


data class State(
    val isLoading: Boolean,
    val product: ProductDomain? = null
) : ViewState

sealed class Event : ViewEvent {
    data class GetProduct(val productId: Int) : Event()
}

sealed class Effect : ViewSideEffect {
    data class ShowMessage(val message: String) : Effect()
}


@HiltViewModel
class SingleProductVIewModel @Inject constructor(
    private val poroductsInteractor: ProductsInteractor

) : MviViewModel<Event, State, Effect>() {
    override fun setInitialState(): State = State(
        isLoading = true
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
                                    Effect.ShowMessage(it.errorMessage)
                                }
                            }

                            is SingleProductsPartialState.Success -> {
                                setState {
                                    copy(
                                        isLoading = false,
                                        product = it.product
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