package com.android.api


import com.android.core_model.ProductDto
import com.android.core_model.UpdateProduct
import kotlinx.coroutines.flow.Flow


interface ProductsRepository {
    fun getAllproducts(): Flow<AllProductsResponse>
    fun getSingleProduct(productID: Int): Flow<SingleProductResponse>
    fun updateSingleProduct(updateProduct: UpdateProduct): Flow<UpdateProductResponse>
}


sealed class AllProductsResponse {
    data class Success(val products: List<ProductDto>?) : AllProductsResponse()
    data class Failed(val errorMsg: String) : AllProductsResponse()
    data class NoData(val msg: String) : AllProductsResponse()
}

sealed class SingleProductResponse {
    data class Success(val product: ProductDto?) : SingleProductResponse()
    data class Failed(val errorMsg: String) : SingleProductResponse()
}

sealed class UpdateProductResponse {
    data class Success(val savedMsg: String) : UpdateProductResponse()
    data class Failed(val errorMsg: String) : UpdateProductResponse()
}