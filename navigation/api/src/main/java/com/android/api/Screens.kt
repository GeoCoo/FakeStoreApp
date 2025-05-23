package com.android.api

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object AllProducts : Screen("allProducts")
    object SingleProduct : Screen("singleProduct/{productId}/isFavorite/{isFavorite}") {
        fun createRoute(productId: Int?, isFavorite: Boolean) =
            "singleProduct/$productId/isFavorite/$isFavorite"
    }

    object EditProduct : Screen("editProduct/{productId}") {
        fun createRoute(productId: Int) = "editProduct/$productId"
    }
}