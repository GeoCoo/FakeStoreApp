package com.android.core.core_data.repository


import com.android.core.core_api.api.ApiClient
import com.android.core_model.ProductDto
import com.android.core_model.UpdateProduct
import com.android.core_resources.provider.ResourceProvider
import com.android.fakestore.core.core_resources.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


interface ProductsRepository {
    fun getAllproducts(): Flow<AllProductsResponse>
    fun getSingleProduct(productID: Int): Flow<SingleProductResponse>
    fun updateSingleProduct(updateProduct: UpdateProduct): Flow<UpdateProductResponse>
}

class ProductsRepositoryImpl @Inject constructor(
    private val apiClient: ApiClient,
    private val resourceProvider: ResourceProvider,
) : ProductsRepository {

    override fun getAllproducts(): Flow<AllProductsResponse> = flow {
        val response = apiClient.retrieveProducts()

        when {
            response.isSuccessful && !response.body().isNullOrEmpty() -> {
                emit(AllProductsResponse.Success(response.body()))
            }

            response.isSuccessful && response.body().isNullOrEmpty() -> {
                emit(AllProductsResponse.NoData(resourceProvider.getString(R.string.no_data_msg)))
            }

            else -> {
                emit(AllProductsResponse.Failed(resourceProvider.getString(R.string.generic_error_msg)))
            }
        }
    }

    override fun getSingleProduct(productID: Int): Flow<SingleProductResponse> = flow {
        val response = apiClient.retrieveSingleProduct(productID)

        when {
            response.isSuccessful && response.body() != null -> {
                emit(SingleProductResponse.Success(response.body()))
            }

            else -> {
                emit(SingleProductResponse.Failed(resourceProvider.getString(R.string.generic_error_msg)))
            }
        }
    }

    override fun updateSingleProduct(
        updateProduct: UpdateProduct
    ): Flow<UpdateProductResponse> = flow {
        val response = apiClient.updateProduct(updateProduct.id, updateProduct)
        when {
            response.isSuccessful && response.body() != null -> {
                emit(UpdateProductResponse.Success(resourceProvider.getString(R.string.save_suceess)))
            }

            else -> {
                emit(UpdateProductResponse.Failed(resourceProvider.getString(R.string.generic_error_msg)))

            }
        }
    }
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