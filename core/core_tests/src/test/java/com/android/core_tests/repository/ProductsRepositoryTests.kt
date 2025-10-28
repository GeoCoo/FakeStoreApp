package com.android.core_tests.repository

import com.android.api.AllProductsResponse
import com.android.api.ApiClient
import com.android.api.ProductsRepository
import com.android.api.ResourceProvider
import com.android.api.SingleProductResponse
import com.android.api.UpdateProductResponse
import com.android.core_model.ProductDto
import com.android.core_model.UpdateProduct
import com.android.core_tests.CoroutineTestRule
import com.android.core_tests.runFlowTest
import com.android.core_tests.runTest
import com.android.fakestore.core.core_resources.R
import com.android.impl.ProductsRepositoryImpl
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.spyk
import junit.framework.TestCase.assertEquals
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

class TestProductsRepositoryImpl {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private lateinit var apiClient: ApiClient
    private lateinit var resourceProvider: ResourceProvider

    private lateinit var repository: ProductsRepository

    private val genericError = "Generic Error"
    private val noDataMsg = "No Data"
    private val savedMsg = "Saved Successfully"

    private val sampleDto = ProductDto(
        id = 1,
        title = "Test",
        price = 5f,
        description = "Desc",
        category = "Cat",
        image = "img",
        rating = null
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        apiClient = spyk()
        resourceProvider = spyk()
        repository = ProductsRepositoryImpl(apiClient, resourceProvider)
        coEvery { resourceProvider.getString(R.string.generic_error_msg) } returns genericError
        coEvery { resourceProvider.getString(R.string.no_data_msg) } returns noDataMsg
        coEvery { resourceProvider.getString(R.string.save_suceess) } returns savedMsg
    }

    @After
    fun tearDown() {
        coroutineRule.cancelScopeAndDispatcher()
    }

    @Test
    fun `getAllproducts emits Success when api returns non-empty list`() =
        coroutineRule.runTest {
            // Given
            val list = listOf(sampleDto)
            coEvery { apiClient.retrieveProducts() } returns Response.success(list)

            // When / Then
            repository.getAllproducts().runFlowTest {
                assertEquals(
                    AllProductsResponse.Success(list),
                    awaitItem()
                )
            }
        }

    @Test
    fun `getAllproducts emits NoData when api returns empty list`() =
        coroutineRule.runTest {
            // Given
            coEvery { apiClient.retrieveProducts() } returns Response.success(emptyList())

            // When / Then
            repository.getAllproducts().runFlowTest {
                assertEquals(
                    AllProductsResponse.NoData(noDataMsg),
                    awaitItem()
                )
            }
        }

    @Test
    fun `getAllproducts emits Failed when api returns error`() =
        coroutineRule.runTest {
            // Given
            val errorBody = "error".toResponseBody("text/plain".toMediaType())
            coEvery { apiClient.retrieveProducts() } returns Response.error(500, errorBody)

            // When / Then
            repository.getAllproducts().runFlowTest {
                assertEquals(
                    AllProductsResponse.Failed(genericError),
                    awaitItem()
                )
            }
        }

    @Test
    fun `getSingleProduct emits Success when api returns product`() =
        coroutineRule.runTest {
            // Given
            coEvery { apiClient.retrieveSingleProduct(1) } returns Response.success(sampleDto)

            // When / Then
            repository.getSingleProduct(1).runFlowTest {
                assertEquals(
                    SingleProductResponse.Success(sampleDto),
                    awaitItem()
                )
            }
        }

    @Test
    fun `getSingleProduct emits Failed when api returns error`() =
        coroutineRule.runTest {
            // Given
            val errorBody = "err".toResponseBody("text/plain".toMediaType())
            coEvery { apiClient.retrieveSingleProduct(1) } returns Response.error(404, errorBody)

            // When / Then
            repository.getSingleProduct(1).runFlowTest {
                assertEquals(
                    SingleProductResponse.Failed(genericError),
                    awaitItem()
                )
            }
        }

    @Test
    fun `updateSingleProduct emits Success when api returns product`() =
        coroutineRule.runTest {
            // Given
            val update = UpdateProduct(1, "U", 1.23f, null, null, null)
            coEvery { apiClient.updateProduct(update.id, update) } returns Response.success(
                sampleDto
            )

            // When / Then
            repository.updateSingleProduct(update).runFlowTest {
                assertEquals(
                    UpdateProductResponse.Success(savedMsg),
                    awaitItem()
                )
            }
        }

    @Test
    fun `updateSingleProduct emits Failed when api returns error`() =
        coroutineRule.runTest {
            // Given
            val update = UpdateProduct(1, "U", 1.23f, null, null, null)
            val errorBody = "err".toResponseBody("text/plain".toMediaType())
            coEvery { apiClient.updateProduct(update.id, update) } returns Response.error(
                400,
                errorBody
            )

            // When / Then
            repository.updateSingleProduct(update).runFlowTest {
                assertEquals(
                    UpdateProductResponse.Failed(genericError),
                    awaitItem()
                )
            }
        }
}
