package com.android.core_ui.component.atoms

import com.android.core_ui.component.util.PreviewTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.core.core_design_system.FakeStoreTheme

/**
 * A generic surface card: rounded corners, surface background, optional
 * click handler. Content is laid out in a [Column].
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(FakeStoreTheme.spacing.xs),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    val shape = FakeStoreTheme.corners.medium
    Column(
        modifier = modifier
            .shadow(elevation = 2.dp, shape = shape, clip = false)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
            .padding(FakeStoreTheme.spacing.md),
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        content = content
    )
}

@Preview(showBackground = true)
@Composable
private fun AppCardPreview() {
    PreviewTheme {
        AppCard(
            modifier = Modifier.width(180.dp),
            onClick = {}
        ) {
            Text("Card title", style = MaterialTheme.typography.titleMedium)
            Text("Supporting body text goes here.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
