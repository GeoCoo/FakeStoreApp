package com.android.core_model

import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("price")
    var price: Float? = null,
    @SerializedName("description")
    var description: String? = null,
    @SerializedName("category")
    var category: String? = null,
    @SerializedName("image")
    var image: String? = null,
    @SerializedName("rating")
    var rating: RatingDto? = RatingDto()
)

data class RatingDto(
    @SerializedName("rate") var rate: Double? = null,
    @SerializedName("count") var count: Int? = null
)
