package com.android.core_pagination.datasource

import com.android.api.PaginatedProductsResponse
import com.android.api.ProductsRepository
import com.android.core_pagination.PaginationDataSource
import com.android.core_pagination.PaginationResult
import com.android.model.ProductDomain
import javax.inject.Inject

/**
 * Products data source implementation for pagination
 */
class ProductsPaginationDataSource @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val productMapper: (com.android.core_model.ProductDto) -> ProductDomain
) : PaginationDataSource<ProductDomain> {
    
    override suspend fun loadPage(page: Int, pageSize: Int): PaginationResult<ProductDomain> {
        return try {
            var result: PaginationResult<ProductDomain>? = null
            
            productsRepository.getProductsPaginated(page, pageSize).collect { response ->
                result = when (response) {
                    is PaginatedProductsResponse.Success -> {
                        val domainProducts = response.products.map { productMapper(it) }
                        PaginationResult.Success(
                            items = domainProducts,
                            currentPage = response.currentPage,
                            totalItems = response.totalItems
                        )
                    }
                    
                    is PaginatedProductsResponse.Failed -> {
                        PaginationResult.Error(response.errorMsg)
                    }
                    
                    is PaginatedProductsResponse.NoData -> {
                        PaginationResult.Success(
                            items = emptyList(),
                            currentPage = page,
                            totalItems = 0
                        )
                    }
                }
            }
            
            result ?: PaginationResult.Error("Unknown error occurred")
        } catch (e: Exception) {
            PaginationResult.Error(e.message ?: "Unknown error occurred")
        }
    }
    
    override suspend fun getTotalItemCount(): Int {
        // Since we don't have a separate API for total count,
        // we'll return a default value. In a real implementation,
        // this would make a separate API call to get the total count.
        return 20 // Fake Store API has 20 products
    }
}