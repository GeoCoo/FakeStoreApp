package com.android.feature_all_products.ui.organisms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.core.core_design_system.FakeStoreTheme
import com.android.core_ui.component.atoms.AppCard
import com.android.core_ui.component.atoms.NetworkImage
import com.android.core_ui.component.organisms.ProductActionsRow
import com.android.model.ProductDomain
import com.android.model.RatingDomain

@Composable
fun ProductItem(
    product: ProductDomain,
    onProductClick: (ProductDomain) -> Unit,
    onFavoriteClick: (ProductDomain) -> Unit,
    modifier: Modifier = Modifier,
) {
    AppCard(
        modifier = modifier.wrapContentHeight(),
        onClick = { onProductClick(product) },
        verticalArrangement = Arrangement.spacedBy(FakeStoreTheme.spacing.xs),
        horizontalAlignment = Alignment.Start
    ) {
        ProductActionsRow(product, onClick = {
            onFavoriteClick(product)
        })

        product.image?.let {
            NetworkImage(
                url = it,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        product.title?.let {
            Text(
                it,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        product.description?.let {
            Text(
                it,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
        product.price.let {
            Text(
                text = "$$it",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    }
}

@Preview
@Composable
fun ProductItemPreview() {
    ProductItem(
        ProductDomain(
            id = 1,
            title = "Product Title",
            description = "This is a sample product description that is quite long and should be truncated.",
            price = 29.99f,
            image = "https://example.com/image.jpg",
            category = "Electronics",
            rating = RatingDomain(10.0, 5),
        ), onProductClick = { }, onFavoriteClick = {})
}
