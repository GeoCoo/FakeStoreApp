package com.android.feature_edit.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.android.core.core_domain.interactor.ProductsInteractor
import com.android.core.core_domain.interactor.UpdateProductsPartialState
import com.android.core_model.UpdateProduct
import com.android.core_ui.base.MviViewModel
import com.android.core_ui.base.ViewEvent
import com.android.core_ui.base.ViewSideEffect
import com.android.core_ui.base.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


data object State : ViewState

sealed class Event : ViewEvent {
    data class UpdateProductEvent(val updateProduct: UpdateProduct) : Event()
}

sealed class Effect : ViewSideEffect {
    data class ShowToast(val message: String) : Effect()
}


@HiltViewModel
class EditProductScreenVIewModel @Inject constructor(private val productsInteractor: ProductsInteractor) :
    MviViewModel<Event, State, Effect>() {
    override fun setInitialState(): State = State

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun handleEvents(event: Event) {
        when (event) {
            is Event.UpdateProductEvent -> {
                viewModelScope.launch {
                    productsInteractor.updateProduct(event.updateProduct).collect {
                        when (it) {
                            is UpdateProductsPartialState.Failed -> {
                                setEffect {
                                    Effect.ShowToast(it.errorMessage)
                                }
                            }

                            is UpdateProductsPartialState.Success -> {
                                setEffect {
                                    Effect.ShowToast(it.savedMesg)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}