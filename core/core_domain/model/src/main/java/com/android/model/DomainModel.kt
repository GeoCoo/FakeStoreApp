package com.android.model


data class ProductDomain(
    var id: Int? = null,
    var title: String? = null,
    var price: Float? = null,
    var description: String? = null,
    var category: String? = null,
    var image: String? = null,
    var rating: RatingDomain? = RatingDomain()
)

data class RatingDomain(
    var rate: Double? = null,
    var count: Int? = null
)

data class Category(val categoryName: String, val categpryId: String)
