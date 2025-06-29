package com.android.core_pagination

/**
 * Represents pagination configuration and state
 */
data class PaginationState<T>(
    val items: List<T> = emptyList(),
    val currentPage: Int = 1,
    val pageSize: Int = 10,
    val totalItems: Int = 0,
    val totalPages: Int = 0,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null,
    val hasNextPage: Boolean = false,
    val hasPreviousPage: Boolean = false
) {
    /**
     * Calculate total pages based on total items and page size
     */
    fun calculateTotalPages(): PaginationState<T> {
        val calculatedTotalPages = if (totalItems > 0) {
            (totalItems + pageSize - 1) / pageSize
        } else {
            0
        }
        return copy(
            totalPages = calculatedTotalPages,
            hasNextPage = currentPage < calculatedTotalPages,
            hasPreviousPage = currentPage > 1
        )
    }

    /**
     * Get the range of items currently displayed
     */
    fun getCurrentItemRange(): Pair<Int, Int> {
        val startItem = ((currentPage - 1) * pageSize) + 1
        val endItem = minOf(currentPage * pageSize, totalItems)
        return Pair(startItem, endItem)
    }

    /**
     * Check if current page is valid
     */
    fun isValidPage(page: Int): Boolean {
        return page > 0 && page <= totalPages
    }
}

/**
 * Represents pagination events/actions
 */
sealed class PaginationAction {
    object LoadFirstPage : PaginationAction()
    object LoadNextPage : PaginationAction()
    object LoadPreviousPage : PaginationAction()
    object LoadLastPage : PaginationAction()
    data class LoadPage(val page: Int) : PaginationAction()
    object Refresh : PaginationAction()
    data class UpdatePageSize(val newPageSize: Int) : PaginationAction()
}

/**
 * Represents the result of a pagination request
 */
sealed class PaginationResult<T> {
    data class Success<T>(
        val items: List<T>,
        val currentPage: Int,
        val totalItems: Int,
        val isAppend: Boolean = false
    ) : PaginationResult<T>()
    
    data class Error<T>(
        val message: String,
        val throwable: Throwable? = null
    ) : PaginationResult<T>()
    
    class Loading<T> : PaginationResult<T>()
}