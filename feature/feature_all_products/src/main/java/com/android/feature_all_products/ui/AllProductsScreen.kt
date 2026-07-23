package com.android.feature_all_products.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.android.core.core_design_system.FakeStoreTheme
import com.android.core_ui.component.organisms.AppTopBar
import com.android.core_ui.component.atoms.LoadingIndicator
import com.android.core_ui.component.molecules.SearchBar
import com.android.core_ui.component.util.LifecycleEffect
import com.android.fakestore.core.core_resources.R
import com.android.feature_all_products.ui.atoms.FeaturedTitle
import com.android.feature_all_products.ui.atoms.ViewModeToggle
import com.android.feature_all_products.ui.molecules.CategoryRow
import com.android.feature_all_products.ui.organisms.ProductItem
import com.android.model.ProductDomain
import com.google.android.material.loadingindicator.LoadingIndicator


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AllProductsScreen(onProductClick: (ProductDomain) -> Unit, onMenuClick: () -> Unit) {
    val viewModel = hiltViewModel<AllProductsScreenViewModel>()
    val state = viewModel.viewState
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    LifecycleEffect(
        lifecycleOwner = lifecycleOwner, lifecycleEvent = Lifecycle.Event.ON_CREATE
    ) {
        viewModel.setEvent(Event.GetAllProducts)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            AppTopBar(
                titleContent = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(FakeStoreTheme.spacing.xs)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_logo),
                            contentDescription = "",
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.app_name),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onMenuClick) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_user_avatar),
                            contentDescription = "",
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (state.value.isLoading)
                LoadingIndicator()
            else
                LazyColumn(
                    modifier = Modifier
                        .padding(horizontal = FakeStoreTheme.spacing.md)
                ) {
                    stickyHeader {
                        SearchBar(query = state.value.searchQuery, onQueryChanged = { searchQuery ->
                            viewModel.setEvent(
                                Event.OnSearch(
                                    searchQuery,
                                    state.value.originalProducts
                                )
                            )
                        })
                    }
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FeaturedTitle()
                            ViewModeToggle(
                                viewMode = state.value.viewMode,
                                onToggle = { viewModel.setEvent(Event.ToggleViewMode) }
                            )
                        }
                    }
                    item {
                        CategoryRow(
                            categories = state.value.categories,
                            selectedCategory = state.value.selectedCategory,
                            onCategoryCLick = {
                                viewModel.setEvent(
                                    Event.OnCategoryCLick(
                                        it,
                                        state.value.originalProducts
                                    )
                                )
                            }
                        )
                    }
                    when (state.value.viewMode) {
                        ProductsViewMode.Grid -> {
                            val productRows = state.value.filteredProducts.orEmpty().chunked(2)
                            items(productRows.size) { rowIndex ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = FakeStoreTheme.spacing.sm),
                                    horizontalArrangement = Arrangement.spacedBy(FakeStoreTheme.spacing.xs)
                                ) {
                                    productRows[rowIndex].forEach { product ->
                                        ProductItem(
                                            modifier = Modifier.weight(1f),
                                            product = product,
                                            onProductClick = onProductClick,
                                            onFavoriteClick = {
                                                viewModel.setEvent(
                                                    Event.HandleFavorites(
                                                        id = product.id,
                                                        isFavorite = product.isFavorite
                                                    )
                                                )
                                            }
                                        )
                                    }
                                    if (productRows[rowIndex].size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }

                        ProductsViewMode.List -> {
                            val products = state.value.filteredProducts.orEmpty()
                            items(products.size) { index ->
                                val product = products[index]
                                ProductItem(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = FakeStoreTheme.spacing.sm),
                                    product = product,
                                    onProductClick = onProductClick,
                                    onFavoriteClick = {
                                        viewModel.setEvent(
                                            Event.HandleFavorites(
                                                id = product.id,
                                                isFavorite = product.isFavorite
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
        }
    }
    BackHandler(enabled = true, onBack = {})


    LaunchedEffect(Unit) {
        viewModel.effect.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .collect { effect ->
                when (effect) {
                    is Effect.ShowMessage -> {
                        Toast.makeText(context, effect.msg, Toast.LENGTH_SHORT).show()
                    }

                    is Effect.GetFavorites ->{
                        viewModel.setEvent(Event.GetFavorites(effect.userId,effect.products))
                    }
                }
            }
    }
}
