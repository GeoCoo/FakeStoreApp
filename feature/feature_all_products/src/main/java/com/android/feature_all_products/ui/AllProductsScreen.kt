package com.android.feature_all_products.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.android.core.core_domain.ProductDomain
import com.android.core_ui.component.LifecycleEffect
import com.android.core_ui.component.NetworkImage
import com.android.fakestore.core.core_resources.R


@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun AllProductsScreen(onProductClick: (ProductDomain) -> Unit) {
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
        contentColor = Color.White,
        topBar = {
            Row(modifier = Modifier.fillMaxWidth()) {
                TopBar()
            }
        }
    ) { paddingValues ->
        if (state.value.isLoading) Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                stickyHeader {
                    SearchBar()
                }
                item {
                    FeaturedTitle()
                }
                item {
                    CategoryRow(state.value.categories)
                }
                item {
                    FlowRow(
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        state.value.prodcts?.forEach { product ->
                            ProductItem(product, onProductClick)
                        }
                    }
                }

            }
        }
    }
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
                contentDescription = "Logo",
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "Stylish",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color(0xFFE91E63),
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(id = R.drawable.ic_user_avatart),
            contentDescription = "Profile",
            modifier = Modifier.size(36.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        placeholder = { Text("Search any Product...") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = Color(0xFFF9F9F9),
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent
        )
    )
}

@Composable
fun FeaturedTitle() {
    Text(
        text = "All Featured",
        color = Color.Black,
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
    )
}

@Composable
fun CategoryRow(categories:List<String>?) {
    LazyRow {
        categories?.size?.let {
            items(it) { index ->
                categories[index].let {
                    Column(modifier = Modifier.padding(8.dp).size(100.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center) {
                        Text(
                            text = it,
                            color = Color.Black,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .background(Color.Transparent),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                }

            }
        }
    }
}


@Composable
fun ProductItem(product: ProductDomain, onProductClick: (ProductDomain) -> Unit) {
    Column(
        modifier = Modifier
            .clickable(onClick = { onProductClick(product) })
            .width(LocalConfiguration.current.screenWidthDp.dp / 2 - 16.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp)),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start
    ) {

        product.image?.let {
            NetworkImage(
                url = it,
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        product.title?.let { Text(it, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,color = Color.Black, maxLines = 1, overflow = TextOverflow.Ellipsis) }
        product.category?.let { Text(it, fontSize = 12.sp, color = Color.Black,overflow = TextOverflow.Ellipsis) }
        product.price?.let { Text(it.toString(), fontWeight = FontWeight.Bold,color = Color.Black,overflow = TextOverflow.Ellipsis) }
    }
}



