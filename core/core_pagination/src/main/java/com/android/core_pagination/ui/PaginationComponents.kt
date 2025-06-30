package com.android.core_pagination.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.core_pagination.PaginationAction
import com.android.core_pagination.PaginationState

/**
 * Pagination controls component with navigation buttons
 */
@Composable
fun <T> PaginationControls(
    state: PaginationState<T>,
    onAction: (PaginationAction) -> Unit,
    modifier: Modifier = Modifier,
    showPageInfo: Boolean = true,
    showItemInfo: Boolean = true
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // First page button
            IconButton(
                onClick = { onAction(PaginationAction.LoadFirstPage) },
                enabled = state.hasPreviousPage && !state.isLoading
            ) {
                Icon(
                    imageVector = Icons.Default.FirstPage,
                    contentDescription = "First page"
                )
            }
            
            // Previous page button
            IconButton(
                onClick = { onAction(PaginationAction.LoadPreviousPage) },
                enabled = state.hasPreviousPage && !state.isLoading
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Previous page"
                )
            }
            
            // Page info
            if (showPageInfo) {
                Card(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = "${state.currentPage} / ${state.totalPages}",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            // Next page button
            IconButton(
                onClick = { onAction(PaginationAction.LoadNextPage) },
                enabled = state.hasNextPage && !state.isLoading
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Next page"
                )
            }
            
            // Last page button
            IconButton(
                onClick = { onAction(PaginationAction.LoadLastPage) },
                enabled = state.hasNextPage && !state.isLoading
            ) {
                Icon(
                    imageVector = Icons.Default.LastPage,
                    contentDescription = "Last page"
                )
            }
        }
        
        // Item info and refresh
        if (showItemInfo) {
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Item range info
                val (startItem, endItem) = state.getCurrentItemRange()
                Text(
                    text = "Items $startItem-$endItem of ${state.totalItems}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Refresh button
                IconButton(
                    onClick = { onAction(PaginationAction.Refresh) },
                    enabled = !state.isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh"
                    )
                }
            }
        }
    }
}

/**
 * Compact pagination controls for mobile
 */
@Composable
fun <T> CompactPaginationControls(
    state: PaginationState<T>,
    onAction: (PaginationAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Previous button
        OutlinedButton(
            onClick = { onAction(PaginationAction.LoadPreviousPage) },
            enabled = state.hasPreviousPage && !state.isLoading,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Previous")
        }
        
        // Page info
        Text(
            text = "${state.currentPage} / ${state.totalPages}",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        
        // Next button
        OutlinedButton(
            onClick = { onAction(PaginationAction.LoadNextPage) },
            enabled = state.hasNextPage && !state.isLoading,
            modifier = Modifier.weight(1f)
        ) {
            Text("Next")
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/**
 * Page size selector component
 */
@Composable
fun PageSizeSelector(
    currentPageSize: Int,
    availablePageSizes: List<Int> = listOf(5, 10, 20, 50),
    onPageSizeChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Items per page:",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(end = 8.dp)
        )
        
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = currentPageSize.toString(),
                onValueChange = { },
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .width(80.dp),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )
            
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                availablePageSizes.forEach { pageSize ->
                    DropdownMenuItem(
                        text = { Text(pageSize.toString()) },
                        onClick = {
                            onPageSizeChanged(pageSize)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

/**
 * Loading indicator for pagination
 */
@Composable
fun PaginationLoadingIndicator(
    isLoading: Boolean,
    isLoadingMore: Boolean = false,
    modifier: Modifier = Modifier
) {
    if (isLoading || isLoadingMore) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = if (isLoadingMore) "Loading more..." else "Loading...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}