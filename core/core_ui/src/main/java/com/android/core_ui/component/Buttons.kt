package com.android.core_ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.core.core_design_system.FakeStoreTheme

/** Primary, filled call-to-action button. */
@Composable
fun ActionButton(text: String, onClick: () -> Unit, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = FakeStoreTheme.corners.small,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        )
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

/** Secondary, outlined action button for less prominent actions. */
@Composable
fun SecondaryActionButton(text: String, onClick: () -> Unit, enabled: Boolean = true) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = FakeStoreTheme.corners.small,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
        )
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Preview(showBackground = true)
@Composable
private fun ButtonsPreview() {
    PreviewTheme {
        Column(verticalArrangement = Arrangement.spacedBy(FakeStoreTheme.spacing.sm)) {
            ActionButton(text = "Continue", onClick = {})
            ActionButton(text = "Disabled", onClick = {}, enabled = false)
            SecondaryActionButton(text = "Cancel", onClick = {})
            SecondaryActionButton(text = "Disabled", onClick = {}, enabled = false)
        }
    }
}
