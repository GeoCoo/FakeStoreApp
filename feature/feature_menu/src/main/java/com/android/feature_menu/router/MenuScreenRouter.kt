package com.android.feature_menu.router

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.android.api.Screen
import com.android.feature_menu.ui.MenuScreen

fun NavGraphBuilder.menuScreen(navController: NavHostController) {
    composable(
        route = Screen.Menu.route,
    ) {
        MenuScreen(
            onBack = { navController.popBackStack() },
            onLoggedOut = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        )
    }
}
