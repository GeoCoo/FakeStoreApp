package com.android.feature_all_products.ui.atoms

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.android.fakestore.core.core_resources.R
import com.android.feature_all_products.ui.ProductsViewMode

@Composable
fun ViewModeToggle(
    viewMode: ProductsViewMode,
    onToggle: () -> Unit
) {
    IconButton(onClick = onToggle) {
        Icon(
            painter = painterResource(
                id = if (viewMode == ProductsViewMode.Grid) R.drawable.ic_list_view else R.drawable.ic_grid_view
            ),
            contentDescription = if (viewMode == ProductsViewMode.Grid)
                stringResource(id = R.string.switch_to_list_view)
            else
                stringResource(id = R.string.switch_to_grid_view),
            tint = MaterialTheme.colorScheme.onBackground
        )
    }
}
