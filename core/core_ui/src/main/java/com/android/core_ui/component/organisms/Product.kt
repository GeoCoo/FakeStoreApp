package com.android.core_ui.component.organisms

import com.android.core_ui.component.atoms.PercentageProgressCircle
import com.android.core_ui.component.util.PreviewTheme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.model.ProductDomain
import com.android.model.RatingDomain

@Composable
fun ProductActionsRow(product: ProductDomain?, onClick: (Int) -> Unit = {}) {
    Row(
        modifier = Modifier
            .height(30.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(modifier = Modifier.size(28.dp), onClick = {
            product?.id?.let { onClick(it) }
        }) {
            Icon(
                imageVector = if (product?.isFavorite == true) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxSize()
            )
        }

        PercentageProgressCircle(product?.rating?.rate?.toFloat())
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductActionsRowPreview() {
    PreviewTheme {
        ProductActionsRow(
            product = ProductDomain(
                id = 1,
                title = "Product Title",
                description = "Description",
                price = 29.99f,
                image = "https://example.com/image.jpg",
                category = "Electronics",
                rating = RatingDomain(4.5, 120),
                isFavorite = true,
            ),
            onClick = {}
        )
    }
}
