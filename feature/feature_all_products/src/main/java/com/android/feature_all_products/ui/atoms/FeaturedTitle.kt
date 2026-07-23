package com.android.feature_all_products.ui.atoms

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.android.core.core_design_system.FakeStoreTheme
import com.android.fakestore.core.core_resources.R

@Composable
fun FeaturedTitle() {
    Text(
        modifier = Modifier.padding(top = FakeStoreTheme.spacing.md, bottom = FakeStoreTheme.spacing.sm),
        text = stringResource(id = R.string.featured_section_title),
        style = MaterialTheme.typography.headlineSmall.copy(
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
    )
}
