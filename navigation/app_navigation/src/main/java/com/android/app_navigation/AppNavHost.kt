package com.android.app_navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.android.app_navigation.Screen.EditProduct
import com.android.app_navigation.Screen.SingleProduct
import com.android.feature_all_products.ui.AllProductsScreen
import com.android.feature_edit.ui.EditProductScreen
import com.android.feature_login.ui.LoginScreen
import com.android.feature_single_product.ui.SingleProductScreen
import com.android.feature_splash.ui.SplashScreen

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen(onCounterFinish = {
                navController.navigate(Screen.Login.route)
            })
        }

        composable(
            route = Screen.Login.route,
        ) {
            LoginScreen(
                onSuccessLoginNavigate = {
                    navController.navigate(Screen.AllProducts.route)
                }
            )
        }

        composable(
            route = Screen.AllProducts.route,
        ) {
            AllProductsScreen(onProductClick = { product ->
                navController.navigate(SingleProduct.createRoute(product.id))
            })
        }

        composable(
            route = SingleProduct.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            val eventArg = backStackEntry.arguments?.getInt("productId") ?: return@composable
            SingleProductScreen(
                productId = eventArg,
                onBackClick = { navController.navigateUp() },
                onEditClick = { navController.navigate(EditProduct.createRoute(eventArg)) }
            )
        }

        composable(
            route = EditProduct.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventArg = backStackEntry.arguments?.getInt("productId") ?: return@composable
            EditProductScreen(productId = eventArg, onBackClick = {
                navController.navigateUp()
            })
        }
    }
}