package com.android.feature_all_products.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.android.core.core_domain.model.Category
import com.android.core.core_domain.model.ProductDomain
import com.android.core_ui.component.LifecycleEffect
import com.android.core_ui.component.LoadingIndicator
import com.android.core_ui.component.NetworkImage
import com.android.fakestore.core.core_resources.R


@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun AllProductsScreen(onProductClick: (ProductDomain) -> Unit) {
    val viewModel = hiltViewModel<AllProductsScreenViewModel>()
    val state = viewModel.viewState
    val lifecycleOwner = LocalLifecycleOwner.current

    LifecycleEffect(
        lifecycleOwner = lifecycleOwner, lifecycleEvent = Lifecycle.Event.ON_CREATE
    ) {
        viewModel.setEvent(Event.GetAllProducts)
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
            if (state.value.isLoading)
                LoadingIndicator()
            else
                LazyColumn(
                    modifier = Modifier
                        .padding(16.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer)
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
                        FeaturedTitle()
                    }
                    item {
                        CategoryRow(state.value.categories, onCategoryCLick = {
                            viewModel.setEvent(
                                Event.OnCategoryCLick(
                                    it,
                                    state.value.originalProducts
                                )
                            )
                        })
                    }
                    item {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            state.value.filteredProducts
                                ?.forEach { product ->
                                    ProductItem(
                                        product = product,
                                        onProductClick = onProductClick
                                    )
                                }
                        }
                    }
                }
        }
    }
    BackHandler(enabled = true, onBack = {})
}

@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
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
        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(id = R.drawable.ic_user_avatart),
            contentDescription = "",
            modifier = Modifier.size(36.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit
) {
    val shape = RoundedCornerShape(12.dp)

    OutlinedTextField(
        value = query,
        onValueChange = { onQueryChanged(it) },
        placeholder = {
            Text(
                stringResource(R.string.search_placeholder),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        singleLine = true,
        shape = shape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = 4.dp,
                shape = shape,
                clip = false
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = shape
            )
    )
}

@Composable
fun FeaturedTitle() {
    Text(
        modifier = Modifier.padding(16.dp),
        text = stringResource(id = R.string.featured_section_title),
        style = MaterialTheme.typography.headlineSmall.copy(
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
fun CategoryRow(categories: List<Category>?, onCategoryCLick: (Category) -> Unit) {
    LazyRow {
        categories?.size?.let {
            items(it) { index ->
                categories[index].let { category ->
                    Column(
                        modifier = Modifier
                            .clickable(onClick = { onCategoryCLick(category) })
                            .padding(8.dp)
                            .size(70.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = category.categoryName,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }

                }

            }
        }
    }
}

@Composable
fun ProductItem(
    product: ProductDomain,
    onProductClick: (ProductDomain) -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = { onProductClick(product) })
            .width(LocalConfiguration.current.screenWidthDp.dp / 2-16.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp)),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.Start
    ) {

        product.image?.let {
            NetworkImage(
                url = it,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        product.title?.let {
            Text(
                it,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        product.description?.let {
            Text(
                it,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
        product.price?.let {
            Text(
                text = "$$it",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    }
}


