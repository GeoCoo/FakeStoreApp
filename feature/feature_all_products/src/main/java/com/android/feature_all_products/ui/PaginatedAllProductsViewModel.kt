package com.android.feature_all_products.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewModelScope
import com.android.api.ProductsRepository
import com.android.api.ResourceProvider
import com.android.core_pagination.PaginationAction
import com.android.core_pagination.PaginationController
import com.android.core_pagination.PaginationState
import com.android.core_pagination.datasource.ProductsPaginationDataSource
import com.android.core_pagination.ui.PaginationViewModel
import com.android.core_ui.base.ViewEvent
import com.android.core_ui.base.ViewSideEffect
import com.android.core_ui.base.ViewState
import com.android.fakestore.core.core_resources.R
import com.android.helpers.buildCategoryList
import com.android.model.Category
import com.android.model.ProductDomain
import com.android.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PaginatedState(
    val categories: List<Category>? = listOf(),
    val searchQuery: String = "",
    val selectedCategory: Category? = null,
    val showPagination: Boolean = true
) : ViewState

sealed class PaginatedEvent : ViewEvent {
    data object InitializePagination : PaginatedEvent()
    data class OnCategoryClick(val category: Category) : PaginatedEvent()
    data class OnSearch(val query: String) : PaginatedEvent()
    data class HandleFavorites(val id: Int, val isFavorite: Boolean) : PaginatedEvent()
    data class OnPaginationAction(val action: PaginationAction) : PaginatedEvent()
}

sealed class PaginatedEffect : ViewSideEffect {
    data class ShowMessage(val msg: String) : PaginatedEffect()
}

@HiltViewModel
class PaginatedAllProductsViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val resourcesProvider: ResourceProvider,
    private val sessionManager: SessionManager
) : PaginationViewModel<ProductDomain>() {

    private val _state = MutableStateFlow(PaginatedState())
    val state: StateFlow<PaginatedState> = _state.asStateFlow()

    private val _effect = MutableStateFlow<PaginatedEffect?>(null)
    val effect: StateFlow<PaginatedEffect?> = _effect.asStateFlow()

    init {
        initializePaginationController()
    }

    private fun initializePaginationController() {
        val dataSource = ProductsPaginationDataSource(
            productsRepository = productsRepository,
            productMapper = { dto ->
                // Simple mapping - in real app, this would be more sophisticated
                ProductDomain(
                    id = dto.id ?: 0,
                    title = dto.title,
                    price = dto.price,
                    description = dto.description,
                    category = dto.category,
                    image = dto.image,
                    rating = dto.rating,
                    isFavorite = false // Default, would be set later
                )
            }
        )
        
        initializePagination(
            dataSource = dataSource,
            initialPageSize = 10,
            cacheEnabled = true
        )
    }

    fun handleEvent(event: PaginatedEvent) {
        when (event) {
            is PaginatedEvent.InitializePagination -> {
                viewModelScope.launch {
                    loadInitialPage()
                    // Load categories from first page
                    val currentState = paginationController.getCurrentState()
                    if (currentState.items.isNotEmpty()) {
                        updateCategoriesFromProducts(currentState.items)
                    }
                }
            }

            is PaginatedEvent.OnCategoryClick -> {
                _state.value = _state.value.copy(
                    selectedCategory = event.category,
                    showPagination = event.category.categpryId == "all"
                )
                
                if (event.category.categpryId == "all") {
                    // Show all products with pagination
                    refreshData()
                } else {
                    // Filter products by category (disable pagination for filtered view)
                    // In a real implementation, you might want to create a separate
                    // paginated endpoint for category filtering
                }
            }

            is PaginatedEvent.OnSearch -> {
                _state.value = _state.value.copy(
                    searchQuery = event.query,
                    showPagination = event.query.isEmpty()
                )
                
                if (event.query.isEmpty()) {
                    // Show all products with pagination
                    refreshData()
                } else {
                    // Filter products by search query (disable pagination for search)
                    // In a real implementation, you might want to create a separate
                    // paginated endpoint for search
                }
            }

            is PaginatedEvent.HandleFavorites -> {
                viewModelScope.launch {
                    // Handle favorites logic here
                    // This would typically update the backend and refresh the UI
                }
            }

            is PaginatedEvent.OnPaginationAction -> {
                handlePaginationAction(event.action)
            }
        }
    }

    private fun updateCategoriesFromProducts(products: List<ProductDomain>) {
        val categories = products.buildCategoryList(
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
        
        _state.value = _state.value.copy(categories = categories)
    }

    private fun setEffect(effect: PaginatedEffect) {
        _effect.value = effect
    }

    fun clearEffect() {
        _effect.value = null
    }
}