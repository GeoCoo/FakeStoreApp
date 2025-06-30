# Pagination Library

A standalone pagination library for Android applications with caching, customizable controls, and seamless integration with Jetpack Compose.

## Features

- ✅ **Pagination Functionality**: Navigate through pages with customizable page sizes
- ✅ **Caching Mechanism**: Efficient in-memory and LRU cache implementations
- ✅ **Navigation Controls**: Next, previous, first, last page navigation
- ✅ **Compose Integration**: Ready-to-use Compose components
- ✅ **Standalone Design**: Modular and easily integrable into any project
- ✅ **MVI Compatible**: Works seamlessly with MVI architecture

## Quick Start

### 1. Basic Usage

```kotlin
// Create a data source
class ProductDataSource : PaginationDataSource<Product> {
    override suspend fun loadPage(page: Int, pageSize: Int): PaginationResult<Product> {
        // Your API call logic here
        return try {
            val response = api.getProducts(page, pageSize)
            PaginationResult.Success(
                items = response.products,
                currentPage = page,
                totalItems = response.totalCount
            )
        } catch (e: Exception) {
            PaginationResult.Error(e.message ?: "Unknown error")
        }
    }
    
    override suspend fun getTotalItemCount(): Int {
        return api.getTotalProductCount()
    }
}

// In your Composable
@Composable
fun ProductScreen() {
    val dataSource = remember { ProductDataSource() }
    val controller = rememberPaginationController(
        dataSource = dataSource,
        initialPageSize = 10,
        cacheEnabled = true
    )
    val state by controller.state.collectAsState()
    
    LaunchedPaginationEffect(controller)
    
    Column {
        PaginationStateHandler(
            state = state,
            onRetry = { controller.handleAction(PaginationAction.Refresh) }
        ) { products ->
            LazyColumn {
                items(products) { product ->
                    ProductItem(product = product)
                }
            }
        }
        
        PaginationControls(
            state = state,
            onAction = { controller.handleAction(it) }
        )
    }
}
```

### 2. ViewModel Integration

```kotlin
class ProductsViewModel @Inject constructor(
    private val dataSource: ProductDataSource
) : PaginationViewModel<Product>() {
    
    init {
        initializePagination(
            dataSource = dataSource,
            initialPageSize = 20,
            cacheEnabled = true
        )
        loadInitialPage()
    }
    
    fun onSearch(query: String) {
        // Update data source with search query
        refreshData()
    }
}

// In your Composable
@Composable
fun ProductScreen(viewModel: ProductsViewModel = hiltViewModel()) {
    val state by viewModel.paginationState
    
    Column {
        PaginationStateHandler(
            state = state,
            onRetry = { viewModel.refreshData() }
        ) { products ->
            ProductList(products = products)
        }
        
        PaginationControls(
            state = state,
            onAction = viewModel::handlePaginationAction
        )
    }
}
```

### 3. Custom Cache Implementation

```kotlin
// LRU Cache with custom size
val lruCache = LruPaginationCache<Product>(maxSize = 20)

// Custom cache implementation
class DatabasePaginationCache<T> : PaginationCache<T> {
    // Your database cache logic here
}

val controller = PaginationController(
    dataSource = dataSource,
    cache = lruCache, // or your custom cache
    initialPageSize = 15,
    cacheEnabled = true
)
```

### 4. UI Components

#### Full Pagination Controls

```kotlin
PaginationControls(
    state = paginationState,
    onAction = { action -> 
        coroutineScope.launch {
            controller.handleAction(action)
        }
    },
    showPageInfo = true,
    showItemInfo = true
)
```

#### Compact Controls (Mobile)

```kotlin
CompactPaginationControls(
    state = paginationState,
    onAction = { action -> 
        controller.handleAction(action)
    }
)
```

#### Page Size Selector

```kotlin
PageSizeSelector(
    currentPageSize = state.pageSize,
    availablePageSizes = listOf(10, 20, 50, 100),
    onPageSizeChanged = { newSize ->
        controller.handleAction(PaginationAction.UpdatePageSize(newSize))
    }
)
```

## Architecture

### Core Components

1. **PaginationState**: Immutable state holding pagination data
2. **PaginationController**: Manages pagination logic and state updates
3. **PaginationDataSource**: Interface for data loading
4. **PaginationCache**: Interface for caching with multiple implementations
5. **PaginationComponents**: Ready-to-use UI components

### State Management

The library uses a reactive state management approach:

```kotlin
data class PaginationState<T>(
    val items: List<T> = emptyList(),
    val currentPage: Int = 1,
    val pageSize: Int = 10,
    val totalItems: Int = 0,
    val totalPages: Int = 0,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null,
    val hasNextPage: Boolean = false,
    val hasPreviousPage: Boolean = false
)
```

### Actions

```kotlin
sealed class PaginationAction {
    object LoadFirstPage : PaginationAction()
    object LoadNextPage : PaginationAction()
    object LoadPreviousPage : PaginationAction()
    object LoadLastPage : PaginationAction()
    data class LoadPage(val page: Int) : PaginationAction()
    object Refresh : PaginationAction()
    data class UpdatePageSize(val newPageSize: Int) : PaginationAction()
}
```

## Integration with FakeStoreApp

The library is designed to integrate seamlessly with the existing FakeStoreApp architecture:

1. **Data Layer**: Implement `PaginationDataSource` in your repository
2. **Domain Layer**: Use `PaginationController` in your interactors
3. **UI Layer**: Use pagination components in your Compose screens
4. **ViewModel**: Extend `PaginationViewModel` or use controller directly

## Best Practices

1. **Cache Management**: Use appropriate cache size based on your data
2. **Error Handling**: Always handle network errors and provide retry mechanisms
3. **Loading States**: Show appropriate loading indicators for better UX
4. **Memory Management**: Clear cache when appropriate to avoid memory leaks
5. **Testing**: Mock `PaginationDataSource` for unit testing

## Testing

```kotlin
class PaginationControllerTest {
    @Test
    fun `should load first page successfully`() = runTest {
        val dataSource = FakeDataSource()
        val controller = PaginationController(dataSource)
        
        controller.handleAction(PaginationAction.LoadFirstPage)
        
        val state = controller.getCurrentState()
        assertEquals(1, state.currentPage)
        assertEquals(10, state.items.size)
    }
}
```

## Performance Considerations

- **Cache Size**: Configure appropriate cache size to balance memory usage and performance
- **Page Size**: Choose optimal page size based on your data and network conditions
- **Lazy Loading**: Components are designed for lazy loading to handle large datasets
- **Memory Management**: Use LRU cache for automatic memory management

## Dependencies

- Kotlin Coroutines
- Jetpack Compose
- StateFlow for reactive state management

## License

This library is part of the FakeStoreApp project and follows the same licensing terms.