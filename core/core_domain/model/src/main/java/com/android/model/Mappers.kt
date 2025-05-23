package com.android.model

import com.android.core_model.ProductDto
import com.android.core_model.RatingDto


fun RatingDto.toDomain(): RatingDomain {
    return RatingDomain(
        rate = this.rate,
        count = this.count
    )
}

fun ProductDto.toDomain(): ProductDomain {
    return ProductDomain(
        id = this.id ?: 0,
        title = this.title?:"",
        price = this.price?: 0f,
        description = this.description,
        category = this.category,
        image = this.image,
        rating = this.rating?.toDomain()
    )
}
