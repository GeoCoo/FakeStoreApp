package com.android.core_ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.core.core_design_system.FakeStoreShapes
import com.android.core.core_design_system.FakeStoreTheme
import com.android.core.core_design_system.FakeStoreTypography
import com.android.core.core_design_system.LightColors

/**
 * Lightweight stand-in for [FakeStoreTheme] usable in @Preview functions.
 * [FakeStoreTheme] is `@RequiresApi(S)` because it drives the system bar
 * colors as a side effect - previews just need the color/type/shape tokens.
 */
@Composable
internal fun PreviewTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = FakeStoreTypography,
        shapes = FakeStoreShapes,
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            content()
        }
    }
}
