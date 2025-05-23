package com.android.impl

import com.android.api.AllProductsResponse
import com.android.api.ApiClient
import com.android.api.ProductsRepository
import com.android.api.ResourceProvider
import com.android.api.SingleProductResponse
import com.android.api.UpdateProductResponse
import com.android.core_model.UpdateProduct
import com.android.fakestore.core.core_resources.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


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
    }.catch {
        emit(AllProductsResponse.Failed(errorMsg = it.localizedMessage ?: ""))
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
    }.catch {
        emit(SingleProductResponse.Failed(errorMsg = it.localizedMessage ?: ""))
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
    }.catch {
        emit(UpdateProductResponse.Failed(errorMsg = it.localizedMessage ?: ""))
    }
}