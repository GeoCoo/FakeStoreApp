package com.android.feature_single_product.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.android.core_ui.component.LifecycleEffect
import com.android.core_ui.component.NetworkImage

@Composable
fun SingleProductScreen(
    productId: Int,
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {}
) {
    val viewModel = hiltViewModel<SingleProductVIewModel>()
    val state = viewModel.viewState
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    LifecycleEffect(
        lifecycleOwner = lifecycleOwner, lifecycleEvent = Lifecycle.Event.ON_CREATE
    ) {
        viewModel.setEvent(Event.GetProduct(productId))
    }
    Scaffold(contentColor = Color.White, topBar = {
        TopBar(onBackClick = onBackClick)
    }) { paddingValues ->
        if (state.value.isLoading)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            } else
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                item {
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        NetworkImage(
                            url = state.value.product?.image ?: "",
                            contentDescription = ""
                        )
                    }
                }
                item {
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

                }
                item {
                    Text(
                        text = state.value.product?.price.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                }
                item {
                    Text(
                        text = "Product Details",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )

                    Text(
                        text = state.value.product?.description ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                }
            }
    }
}

@Composable
fun TopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White),
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

        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "",
                tint = Color.Black
            )
        }
    }
}

