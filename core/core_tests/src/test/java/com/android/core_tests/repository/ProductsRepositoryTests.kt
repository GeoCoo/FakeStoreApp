package com.android.core_tests.repository

import com.android.core.core_api.api.ApiClient
import com.android.core.core_data.repository.ProductsRepository
import com.android.core.core_data.repository.ProductsRepositoryImpl
import com.android.core.core_data.repository.AllProductsResponse
import com.android.core.core_data.repository.SingleProductResponse
import com.android.core.core_data.repository.UpdateProductResponse
import com.android.core_model.ProductDto
import com.android.core_model.UpdateProduct
import com.android.core_resources.provider.ResourceProvider
import com.android.fakestore.core.core_resources.R
import com.android.core_tests.CoroutineTestRule
import com.android.core_tests.runFlowTest
import com.android.core_tests.runTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.Spy
import retrofit2.Response
import junit.framework.TestCase.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class TestProductsRepositoryImpl {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    @Spy
    private lateinit var apiClient: ApiClient

    @Spy
    private lateinit var resourceProvider: ResourceProvider

    private lateinit var repository: ProductsRepository

    private val genericError = "Generic Error"
    private val noDataMsg    = "No Data"
    private val savedMsg     = "Saved Successfully"

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
        MockitoAnnotations.openMocks(this)
        repository = ProductsRepositoryImpl(apiClient, resourceProvider)
        Mockito.`when`(resourceProvider.getString(R.string.generic_error_msg))
            .thenReturn(genericError)
        Mockito.`when`(resourceProvider.getString(R.string.no_data_msg))
            .thenReturn(noDataMsg)
        Mockito.`when`(resourceProvider.getString(R.string.save_suceess))
            .thenReturn(savedMsg)
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
            Mockito.`when`(apiClient.retrieveProducts())
                .thenReturn(Response.success(list))

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
            Mockito.`when`(apiClient.retrieveProducts())
                .thenReturn(Response.success(emptyList()))

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
            Mockito.`when`(apiClient.retrieveProducts())
                .thenReturn(Response.error(500, errorBody))

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
            Mockito.`when`(apiClient.retrieveSingleProduct(1))
                .thenReturn(Response.success(sampleDto))

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
            Mockito.`when`(apiClient.retrieveSingleProduct(1))
                .thenReturn(Response.error(404, errorBody))

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
            Mockito.`when`(apiClient.updateProduct(update.id, update))
                .thenReturn(Response.success(sampleDto))

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
            Mockito.`when`(apiClient.updateProduct(update.id, update))
                .thenReturn(Response.error(400, errorBody))

            // When / Then
            repository.updateSingleProduct(update).runFlowTest {
                assertEquals(
                    UpdateProductResponse.Failed(genericError),
                    awaitItem()
                )
            }
        }
}
