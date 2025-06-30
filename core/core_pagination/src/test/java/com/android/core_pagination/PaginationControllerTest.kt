package com.android.core_pagination

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for PaginationController
 */
class PaginationControllerTest {

    @Test
    fun `initial state should be correct`() {
        val dataSource = MockDataSource()
        val controller = PaginationController(
            dataSource = dataSource,
            cacheEnabled = false
        )
        
        val state = controller.getCurrentState()
        
        assertEquals(1, state.currentPage)
        assertEquals(10, state.pageSize)
        assertEquals(0, state.totalItems)
        assertEquals(0, state.totalPages)
        assertTrue(state.items.isEmpty())
        assertFalse(state.isLoading)
        assertFalse(state.hasError)
    }

    @Test
    fun `should load first page successfully`() = runTest {
        val dataSource = MockDataSource()
        val controller = PaginationController(
            dataSource = dataSource,
            cacheEnabled = false
        )
        
        controller.handleAction(PaginationAction.LoadFirstPage)
        
        val state = controller.getCurrentState()
        assertEquals(1, state.currentPage)
        assertEquals(10, state.items.size)
        assertEquals(100, state.totalItems)
        assertEquals(10, state.totalPages)
        assertTrue(state.hasNextPage)
        assertFalse(state.hasPreviousPage)
        assertFalse(state.isLoading)
        assertFalse(state.hasError)
    }

    @Test
    fun `should load next page successfully`() = runTest {
        val dataSource = MockDataSource()
        val controller = PaginationController(
            dataSource = dataSource,
            cacheEnabled = false
        )
        
        controller.handleAction(PaginationAction.LoadFirstPage)
        controller.handleAction(PaginationAction.LoadNextPage)
        
        val state = controller.getCurrentState()
        assertEquals(2, state.currentPage)
        assertEquals(10, state.items.size)
        assertTrue(state.hasNextPage)
        assertTrue(state.hasPreviousPage)
    }

    @Test
    fun `should handle errors correctly`() = runTest {
        val dataSource = ErrorDataSource()
        val controller = PaginationController(
            dataSource = dataSource,
            cacheEnabled = false
        )
        
        controller.handleAction(PaginationAction.LoadFirstPage)
        
        val state = controller.getCurrentState()
        assertTrue(state.hasError)
        assertEquals("Test error", state.errorMessage)
        assertTrue(state.items.isEmpty())
    }

    @Test
    fun `should calculate pagination correctly`() {
        val state = PaginationState<TestItem>(
            currentPage = 3,
            pageSize = 10,
            totalItems = 95
        )
        
        val calculatedState = state.calculateTotalPages()
        
        assertEquals(10, calculatedState.totalPages)
        assertTrue(calculatedState.hasNextPage)
        assertTrue(calculatedState.hasPreviousPage)
        
        val (startItem, endItem) = calculatedState.getCurrentItemRange()
        assertEquals(21, startItem)
        assertEquals(30, endItem)
    }

    @Test
    fun `cache should work correctly`() = runTest {
        val dataSource = MockDataSource()
        val cache = InMemoryPaginationCache<TestItem>()
        val controller = PaginationController(
            dataSource = dataSource,
            cache = cache,
            cacheEnabled = true
        )
        
        // Load first page
        controller.handleAction(PaginationAction.LoadFirstPage)
        assertTrue(controller.isPageCached(1))
        
        // Load second page
        controller.handleAction(PaginationAction.LoadNextPage)
        assertTrue(controller.isPageCached(2))
        
        // Go back to first page (should use cache)
        controller.handleAction(PaginationAction.LoadPreviousPage)
        val state = controller.getCurrentState()
        assertEquals(1, state.currentPage)
    }

    @Test
    fun `LRU cache should evict old pages`() {
        val cache = LruPaginationCache<TestItem>(maxSize = 2)
        val items1 = listOf(TestItem(1), TestItem(2))
        val items2 = listOf(TestItem(3), TestItem(4))
        val items3 = listOf(TestItem(5), TestItem(6))
        
        cache.put(1, items1)
        cache.put(2, items2)
        cache.put(3, items3) // Should evict page 1
        
        assertFalse(cache.containsPage(1))
        assertTrue(cache.containsPage(2))
        assertTrue(cache.containsPage(3))
    }
}

/**
 * Mock data source for testing
 */
class MockDataSource : PaginationDataSource<TestItem> {
    override suspend fun loadPage(page: Int, pageSize: Int): PaginationResult<TestItem> {
        val startId = (page - 1) * pageSize + 1
        val items = (startId until startId + pageSize).map { TestItem(it) }
        
        return PaginationResult.Success(
            items = items,
            currentPage = page,
            totalItems = 100
        )
    }
    
    override suspend fun getTotalItemCount(): Int = 100
}

/**
 * Error data source for testing error scenarios
 */
class ErrorDataSource : PaginationDataSource<TestItem> {
    override suspend fun loadPage(page: Int, pageSize: Int): PaginationResult<TestItem> {
        return PaginationResult.Error("Test error")
    }
    
    override suspend fun getTotalItemCount(): Int = 0
}

/**
 * Test data item
 */
data class TestItem(val id: Int)