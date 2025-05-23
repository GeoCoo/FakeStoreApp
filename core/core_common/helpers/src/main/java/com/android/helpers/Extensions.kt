package com.android.helpers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

fun <T, K> List<T>?.buildCategoryList(
    allCategoryLabel: String,
    allCategoryId: String,
    categorySelector: (T) -> String?,
    categoryMapper: (label: String, id: String) -> K
): List<K> = buildList {
    if (this@buildCategoryList.isNullOrEmpty()) return@buildList
    add(categoryMapper(allCategoryLabel, allCategoryId))
    this@buildCategoryList
        .mapNotNull(categorySelector)
        .distinct()
        .forEach { category ->
            add(categoryMapper(category.replaceFirstChar { it.uppercase() }, category))
        }
}

fun <T> Flow<T>.safeAsync(with: (Throwable) -> (T)): Flow<T> {
    return this.flowOn(Dispatchers.IO).catch { emit(with(it)) }
}