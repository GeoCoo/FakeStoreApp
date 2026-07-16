package com.android.core_ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.core.core_design_system.FakeStoreTheme
import com.android.fakestore.core.core_resources.R

@Composable
fun ExpandableText(
    text: String,
    minimizedMaxLines: Int
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            text = text,
            maxLines = if (expanded) Int.MAX_VALUE else minimizedMaxLines,
            overflow = if (expanded) TextOverflow.Clip else TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
        )
        Text(
            text = if (expanded) stringResource(R.string.show_less_label) else stringResource(R.string.show_more_label),
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(top = FakeStoreTheme.spacing.xs)
                .clickable { expanded = !expanded }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExpandableTextPreview() {
    PreviewTheme {
        ExpandableText(
            text = "This is a sample product description that runs long enough to " +
                "demonstrate truncation and the show more / show less toggle behavior.",
            minimizedMaxLines = 2
        )
    }
}
