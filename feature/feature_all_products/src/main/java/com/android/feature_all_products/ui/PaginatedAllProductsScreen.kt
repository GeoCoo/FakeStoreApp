package com.android.feature_all_products.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsState
import com.android.core_pagination.PaginationAction
import com.android.core_pagination.ui.CompactPaginationControls
import com.android.core_pagination.ui.PageSizeSelector
import com.android.core_pagination.ui.PaginationLoadingIndicator
import com.android.core_pagination.ui.PaginationStateHandler
import com.android.core_ui.component.LoadingIndicator
import com.android.core_ui.component.NetworkImage
import com.android.fakestore.core.core_resources.R
import com.android.model.Category
import com.android.model.ProductDomain

@Composable
fun PaginatedAllProductsScreen(
    onProductClick: (ProductDomain) -> Unit,
    viewModel: PaginatedAllProductsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val paginationState by viewModel.paginationState
    val effect by viewModel.effect.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    // Handle lifecycle events
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                viewModel.handleEvent(PaginatedEvent.InitializePagination)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Handle effects
    LaunchedEffect(effect) {
        effect?.let {
            when (it) {
                is PaginatedEffect.ShowMessage -> {
                    // Show snackbar or toast
                }
            }
            viewModel.clearEffect()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        topBar = {
            Row(modifier = Modifier.fillMaxWidth()) {
                TopBar()
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                // Search bar
                item {
                    SearchBar(
                        query = state.searchQuery,
                        onQueryChanged = { searchQuery ->
                            viewModel.handleEvent(PaginatedEvent.OnSearch(searchQuery))
                        }
                    )
                }

                // Categories
                if (state.categories?.isNotEmpty() == true) {
                    item {
                        CategoryRow(
                            categories = state.categories!!,
                            onCategoryCLick = { category ->
                                viewModel.handleEvent(PaginatedEvent.OnCategoryClick(category))
                            }
                        )
                    }
                }

                // Page size selector (only show when pagination is enabled)
                if (state.showPagination) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PageSizeSelector(
                                currentPageSize = paginationState.pageSize,
                                availablePageSizes = listOf(5, 10, 15, 20),
                                onPageSizeChanged = { newSize ->
                                    viewModel.handleEvent(
                                        PaginatedEvent.OnPaginationAction(
                                            PaginationAction.UpdatePageSize(newSize)
                                        )
                                    )
                                }
                            )
                        }
                    }
                }

                // Products with pagination state handling
                item {
                    PaginationStateHandler(
                        state = paginationState,
                        onRetry = {
                            viewModel.handleEvent(
                                PaginatedEvent.OnPaginationAction(PaginationAction.Refresh)
                            )
                        },
                        loadingContent = {
                            if (paginationState.items.isEmpty()) {
                                LoadingIndicator()
                            }
                        },
                        errorContent = { message ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = message,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    TextButton(
                                        onClick = {
                                            viewModel.handleEvent(
                                                PaginatedEvent.OnPaginationAction(PaginationAction.Refresh)
                                            )
                                        }
                                    ) {
                                        Text("Retry")
                                    }
                                }
                            }
                        },
                        emptyContent = {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "No products found",
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    ) { products ->
                        // Products grid
                        Column {
                            val chunkedProducts = products.chunked(2)
                            chunkedProducts.forEach { rowProducts ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    rowProducts.forEach { product ->
                                        ProductItem(
                                            product = product,
                                            onProductClick = onProductClick,
                                            onFavoriteClick = {
                                                viewModel.handleEvent(
                                                    PaginatedEvent.HandleFavorites(
                                                        id = product.id,
                                                        isFavorite = product.isFavorite
                                                    )
                                                )
                                            }
                                        )
                                    }
                                    // Add spacer if odd number of products
                                    if (rowProducts.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }

                // Loading more indicator
                if (paginationState.isLoadingMore) {
                    item {
                        PaginationLoadingIndicator(
                            isLoading = false,
                            isLoadingMore = true,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            // Pagination controls (only show when pagination is enabled)
            if (state.showPagination && paginationState.totalPages > 1) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp
                ) {
                    CompactPaginationControls(
                        state = paginationState,
                        onAction = { action ->
                            viewModel.handleEvent(PaginatedEvent.OnPaginationAction(action))
                        },
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TopBar() {
    Text(
        modifier = Modifier.padding(16.dp),
        text = stringResource(id = R.string.app_name),
        style = MaterialTheme.typography.headlineLarge.copy(
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
fun SearchBar(query: String, onQueryChanged: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text(stringResource(R.string.search_hint)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        singleLine = true
    )
}

@Composable
fun CategoryRow(categories: List<Category>, onCategoryCLick: (Category) -> Unit) {
    LazyRow(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            Card(
                modifier = Modifier
                    .clickable { onCategoryCLick(category) }
                    .padding(4.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = category.categoryName,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun ProductItem(
    product: ProductDomain,
    onProductClick: (ProductDomain) -> Unit,
    onFavoriteClick: (ProductDomain) -> Unit
) {
    Card(
        modifier = Modifier
            .clickable { onProductClick(product) }
            .width(170.dp)
            .padding(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Favorite button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = { onFavoriteClick(product) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (product.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (product.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Product image
            product.image?.let { imageUrl ->
                NetworkImage(
                    url = imageUrl,
                    contentDescription = product.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            // Product title
            product.title?.let { title ->
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Product price
            product.price?.let { price ->
                Text(
                    text = "$$price",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}