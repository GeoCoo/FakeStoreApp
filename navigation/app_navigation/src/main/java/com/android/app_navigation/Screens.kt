package com.android.app_navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object AllProducts : Screen("allProducts")
    object SingleProduct : Screen("singleProduct/{productId}") {
        fun createRoute(productId: Int?) = "singleProduct/$productId"
    }

    object EditProduct : Screen("editProduct/{productId}") {
        fun createRoute(productId: Int) = "editProduct/$productId"
    }
}