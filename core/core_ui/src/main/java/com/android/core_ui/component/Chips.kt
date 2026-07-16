package com.android.core_ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.android.core.core_design_system.FakeStoreTheme

/** A pill-shaped, tappable chip used for categories/filters. */
@Composable
fun SelectableChip(
    text: String,
    selected: Boolean = false,
    onClick: () -> Unit,
) {
    val background = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = contentColor
        ),
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(background, FakeStoreTheme.corners.full)
            .padding(horizontal = FakeStoreTheme.spacing.md, vertical = FakeStoreTheme.spacing.sm)
    )
}

@Preview(showBackground = true)
@Composable
private fun SelectableChipPreview() {
    PreviewTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(FakeStoreTheme.spacing.sm)) {
            SelectableChip(text = "All", selected = true, onClick = {})
            SelectableChip(text = "Electronics", selected = false, onClick = {})
            SelectableChip(text = "Jewelery", selected = false, onClick = {})
        }
    }
}
