package com.android.core.core_domain.interactor

import com.android.core.core_data.repository.AllProductsResponse
import com.android.core.core_data.repository.ProductsRepository
import com.android.core.core_data.repository.SingleProductResponse
import com.android.core.core_data.repository.UpdateProductResponse
import com.android.core.core_domain.model.ProductDomain
import com.android.core.core_domain.model.toDomain
import com.android.core_model.UpdateProduct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface ProductsInteractor {
    suspend fun getAllProducts(): Flow<AllProductsPartialState>
    suspend fun getSingleProduct(productID: Int): Flow<SingleProductsPartialState>
    suspend fun updateProduct(
        updateProduct: UpdateProduct
    ): Flow<UpdateProductsPartialState>
}

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

    override suspend fun updateProduct(
        updateProduct: UpdateProduct
    ): Flow<UpdateProductsPartialState> = flow {
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

