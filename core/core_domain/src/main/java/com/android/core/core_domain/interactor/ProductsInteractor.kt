package com.android.core.core_domain.interactor

import com.android.core.core_data.repository.AllProductsResponse
import com.android.core.core_data.repository.ProductsRepository
import com.android.core.core_data.repository.SingleProductResponse
import com.android.core.core_domain.ProductDomain
import com.android.core.core_domain.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface ProductsInteractor {
    suspend fun getAllProducts(): Flow<AllProductsPartialState>
    suspend fun getSingleProduct(productID: Int) : Flow<SingleProductsPartialState>
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
                    emit(AllProductsPartialState.Success(it.sports?.map { it.toDomain() }))
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
                    emit(SingleProductsPartialState.Success(it.sports?.toDomain()))
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

