package com.android.feature_all_products.ui.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.core.core_design_system.FakeStoreTheme
import com.android.core_ui.component.atoms.SelectableChip
import com.android.model.Category

@Composable
fun CategoryRow(
    categories: List<Category>?,
    selectedCategory: Category?,
    onCategoryCLick: (Category) -> Unit
) {
    LazyRow(
        modifier = Modifier.padding(bottom = FakeStoreTheme.spacing.md),
        horizontalArrangement = Arrangement.spacedBy(FakeStoreTheme.spacing.sm)
    ) {
        categories?.size?.let {
            items(it) { index ->
                categories[index].let { category ->
                    SelectableChip(
                        text = category.categoryName,
                        selected = category == selectedCategory,
                        onClick = { onCategoryCLick(category) }
                    )
                }
            }
        }
    }
}
