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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.fakestore.core.core_resources.R

val products = listOf(
    Product(
        productId = 0,
        "Women Printed Kurta",
        "Neque porro quisquam est qui dolorem ipsum quia",
        "1500 €",
        R.drawable.ic_logo
    ),
    Product(
        productId = 1,
        "HRX by Hrithik Roshan",
        "Neque porro quisquam est qui dolorem ipsum quia",
        "24.99 €",
        R.drawable.ic_logo
    ),
    Product(productId = 2,"IWC Pilot’s Watch", "SIHH 2019” 44mm", "6.50 €", R.drawable.ic_logo),
    Product(productId = 3,"Labbin White Sneakers", "For Men and Female", "6.50 €", R.drawable.ic_logo),
    Product(
        productId = 4,
        "Women Printed Kurta",
        "Neque porro quisquam est qui dolorem ipsum quia",
        "1500 €",
        R.drawable.ic_logo
    ),
    Product(
        productId = 5,
        "HRX by Hrithik Roshan",
        "Neque porro quisquam est qui dolorem ipsum quia",
        "24.99 €",
        R.drawable.ic_logo
    ),
    Product(productId = 6,"IWC Pilot’s Watch", "SIHH 2019” 44mm", "6.50 €", R.drawable.ic_logo),
    Product(productId = 7,"Labbin White Sneakers", "For Men and Female", "6.50 €", R.drawable.ic_logo)
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun AllProductsScreen(onProductClick: (Product) -> Unit) {
    Scaffold(
        topBar = { TopBar() }
    ) { paddingValues ->
        Column {
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
                    CategoryRow()
                }
                item {
                    FlowRow(
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        products.forEach { product ->
                            ProductItem(product,onProductClick)
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
            .fillMaxWidth()
            .background(color = Color.White),
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
fun CategoryRow() {
    val categories = listOf("Electronics", "Jewelry", "Men's Clothing", "Women's Clothing", "Gifts")
    LazyRow {
        items(categories.size) { index ->
            Text(
                text = categories[index],
                color = Color.Black,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .background(Color.Transparent),
                style = MaterialTheme.typography.bodyMedium
            )

        }
    }
}


data class Product(val productId: Int = 0, val name: String, val desc: String, val price: String, val imageRes: Int)

@Composable
fun ProductItem(product: Product,onProductClick: (Product) -> Unit) {
    Column(
        modifier = Modifier
            .clickable(onClick = {onProductClick(product)})
            .width(LocalConfiguration.current.screenWidthDp.dp / 2 - 16.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = product.imageRes),
            contentDescription = null,
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(product.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Text(product.desc, fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Text(product.price, fontWeight = FontWeight.Bold)
    }
}



