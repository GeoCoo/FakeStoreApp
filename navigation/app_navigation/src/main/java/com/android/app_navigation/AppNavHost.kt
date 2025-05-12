package com.android.app_navigation

import android.window.SplashScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.android.feature_splash.ui.SplashScreen

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen()
        }

//        composable(
//            route = Screen.Login.route,
//            arguments = listOf(
//                navArgument("singleEvent") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val eventArg = backStackEntry.arguments?.getString("singleEvent") ?: return@composable
//            val event = eventArg.deserialize<SingleEventDomain>(Gson())
//            SingleEventScreen(event = event, navController = navController)
//        }

//        composable(
//            route = Screen.AllProducts.route,
//            arguments = listOf(
//                navArgument("singleEvent") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val eventArg = backStackEntry.arguments?.getString("singleEvent") ?: return@composable
//            val event = eventArg.deserialize<SingleEventDomain>(Gson())
//            SingleEventScreen(event = event, navController = navController)
//        }
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