package com.android.feature_single_product.ui


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
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
}


@HiltViewModel
class SingleProductVIewModel @Inject constructor(

) : MviViewModel<Event, State, Effect>() {
    override fun setInitialState(): State = State(
        isLoading = true
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun handleEvents(event: Event) {
        when (event) {
            is Event.GetProduct -> {
                viewModelScope.launch {
                    setState {
                        copy(
                            isLoading = false,
                            product = ProductDomain(
                                id = 0,
                                title = "Title",
                                price = 0.0,
                                description = "Description",
                                category = "Category",
                                image = "Image",
                            )
                        )
                    }

                }
            }
        }
    }
}