package com.android.feature_menu.ui.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.android.core.core_design_system.FakeStoreTheme
import com.android.fakestore.core.core_resources.R
import com.android.feature_menu.ui.atoms.ThemeOptionRow
import com.android.session.ThemeMode

@Composable
fun ThemeSection(
    selectedMode: ThemeMode,
    onModeSelected: (ThemeMode) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(FakeStoreTheme.spacing.xs)) {
        Text(
            text = stringResource(id = R.string.menu_theme),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Column(Modifier.selectableGroup()) {
            ThemeOptionRow(
                label = stringResource(id = R.string.menu_theme_light),
                selected = selectedMode == ThemeMode.LIGHT,
                onClick = { onModeSelected(ThemeMode.LIGHT) }
            )
            ThemeOptionRow(
                label = stringResource(id = R.string.menu_theme_dark),
                selected = selectedMode == ThemeMode.DARK,
                onClick = { onModeSelected(ThemeMode.DARK) }
            )
            ThemeOptionRow(
                label = stringResource(id = R.string.menu_theme_auto),
                selected = selectedMode == ThemeMode.SYSTEM,
                onClick = { onModeSelected(ThemeMode.SYSTEM) }
            )
        }
    }
}
