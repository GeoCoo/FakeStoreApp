package com.android.feature_login.router

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.android.api.Screen
import com.android.feature_login.ui.LoginScreen

fun NavGraphBuilder.loginScreen(navController: NavHostController) {
    composable(
        route = Screen.Login.route,
    ) {
        LoginScreen(
            onSuccessLoginNavigate = {
                navController.navigate(Screen.AllProducts.route)
            }
        )
    }
}



