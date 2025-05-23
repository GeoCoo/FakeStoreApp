package com.android.feature_splash.router

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.android.api.Screen
import com.android.feature_splash.ui.SplashScreen

fun NavGraphBuilder.splashScreen(navController: NavHostController) {
    composable(route = Screen.Splash.route) {
        SplashScreen(onNavigate = { isLoggedIn ->
            if (isLoggedIn)
                navController.navigate(Screen.AllProducts.route)
            else
                navController.navigate(Screen.Login.route)
        })
    }
}