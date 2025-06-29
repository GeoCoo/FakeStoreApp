# Pagination Library Usage Examples

This document provides practical examples of how to use the pagination library in the FakeStoreApp project.

## Example 1: Basic Integration (Using PaginatedAllProductsScreen)

The `PaginatedAllProductsScreen` demonstrates a complete integration of the pagination library:

```kotlin
// In your navigation setup
@Composable
fun AppNavigation() {
    NavHost(navController = navController, startDestination = "products") {
        composable("products") {
            PaginatedAllProductsScreen(
                onProductClick = { product ->
                    navController.navigate("product/${product.id}")
                }
            )
        }
    }
}
```

## Example 2: Custom ViewModel Implementation

Create your own paginated ViewModel by extending `PaginationViewModel`:

```kotlin
@HiltViewModel
class CustomProductsViewModel @Inject constructor(
    private val repository: ProductsRepository
) : PaginationViewModel<ProductDomain>() {
    
    init {
        val dataSource = ProductsPaginationDataSource(
            productsRepository = repository,
            productMapper = { dto -> dto.toDomain() }
        )
        
        initializePagination(
            dataSource = dataSource,
            initialPageSize = 15,
            cacheEnabled = true
        )
        
        loadInitialPage()
    }
    
    fun searchProducts(query: String) {
        // Implement search logic
        refreshData()
    }
}
```

## Example 3: Custom Data Source

Implement your own data source for different types of data:

```kotlin
class CustomDataSource @Inject constructor(
    private val apiService: ApiService
) : PaginationDataSource<MyDataType> {
    
    override suspend fun loadPage(page: Int, pageSize: Int): PaginationResult<MyDataType> {
        return try {
            val response = apiService.getData(page, pageSize)
            if (response.isSuccessful) {
                PaginationResult.Success(
                    items = response.body()?.data ?: emptyList(),
                    currentPage = page,
                    totalItems = response.body()?.totalItems ?: 0
                )
            } else {
                PaginationResult.Error("API error: ${response.code()}")
            }
        } catch (e: Exception) {
            PaginationResult.Error(e.message ?: "Unknown error")
        }
    }
    
    override suspend fun getTotalItemCount(): Int {
        return apiService.getTotalCount()
    }
}
```

## Example 4: Custom UI Components

Create custom pagination controls:

```kotlin
@Composable
fun CustomPaginationControls(
    state: PaginationState<ProductDomain>,
    onAction: (PaginationAction) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Button(
            onClick = { onAction(PaginationAction.LoadFirstPage) },
            enabled = state.hasPreviousPage
        ) {
            Text("First")
        }
        
        Button(
            onClick = { onAction(PaginationAction.LoadPreviousPage) },
            enabled = state.hasPreviousPage
        ) {
            Text("Previous")
        }
        
        Text(
            text = "Page ${state.currentPage} of ${state.totalPages}",
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        
        Button(
            onClick = { onAction(PaginationAction.LoadNextPage) },
            enabled = state.hasNextPage
        ) {
            Text("Next")
        }
        
        Button(
            onClick = { onAction(PaginationAction.LoadLastPage) },
            enabled = state.hasNextPage
        ) {
            Text("Last")
        }
    }
}
```

## Example 5: Using Compose Wrappers

Simplify integration with Compose helpers:

```kotlin
@Composable
fun MyPaginatedScreen() {
    val dataSource = remember { MyDataSource() }
    val controller = rememberPaginationController(
        dataSource = dataSource,
        initialPageSize = 20
    )
    val state by controller.state.collectAsState()
    
    LaunchedPaginationEffect(controller)
    
    Column {
        PaginationStateHandler(
            state = state,
            onRetry = { controller.handleAction(PaginationAction.Refresh) }
        ) { items ->
            LazyColumn {
                items(items) { item ->
                    MyItemComponent(item = item)
                }
            }
        }
        
        CompactPaginationControls(
            state = state,
            onAction = { controller.handleAction(it) }
        )
    }
}
```

## Example 6: Cache Configuration

Configure different cache strategies:

```kotlin
// In-memory cache (default)
val inMemoryCache = InMemoryPaginationCache<ProductDomain>()

// LRU cache with custom size
val lruCache = LruPaginationCache<ProductDomain>(maxSize = 50)

// Using with controller
val controller = PaginationController(
    dataSource = dataSource,
    cache = lruCache,
    initialPageSize = 10,
    cacheEnabled = true
)
```

## Example 7: Error Handling

Handle errors gracefully:

```kotlin
@Composable
fun PaginatedScreenWithErrorHandling() {
    val controller = rememberPaginationController(dataSource)
    val state by controller.state.collectAsState()
    
    Column {
        PaginationStateHandler(
            state = state,
            onRetry = { 
                controller.handleAction(PaginationAction.Refresh) 
            },
            errorContent = { message ->
                ErrorCard(
                    message = message,
                    onRetry = { 
                        controller.handleAction(PaginationAction.Refresh) 
                    },
                    onGoBack = { /* navigation logic */ }
                )
            }
        ) { items ->
            // Content
        }
    }
}

@Composable
fun ErrorCard(
    message: String,
    onRetry: () -> Unit,
    onGoBack: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                TextButton(onClick = onGoBack) {
                    Text("Go Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onRetry) {
                    Text("Retry")
                }
            }
        }
    }
}
```

## Example 8: Integration with Existing MVI Architecture

The library works seamlessly with the existing MVI pattern:

```kotlin
// In your existing ViewModel
class MyViewModel : MviViewModel<Event, State, Effect>() {
    
    private val paginationController = PaginationController(...)
    
    override fun handleEvents(event: Event) {
        when (event) {
            is Event.LoadNextPage -> {
                viewModelScope.launch {
                    paginationController.handleAction(PaginationAction.LoadNextPage)
                }
            }
            // Handle other events
        }
    }
    
    // Expose pagination state
    val paginationState = paginationController.state
}
```

## Example 9: Testing

Test your pagination implementation:

```kotlin
@Test
fun `should load first page successfully`() = runTest {
    // Arrange
    val mockDataSource = MockDataSource()
    val controller = PaginationController(
        dataSource = mockDataSource,
        cacheEnabled = false
    )
    
    // Act
    controller.handleAction(PaginationAction.LoadFirstPage)
    
    // Assert
    val state = controller.getCurrentState()
    assertEquals(1, state.currentPage)
    assertEquals(10, state.items.size)
    assertTrue(state.hasNextPage)
}

class MockDataSource : PaginationDataSource<TestItem> {
    override suspend fun loadPage(page: Int, pageSize: Int): PaginationResult<TestItem> {
        val items = (1..pageSize).map { TestItem(id = it + (page - 1) * pageSize) }
        return PaginationResult.Success(
            items = items,
            currentPage = page,
            totalItems = 100
        )
    }
    
    override suspend fun getTotalItemCount(): Int = 100
}
```

## Performance Tips

1. **Cache Size**: Configure appropriate cache size based on your data size and memory constraints
2. **Page Size**: Choose optimal page size (typically 10-20 items for mobile)
3. **Preloading**: Consider implementing preloading for better UX
4. **Memory Management**: Clear cache when appropriate to avoid memory leaks
5. **Network Optimization**: Implement proper retry logic and handle network errors

## Migration from Non-Paginated Implementation

To migrate from a non-paginated implementation:

1. **Replace Repository Calls**: Change from `getAllProducts()` to `getProductsPaginated()`
2. **Update ViewModel**: Extend `PaginationViewModel` or use `PaginationController`
3. **Update UI**: Replace list rendering with `PaginationStateHandler`
4. **Add Controls**: Include pagination controls in your UI
5. **Handle States**: Implement proper loading, error, and empty states

This completes the comprehensive examples for using the pagination library in various scenarios within the FakeStoreApp project.