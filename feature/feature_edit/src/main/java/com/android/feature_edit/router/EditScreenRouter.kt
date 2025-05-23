package com.android.feature_edit.router

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.android.api.Screen.EditProduct
import com.android.feature_edit.ui.EditProductScreen

fun NavGraphBuilder.editScreen(navController: NavHostController) {
    composable(
        route = EditProduct.route,
        arguments = listOf(
            navArgument("productId") { type = NavType.IntType })
    ) { backStackEntry ->
        val eventArg = backStackEntry.arguments?.getInt("productId") ?: return@composable
        EditProductScreen(
            productId = eventArg,
            onBack = {
                navController.navigateUp()
            }
        )
    }
}