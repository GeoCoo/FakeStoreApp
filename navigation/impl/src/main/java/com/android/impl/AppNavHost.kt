package com.android.impl

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.android.api.Screen
import com.android.feature_all_products.router.allProductsScreen
import com.android.feature_edit.router.editScreen
import com.android.feature_login.router.loginScreen
import com.android.feature_single_product.router.singleProductScreen
import com.android.feature_splash.router.splashScreen


@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        this.splashScreen(navController)
        this.loginScreen(navController)
        this.allProductsScreen(navController)
        this.singleProductScreen(navController)
        this.editScreen(navController)

    }
}

