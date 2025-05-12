package com.android.feature_tests.viewModel

import com.android.core.core_domain.interactor.ProductsInteractor
import com.android.core.core_domain.interactor.UpdateProductsPartialState
import com.android.core_model.UpdateProduct
import com.android.feature_edit.ui.EditProductScreenVIewModel
import com.android.feature_edit.ui.Effect
import com.android.feature_edit.ui.Event
import com.android.feature_tests.CoroutineTestRule
import com.android.feature_tests.RobolectricTest
import com.android.feature_tests.runFlowTest
import com.android.feature_tests.runTest
import com.android.feature_tests.toFlow
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.Spy
import org.mockito.kotlin.times

class EditProductViewModelTest : RobolectricTest() {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    @Spy
    private lateinit var productsInteractor: ProductsInteractor
    private lateinit var viewModel: EditProductScreenVIewModel

    private val updateReq = UpdateProduct(
        id = 10, title = "New", price = 5.5f,
        description = null, category = null, image = null
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = EditProductScreenVIewModel(productsInteractor)
    }

    @After
    fun tearDown() {
        coroutineRule.cancelScopeAndDispatcher()
    }

    fun `Failed update emits ShowToast with error`() = coroutineRule.runTest {
        val err = "update failed"
        getUpdateProducInteractor(UpdateProductsPartialState.Failed(err))

        viewModel.effect.runFlowTest {
            viewModel.setEvent(Event.UpdateProductEvent(updateReq))
            Mockito.verify(productsInteractor, times(1)).updateProduct(updateReq)
            assertEquals(Effect.ShowToast(err), awaitItem())
        }
    }

    @Test
    fun `Successful update emits ShowToast with savedMsg`() = coroutineRule.runTest {
        val saved = "saved!"
        getUpdateProducInteractor(UpdateProductsPartialState.Success(saved))

        viewModel.effect.runFlowTest {
            viewModel.setEvent(Event.UpdateProductEvent(updateReq))
            Mockito.verify(productsInteractor, times(1)).updateProduct(updateReq)
            assertEquals(Effect.ShowToast(saved), awaitItem())
        }
    }


    suspend fun getUpdateProducInteractor(partialState: UpdateProductsPartialState) {
        Mockito.`when`(productsInteractor.updateProduct(updateReq))
            .thenReturn(partialState.toFlow())
    }
}
