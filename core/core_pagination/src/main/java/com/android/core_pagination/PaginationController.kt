package com.android.core_pagination

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Core pagination controller that manages pagination state and operations
 */
class PaginationController<T>(
    private val dataSource: PaginationDataSource<T>,
    private val cache: PaginationCache<T> = InMemoryPaginationCache(),
    initialPageSize: Int = 10,
    private val cacheEnabled: Boolean = true
) {
    
    private val _state = MutableStateFlow(
        PaginationState<T>(pageSize = initialPageSize)
    )
    val state: StateFlow<PaginationState<T>> = _state.asStateFlow()
    
    /**
     * Handle pagination actions
     */
    suspend fun handleAction(action: PaginationAction) {
        when (action) {
            is PaginationAction.LoadFirstPage -> loadPage(1)
            is PaginationAction.LoadNextPage -> loadNextPage()
            is PaginationAction.LoadPreviousPage -> loadPreviousPage()
            is PaginationAction.LoadLastPage -> loadLastPage()
            is PaginationAction.LoadPage -> loadPage(action.page)
            is PaginationAction.Refresh -> refresh()
            is PaginationAction.UpdatePageSize -> updatePageSize(action.newPageSize)
        }
    }
    
    /**
     * Load a specific page
     */
    private suspend fun loadPage(page: Int, isAppend: Boolean = false) {
        val currentState = _state.value
        
        // Validate page number
        if (currentState.totalPages > 0 && !currentState.isValidPage(page)) {
            return
        }
        
        // Check cache first if enabled
        if (cacheEnabled && cache.containsPage(page)) {
            val cachedItems = cache.get(page) ?: emptyList()
            updateStateWithCachedData(page, cachedItems, isAppend)
            return
        }
        
        // Set loading state
        _state.value = currentState.copy(
            isLoading = !isAppend,
            isLoadingMore = isAppend,
            hasError = false,
            errorMessage = null
        )
        
        try {
            val result = dataSource.loadPage(page, currentState.pageSize)
            
            when (result) {
                is PaginationResult.Success -> {
                    // Cache the data if caching is enabled
                    if (cacheEnabled) {
                        cache.put(page, result.items)
                    }
                    
                    updateStateWithNewData(result, page, isAppend)
                }
                
                is PaginationResult.Error -> {
                    _state.value = currentState.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        hasError = true,
                        errorMessage = result.message
                    )
                }
                
                is PaginationResult.Loading -> {
                    // Loading state already set above
                }
            }
        } catch (e: Exception) {
            _state.value = currentState.copy(
                isLoading = false,
                isLoadingMore = false,
                hasError = true,
                errorMessage = e.message ?: "Unknown error occurred"
            )
        }
    }
    
    private suspend fun updateStateWithNewData(
        result: PaginationResult.Success<T>,
        page: Int,
        isAppend: Boolean
    ) {
        val currentState = _state.value
        
        val newItems = if (isAppend) {
            currentState.items + result.items
        } else {
            result.items
        }
        
        _state.value = currentState.copy(
            items = newItems,
            currentPage = page,
            totalItems = result.totalItems,
            isLoading = false,
            isLoadingMore = false,
            hasError = false,
            errorMessage = null
        ).calculateTotalPages()
    }
    
    private fun updateStateWithCachedData(
        page: Int,
        cachedItems: List<T>,
        isAppend: Boolean
    ) {
        val currentState = _state.value
        
        val newItems = if (isAppend) {
            currentState.items + cachedItems
        } else {
            cachedItems
        }
        
        _state.value = currentState.copy(
            items = newItems,
            currentPage = page,
            isLoading = false,
            isLoadingMore = false,
            hasError = false,
            errorMessage = null
        ).calculateTotalPages()
    }
    
    /**
     * Load next page
     */
    private suspend fun loadNextPage() {
        val currentState = _state.value
        if (currentState.hasNextPage) {
            loadPage(currentState.currentPage + 1)
        }
    }
    
    /**
     * Load previous page
     */
    private suspend fun loadPreviousPage() {
        val currentState = _state.value
        if (currentState.hasPreviousPage) {
            loadPage(currentState.currentPage - 1)
        }
    }
    
    /**
     * Load last page
     */
    private suspend fun loadLastPage() {
        val currentState = _state.value
        if (currentState.totalPages > 0) {
            loadPage(currentState.totalPages)
        }
    }
    
    /**
     * Refresh current page
     */
    private suspend fun refresh() {
        val currentState = _state.value
        if (cacheEnabled) {
            cache.invalidate()
        }
        loadPage(currentState.currentPage)
    }
    
    /**
     * Update page size and reload first page
     */
    private suspend fun updatePageSize(newPageSize: Int) {
        if (newPageSize > 0) {
            _state.value = _state.value.copy(pageSize = newPageSize)
            if (cacheEnabled) {
                cache.invalidate()
            }
            loadPage(1)
        }
    }
    
    /**
     * Get current state
     */
    fun getCurrentState(): PaginationState<T> = _state.value
    
    /**
     * Clear cache
     */
    fun clearCache() {
        if (cacheEnabled) {
            cache.clear()
        }
    }
    
    /**
     * Check if page is cached
     */
    fun isPageCached(page: Int): Boolean {
        return cacheEnabled && cache.containsPage(page)
    }
}