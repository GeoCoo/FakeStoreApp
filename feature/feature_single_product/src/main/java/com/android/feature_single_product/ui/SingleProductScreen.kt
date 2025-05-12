package com.android.feature_single_product.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.android.core_ui.component.ExpandableText
import com.android.core_ui.component.LifecycleEffect
import com.android.core_ui.component.LoadingIndicator
import com.android.core_ui.component.NetworkImage
import com.android.fakestore.core.core_resources.R

@Composable
fun SingleProductScreen(
    productId: Int, onBackClick: () -> Unit, onClickEdit: (Int) -> Unit
) {
    val viewModel = hiltViewModel<SingleProductVIewModel>()
    val state = viewModel.viewState
    val lifecycleOwner = LocalLifecycleOwner.current

    LifecycleEffect(
        lifecycleOwner = lifecycleOwner, lifecycleEvent = Lifecycle.Event.ON_CREATE
    ) {
        viewModel.setEvent(Event.GetProduct(productId))
    }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        topBar = {
            Row(modifier = Modifier.fillMaxWidth()) {
                TopBar(
                    onBackClick = onBackClick,
                    onEditClick = { onClickEdit(productId) })
            }
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
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(
                                    RoundedCornerShape(12.dp)
                                ),
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
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = state.value.product?.title ?: "",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.Black
                            )

                            Text(
                                text = state.value.product?.category ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black
                            )

                            Text(
                                text = state.value.product?.price.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black
                            )
                        }
                    }
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.single_products_details_title),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.Black
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

@Composable
fun TopBar(onBackClick: () -> Unit, onEditClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = { onBackClick() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "",
                tint = Color.Black
            )
        }
        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = onEditClick) {
            Icon(
                imageVector = Icons.Default.Edit, contentDescription = "", tint = Color.Black
            )
        }
    }
}

