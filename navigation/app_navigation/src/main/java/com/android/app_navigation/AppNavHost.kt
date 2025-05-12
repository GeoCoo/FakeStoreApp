package com.android.app_navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.feature_all_products.ui.AllProductsScreen
import com.android.feature_login.ui.LoginScreen
import com.android.feature_splash.ui.SplashScreen

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen{
                navController.navigate(Screen.Login.route)
            }
        }

        composable(
            route = Screen.Login.route,
        ) {
            LoginScreen{
                navController.navigate(Screen.AllProducts.route)
            }
        }

        composable(
            route = Screen.AllProducts.route,
        ) {
            AllProductsScreen()
        }
//
//        composable(
//            route = Screen.SingleProduct.route,
//            arguments = listOf(
//                navArgument("singleEvent") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val eventArg = backStackEntry.arguments?.getString("singleEvent") ?: return@composable
//            val event = eventArg.deserialize<SingleEventDomain>(Gson())
//            SingleEventScreen(event = event, navController = navController)
        }

//        composable(
//            route = Screen.EditProduct.route,
//            arguments = listOf(
//                navArgument("singleEvent") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val eventArg = backStackEntry.arguments?.getString("singleEvent") ?: return@composable
//            val event = eventArg.deserialize<SingleEventDomain>(Gson())
//            SingleEventScreen(event = event, navController = navController)
//        }
//    }
}