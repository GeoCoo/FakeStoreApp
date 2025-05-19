package com.android.api

import com.android.core_model.UpdateProduct
import com.android.model.ProductDomain
import kotlinx.coroutines.flow.Flow

interface ProductsInteractor {
    suspend fun getAllProducts(): Flow<AllProductsPartialState>
    suspend fun getSingleProduct(productID: Int): Flow<SingleProductsPartialState>
    suspend fun updateProduct(
        updateProduct: UpdateProduct
    ): Flow<UpdateProductsPartialState>
}


sealed class AllProductsPartialState {
    data class Success(val products: List<ProductDomain>?) : AllProductsPartialState()
    data class Failed(val errorMessage: String) : AllProductsPartialState()
    data class NoData(val errorMessage: String) : AllProductsPartialState()
}

sealed class SingleProductsPartialState {
    data class Success(val product: ProductDomain?) : SingleProductsPartialState()
    data class Failed(val errorMessage: String) : SingleProductsPartialState()
}

sealed class UpdateProductsPartialState {
    data class Success(val savedMesg: String) : UpdateProductsPartialState()
    data class Failed(val errorMessage: String) : UpdateProductsPartialState()
}

