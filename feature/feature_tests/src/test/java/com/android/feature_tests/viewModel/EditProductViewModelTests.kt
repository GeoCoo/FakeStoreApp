package com.android.feature_tests.viewModel

import com.android.api.ProductsInteractor
import com.android.api.UpdateProductsPartialState
import com.android.core_model.UpdateProduct
import com.android.feature_edit.ui.EditProductScreenVIewModel
import com.android.feature_edit.ui.Effect
import com.android.feature_edit.ui.Event
import com.android.feature_tests.CoroutineTestRule
import com.android.feature_tests.RobolectricTest
import com.android.feature_tests.runFlowTest
import com.android.feature_tests.runTest
import com.android.feature_tests.toFlow
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.spyk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.launchIn
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EditProductViewModelTest : RobolectricTest() {

    @get:Rule
    val coroutineRule = CoroutineTestRule()
    private lateinit var productsInteractor: ProductsInteractor
    private lateinit var viewModel: EditProductScreenVIewModel

    private val updateReq = UpdateProduct(
        id = 10, title = "New", price = 5.5f,
        description = null, category = null, image = null
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        productsInteractor = spyk()
        viewModel = EditProductScreenVIewModel(productsInteractor)
    }

    @After
    fun tearDown() {
        coroutineRule.cancelScopeAndDispatcher()
        clearAllMocks()
    }

    fun `Failed update emits ShowToast with error`() = coroutineRule.runTest {
        val err = "update failed"
        getUpdateProducInteractor(UpdateProductsPartialState.Failed(err))
        viewModel.setEvent(Event.UpdateProductEvent(updateReq))

        viewModel.effect.runFlowTest {
            assertEquals(Effect.ShowToast(err), awaitItem())
            coVerify(exactly = 1) { productsInteractor.updateProduct(updateReq) }
        }
    }

    @Test
    fun `Successful update emits ShowToast with savedMsg`() = coroutineRule.runTest {
        val saved = "saved!"
        getUpdateProducInteractor(UpdateProductsPartialState.Success(saved))

        viewModel.setEvent(Event.UpdateProductEvent(updateReq))

        viewModel.effect.runFlowTest {
            assertEquals(Effect.ShowToast(saved), awaitItem())
            coVerify(exactly = 1) { productsInteractor.updateProduct(updateReq) }
        }
    }


    fun getUpdateProducInteractor(partialState: UpdateProductsPartialState) {
        coEvery { productsInteractor.updateProduct(updateReq) } answers  {partialState.toFlow()}
    }
}
