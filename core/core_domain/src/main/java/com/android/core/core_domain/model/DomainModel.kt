package com.android.core.core_domain.model

import com.android.core_model.ProductDto
import com.android.core_model.RatingDto

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

fun RatingDto.toDomain(): RatingDomain {
    return RatingDomain(
        rate = this.rate,
        count = this.count
    )
}

fun ProductDto.toDomain(): ProductDomain {
    return ProductDomain(
        id = this.id,
        title = this.title,
        price = this.price,
        description = this.description,
        category = this.category,
        image = this.image,
        rating = this.rating?.toDomain()
    )
}
