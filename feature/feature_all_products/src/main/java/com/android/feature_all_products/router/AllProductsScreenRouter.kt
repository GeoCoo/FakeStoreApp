package com.android.feature_all_products.router

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.android.api.Screen
import com.android.api.Screen.SingleProduct
import com.android.feature_all_products.ui.AllProductsScreen


fun NavGraphBuilder.allProductsScreen(navController: NavHostController
) {
    composable(
        route = Screen.AllProducts.route,
    ) {
        AllProductsScreen(onProductClick = { product ->
            navController.navigate(SingleProduct.createRoute(product.id, product.isFavorite))
        })
    }
}