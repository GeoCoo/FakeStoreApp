package com.android.impl

import com.android.api.AllProductsPartialState
import com.android.api.AllProductsResponse
import com.android.api.ProductsInteractor
import com.android.api.ProductsRepository
import com.android.api.SingleProductResponse
import com.android.api.SingleProductsPartialState
import com.android.api.UpdateProductResponse
import com.android.api.UpdateProductsPartialState
import com.android.core_model.UpdateProduct
import com.android.model.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProductsInteractorImpl @Inject constructor(
    private val productsRepository: ProductsRepository

) : ProductsInteractor {
    override suspend fun getAllProducts(): Flow<AllProductsPartialState> = flow {
        productsRepository.getAllproducts().collect {
            when (it) {
                is AllProductsResponse.Failed -> {
                    emit(AllProductsPartialState.Failed(it.errorMsg))
                }

                is AllProductsResponse.NoData -> {
                    emit(AllProductsPartialState.NoData(it.msg))
                }

                is AllProductsResponse.Success -> {
                    emit(AllProductsPartialState.Success(it.products?.map { it.toDomain() }))
                }
            }
        }
    }

    override suspend fun getSingleProduct(productID: Int): Flow<SingleProductsPartialState> = flow {
        productsRepository.getSingleProduct(productID).collect {
            when (it) {
                is SingleProductResponse.Failed -> {
                    emit(SingleProductsPartialState.Failed(it.errorMsg))
                }

                is SingleProductResponse.Success -> {
                    emit(SingleProductsPartialState.Success(it.product?.toDomain()))
                }
            }
        }
    }

    override suspend fun updateProduct(updateProduct: UpdateProduct): Flow<UpdateProductsPartialState> = flow {
        productsRepository.updateSingleProduct(updateProduct).collect {
            when (it) {
                is UpdateProductResponse.Failed -> {
                    emit(UpdateProductsPartialState.Failed(it.errorMsg))
                }

                is UpdateProductResponse.Success -> {
                    emit(UpdateProductsPartialState.Success(it.savedMsg))
                }
            }
        }
    }
}
