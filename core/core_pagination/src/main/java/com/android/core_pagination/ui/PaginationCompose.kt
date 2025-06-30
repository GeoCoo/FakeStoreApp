package com.android.core_pagination.ui

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.core_pagination.PaginationAction
import com.android.core_pagination.PaginationController
import com.android.core_pagination.PaginationDataSource
import com.android.core_pagination.PaginationState
import kotlinx.coroutines.launch

/**
 * ViewModel extension for pagination support
 */
abstract class PaginationViewModel<T> : ViewModel() {
    protected lateinit var paginationController: PaginationController<T>
    
    val paginationState: State<PaginationState<T>>
        @Composable get() = paginationController.state.collectAsState()
    
    protected fun initializePagination(
        dataSource: PaginationDataSource<T>,
        initialPageSize: Int = 10,
        cacheEnabled: Boolean = true
    ) {
        paginationController = PaginationController(
            dataSource = dataSource,
            initialPageSize = initialPageSize,
            cacheEnabled = cacheEnabled
        )
    }
    
    fun handlePaginationAction(action: PaginationAction) {
        viewModelScope.launch {
            paginationController.handleAction(action)
        }
    }
    
    fun loadInitialPage() {
        handlePaginationAction(PaginationAction.LoadFirstPage)
    }
    
    fun refreshData() {
        handlePaginationAction(PaginationAction.Refresh)
    }
    
    fun updatePageSize(newPageSize: Int) {
        handlePaginationAction(PaginationAction.UpdatePageSize(newPageSize))
    }
}

/**
 * Composable function for easy pagination integration
 */
@Composable
fun <T> rememberPaginationController(
    dataSource: PaginationDataSource<T>,
    initialPageSize: Int = 10,
    cacheEnabled: Boolean = true
): PaginationController<T> {
    return remember(dataSource) {
        PaginationController(
            dataSource = dataSource,
            initialPageSize = initialPageSize,
            cacheEnabled = cacheEnabled
        )
    }
}

/**
 * Effect for loading initial pagination data
 */
@Composable
fun <T> LaunchedPaginationEffect(
    controller: PaginationController<T>,
    key: Any? = Unit,
    loadInitialPage: Boolean = true
) {
    LaunchedEffect(key) {
        if (loadInitialPage) {
            controller.handleAction(PaginationAction.LoadFirstPage)
        }
    }
}

/**
 * Compose wrapper for pagination state handling
 */
@Composable
fun <T> PaginationStateHandler(
    state: PaginationState<T>,
    onRetry: () -> Unit = {},
    loadingContent: @Composable () -> Unit = { PaginationLoadingIndicator(isLoading = true) },
    errorContent: @Composable (String) -> Unit = { message ->
        ErrorMessage(message = message, onRetry = onRetry)
    },
    emptyContent: @Composable () -> Unit = { EmptyMessage() },
    content: @Composable (List<T>) -> Unit
) {
    when {
        state.isLoading && state.items.isEmpty() -> {
            loadingContent()
        }
        state.hasError -> {
            errorContent(state.errorMessage ?: "An error occurred")
        }
        state.items.isEmpty() && !state.isLoading -> {
            emptyContent()
        }
        else -> {
            content(state.items)
        }
    }
}

/**
 * Default error message component
 */
@Composable
private fun ErrorMessage(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        androidx.compose.material3.Text(
            text = message,
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            color = androidx.compose.material3.MaterialTheme.colorScheme.error
        )
        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
        androidx.compose.material3.TextButton(onClick = onRetry) {
            androidx.compose.material3.Text("Retry")
        }
    }
}

/**
 * Default empty message component
 */
@Composable
private fun EmptyMessage() {
    androidx.compose.material3.Text(
        text = "No items found",
        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
    )
}