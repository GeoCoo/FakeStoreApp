package com.android.core_tests.interactor

import com.android.api.AllProductsPartialState
import com.android.api.AllProductsResponse
import com.android.api.FavoriteController
import com.android.api.ProductsInteractor
import com.android.api.ProductsRepository
import com.android.api.ResourceProvider
import com.android.api.SingleProductResponse
import com.android.api.SingleProductsPartialState
import com.android.api.UpdateProductResponse
import com.android.api.UpdateProductsPartialState
import com.android.core_model.ProductDto
import com.android.core_model.UpdateProduct
import com.android.core_tests.CoroutineTestRule
import com.android.core_tests.runFlowTest
import com.android.core_tests.runTest
import com.android.core_tests.toFlow
import com.android.impl.ProductsInteractorImpl
import com.android.model.toDomain
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.spyk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ProductsInteractorTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private lateinit var repository: ProductsRepository
    private lateinit var favoriteController: FavoriteController
    private lateinit var resourcesProvider: ResourceProvider

    private lateinit var interactor: ProductsInteractor

    private val sampleDto = ProductDto(
        id = 1,
        title = "Test",
        price = 5.0f,
        description = "Desc",
        category = "Cat",
        image = "img",
        rating = null
    )

    private val updateReq = UpdateProduct(
        id = 1,
        title = "Updated",
        price = 9.99f,
        description = null,
        category = null,
        image = null
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        repository = spyk()
        favoriteController = spyk()
        resourcesProvider = spyk()
        interactor = ProductsInteractorImpl(repository, favoriteController, resourcesProvider)
    }

    @After
    fun tearDown() {
        coroutineRule.cancelScopeAndDispatcher()
        clearAllMocks()
    }

    @Test
    fun `getAllProducts emits Success when repository returns Success`() = coroutineRule.runTest {
        // Given
        val dtoList = listOf(sampleDto)
        getAllProductsInterceptor(AllProductsResponse.Success(dtoList))
        // When / Then
        interactor.getAllProducts().runFlowTest {
            assertEquals(
                AllProductsPartialState.Success(dtoList.map { it.toDomain() }),
                awaitItem()
            )
        }
    }


    @Test
    fun `getAllProducts emits NoData when repository returns NoData`() = coroutineRule.runTest {
        // Given
        getAllProductsInterceptor(AllProductsResponse.NoData("no-data"))
        // When / Then
        interactor.getAllProducts().runFlowTest {
            assertEquals(
                AllProductsPartialState.NoData("no-data"),
                awaitItem()
            )
        }
    }

    @Test
    fun `getAllProducts emits Failed when repository returns Failed`() = coroutineRule.runTest {
        // Given
        getAllProductsInterceptor(AllProductsResponse.Failed("err"))

        // When / Then
        interactor.getAllProducts().runFlowTest {
            assertTrue(awaitItem() is AllProductsPartialState.Failed)
        }
    }


    @Test
    fun `getSingleProduct emits Success when repository returns Success`() =
        coroutineRule.runTest {
            // Given
            getSingleProductInterceptor(SingleProductResponse.Success(sampleDto))
            // When / Then
            interactor.getSingleProduct(1).runFlowTest {
                assertEquals(
                    SingleProductsPartialState.Success(sampleDto.toDomain()),
                    awaitItem()
                )
            }
        }

    @Test
    fun `getSingleProduct emits Failed when repository returns Failed`() =
        coroutineRule.runTest {
            // Given
            getSingleProductInterceptor(SingleProductResponse.Failed("fail"))

            // When / Then
            interactor.getSingleProduct(1).runFlowTest {
                assertEquals(
                    SingleProductsPartialState.Failed("fail"),
                    awaitItem()
                )
            }
        }

    @Test
    fun `updateProduct emits Success when repository returns Success`() =
        coroutineRule.runTest {
            // Given
            updateProducInterceptor(UpdateProductResponse.Success("saved"))
            // When / Then
            interactor.updateProduct(updateReq).runFlowTest {
                assertEquals(
                    UpdateProductsPartialState.Success("saved"),
                    awaitItem()
                )
            }
        }

    @Test
    fun `updateProduct emits Failed when repository returns Failed`() = coroutineRule.runTest {
        // Given
        updateProducInterceptor(UpdateProductResponse.Failed("fail"))
        // When / Then
        interactor.updateProduct(updateReq).runFlowTest {
            assertEquals(
                UpdateProductsPartialState.Failed("fail"),
                awaitItem()
            )
        }
    }

    fun updateProducInterceptor(repositoryResponse: UpdateProductResponse) {
        every { repository.updateSingleProduct(any()) } returns repositoryResponse.toFlow()
    }

    fun getSingleProductInterceptor(repositoryResponse: SingleProductResponse) {
        every { repository.getSingleProduct(any()) } returns repositoryResponse.toFlow()
    }

    private fun getAllProductsInterceptor(repositoryResponse: AllProductsResponse) {
        every { repository.getAllproducts() } returns repositoryResponse.toFlow()
    }

}


