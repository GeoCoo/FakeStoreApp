package com.android.impl

import com.android.api.AllProductsPartialState
import com.android.api.AllProductsResponse
import com.android.api.FavoriteController
import com.android.api.FavoriteControllerPartialState
import com.android.api.FavoritesPartialState
import com.android.api.FavoritesPartialState.*
import com.android.api.ProductsInteractor
import com.android.api.ProductsRepository
import com.android.api.SingleProductResponse
import com.android.api.SingleProductsPartialState
import com.android.api.UpdateProductResponse
import com.android.api.UpdateProductsPartialState
import com.android.core_model.UpdateProduct
import com.android.helpers.safeAsync
import com.android.model.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import com.android.api.ResourceProvider
import com.android.fakestore.core.core_resources.R

class ProductsInteractorImpl @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val favoriteController: FavoriteController,
    private val resourcesProvider: ResourceProvider

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
    }.safeAsync {
        AllProductsPartialState.Failed(
            it.message ?: resourcesProvider.getString(R.string.generic_error_msg)
        )
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
    }.safeAsync {
        SingleProductsPartialState.Failed(it.message.toString())
    }

    override suspend fun updateProduct(updateProduct: UpdateProduct): Flow<UpdateProductsPartialState> =
        flow {
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
        }.safeAsync {
            UpdateProductsPartialState.Failed(
                it.message ?: resourcesProvider.getString(R.string.generic_error_msg)
            )
        }


    override suspend fun handleFavorites(
        userId: String?, id: Int, isFavorite: Boolean
    ): Flow<FavoritesPartialState> = flow {
        favoriteController.handleFavorites(userId = userId, id = id, isFavorite = isFavorite)
            .collect {
                when (it) {
                    is FavoriteControllerPartialState.Success -> {
                        emit(Success(it.products))
                    }

                    is FavoriteControllerPartialState.Failed -> {
                        emit(Failed(it.errorMessage))
                    }
                }
            }
    }.safeAsync {
        Failed(it.message ?: resourcesProvider.getString(R.string.generic_error_msg))
    }

    override suspend fun getFavorites(userId: String?): Flow<FavoritesPartialState> = flow {
        favoriteController.getFavorites(userId).collect {
            when (it) {
                is FavoriteControllerPartialState.Success -> {
                    emit(Success(it.products))
                }

                is FavoriteControllerPartialState.Failed -> {
                    emit(Failed(it.errorMessage))
                }
            }
        }
    }.safeAsync {
        Failed(it.message ?: resourcesProvider.getString(R.string.generic_error_msg))
    }
}
