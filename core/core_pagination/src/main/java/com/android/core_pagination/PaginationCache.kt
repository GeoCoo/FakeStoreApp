package com.android.core_pagination

/**
 * Generic interface for paginated data sources
 */
interface PaginationDataSource<T> {
    suspend fun loadPage(page: Int, pageSize: Int): PaginationResult<T>
    suspend fun getTotalItemCount(): Int
}

/**
 * Cache interface for storing paginated data
 */
interface PaginationCache<T> {
    fun put(page: Int, items: List<T>)
    fun get(page: Int): List<T>?
    fun clear()
    fun clearPage(page: Int)
    fun containsPage(page: Int): Boolean
    fun getAllCachedItems(): Map<Int, List<T>>
    fun invalidate()
}

/**
 * In-memory implementation of PaginationCache
 */
class InMemoryPaginationCache<T> : PaginationCache<T> {
    private val cache = mutableMapOf<Int, List<T>>()
    
    override fun put(page: Int, items: List<T>) {
        cache[page] = items
    }
    
    override fun get(page: Int): List<T>? {
        return cache[page]
    }
    
    override fun clear() {
        cache.clear()
    }
    
    override fun clearPage(page: Int) {
        cache.remove(page)
    }
    
    override fun containsPage(page: Int): Boolean {
        return cache.containsKey(page)
    }
    
    override fun getAllCachedItems(): Map<Int, List<T>> {
        return cache.toMap()
    }
    
    override fun invalidate() {
        clear()
    }
}

/**
 * LRU cache implementation for pagination
 */
class LruPaginationCache<T>(private val maxSize: Int = 10) : PaginationCache<T> {
    private val cache = LinkedHashMap<Int, List<T>>(16, 0.75f, true)
    
    override fun put(page: Int, items: List<T>) {
        cache[page] = items
        if (cache.size > maxSize) {
            val iterator = cache.iterator()
            iterator.next()
            iterator.remove()
        }
    }
    
    override fun get(page: Int): List<T>? {
        return cache[page]
    }
    
    override fun clear() {
        cache.clear()
    }
    
    override fun clearPage(page: Int) {
        cache.remove(page)
    }
    
    override fun containsPage(page: Int): Boolean {
        return cache.containsKey(page)
    }
    
    override fun getAllCachedItems(): Map<Int, List<T>> {
        return cache.toMap()
    }
    
    override fun invalidate() {
        clear()
    }
}