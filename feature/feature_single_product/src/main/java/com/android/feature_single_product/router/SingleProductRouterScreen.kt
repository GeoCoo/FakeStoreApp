package com.android.feature_single_product.router

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.android.api.Screen.EditProduct
import com.android.api.Screen.SingleProduct
import com.android.feature_single_product.ui.SingleProductScreen

fun NavGraphBuilder.singleProductScreen(
    navController: androidx.navigation.NavHostController,
) {
    composable(
        route = SingleProduct.route,
        arguments = listOf(
            navArgument("productId") { type = NavType.IntType },
            navArgument("isFavorite") { type = NavType.BoolType })
    ) { backStackEntry ->
        val productId = backStackEntry.arguments?.getInt("productId") ?: return@composable
        val isFavorite = backStackEntry.arguments?.getBoolean("isFavorite") ?: return@composable
        SingleProductScreen(
            productId = productId,
            isFavorite = isFavorite,
            onBackClick = { navController.navigateUp() },
            onClickEdit = { navController.navigate(EditProduct.createRoute(productId)) }
        )
    }
}