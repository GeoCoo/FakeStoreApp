package com.android.model


data class ProductDomain(
    var id: Int,
    var title: String?="",
    var price: Float,
    var description: String? = "",
    var category: String? = "",
    var image: String? = "",
    var isFavorite: Boolean = false,
    var rating: RatingDomain? = RatingDomain()
)

data class RatingDomain(
    var rate: Double? = null,
    var count: Int? = null
)

data class Category(val categoryName: String, val categpryId: String)
