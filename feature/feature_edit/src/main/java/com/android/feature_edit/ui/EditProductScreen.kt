package com.android.feature_edit.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.android.core.core_design_system.FakeStoreTheme
import com.android.core_ui.component.organisms.AppTopBar
import com.android.core_model.UpdateProduct
import com.android.core_ui.component.atoms.ActionButton
import com.android.core_ui.component.atoms.AppTextField
import com.android.fakestore.core.core_resources.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    productId: Int,
    onBack: () -> Unit
) {
    val viewModel = hiltViewModel<EditProductScreenVIewModel>()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var title by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            AppTopBar(onBackClick = onBack)
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(FakeStoreTheme.spacing.md),
                verticalArrangement = Arrangement.spacedBy(FakeStoreTheme.spacing.md)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                                FakeStoreTheme.corners.medium
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Image Placeholder",
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
                item {
                    AppTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = stringResource(R.string.edit_title),
                    )
                }

                item {
                    AppTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = stringResource(R.string.edit_price),
                        keyboardType = KeyboardType.Number,
                    )
                }

                item {
                    AppTextField(
                        value = category,
                        onValueChange = {
                            category = it
                        },
                        label = stringResource(R.string.edit_category),
                    )
                }

                item {
                    AppTextField(
                        value = description,
                        onValueChange = {
                            description = it
                        },
                        label = stringResource(R.string.edit_description),
                        singleLine = false,
                        modifier = Modifier.height(100.dp),
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(FakeStoreTheme.spacing.xl))
                    ActionButton(stringResource(R.string.save_btn_txt), onClick = {
                        viewModel.setEvent(
                            Event.UpdateProductEvent(
                                UpdateProduct(
                                    id = productId,
                                    title = title,
                                    price = if (price.isNotEmpty()) price.toFloat() else 0f,
                                    category = category,
                                )
                            )
                        )
                    })
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .collect { effect ->
                when (effect) {
                    is Effect.ShowToast -> {
                        Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}
