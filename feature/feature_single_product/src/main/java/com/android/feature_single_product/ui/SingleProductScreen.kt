package com.android.feature_single_product.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.android.core.core_design_system.FakeStoreTheme
import com.android.core_ui.component.organisms.AppTopBar
import com.android.core_ui.component.atoms.ExpandableText
import com.android.core_ui.component.util.LifecycleEffect
import com.android.core_ui.component.atoms.LoadingIndicator
import com.android.core_ui.component.atoms.NetworkImage
import com.android.core_ui.component.organisms.ProductActionsRow
import com.android.fakestore.core.core_resources.R

@Composable
fun SingleProductScreen(
    productId: Int,isFavorite: Boolean, onBackClick: () -> Unit, onClickEdit: (Int) -> Unit
) {
    val viewModel = hiltViewModel<SingleProductVIewModel>()
    val state = viewModel.viewState
    val lifecycleOwner = LocalLifecycleOwner.current

    LifecycleEffect(
        lifecycleOwner = lifecycleOwner, lifecycleEvent = Lifecycle.Event.ON_CREATE
    ) {
        viewModel.setEvent(Event.GetProduct(isFavorite,productId))
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            AppTopBar(
                onBackClick = onBackClick,
                actions = {
                    IconButton(onClick = { onClickEdit(productId) }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()) {
            if (state.value.isLoading)
                LoadingIndicator()
            else
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(FakeStoreTheme.spacing.md),
                    verticalArrangement = Arrangement.spacedBy(FakeStoreTheme.spacing.md)
                ) {
                    item{
                        ProductActionsRow(state.value.product, onClick = {
                            state.value.product?.let { product -> viewModel.setEvent(Event.HandleFavorite(isFavorite = isFavorite, product = product)) }
                        })
                    }
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(FakeStoreTheme.corners.medium),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            NetworkImage(
                                url = state.value.product?.image ?: "", contentDescription = ""
                            )
                        }
                    }
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(FakeStoreTheme.spacing.sm)
                        ) {
                            Text(
                                text = state.value.product?.title ?: "",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Text(
                                text = state.value.product?.category ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "$${state.value.product?.price ?: ""}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(FakeStoreTheme.spacing.sm)
                        ) {
                            Text(
                                text = stringResource(id = R.string.single_products_details_title),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            ExpandableText(
                                text = state.value.product?.description ?: "",
                                minimizedMaxLines = 2
                            )
                        }
                    }
                }
        }
    }
}

