package com.android.core_model

data class UpdateProduct(
    val id: Int,
    val title: String? = null,
    val price: Float? = null,
    val description: String? = null,
    val category: String? = null,
    val image: String? = null
)