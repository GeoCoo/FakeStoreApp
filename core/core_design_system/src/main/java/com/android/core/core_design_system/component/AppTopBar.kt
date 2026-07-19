package com.android.core.core_design_system.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.core.core_design_system.FakeStoreShapes
import com.android.core.core_design_system.FakeStoreTheme
import com.android.core.core_design_system.FakeStoreTypography
import com.android.core.core_design_system.LightColors

/**
 * The standard top bar used across the app: an optional back button, a
 * centered title (plain text or custom content), and optional trailing
 * actions. Any slot can be omitted to match a given screen's needs.
 */
@Composable
fun AppTopBar(
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    title: String? = null,
    titleContent: (@Composable () -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = FakeStoreTheme.spacing.sm)
    ) {
        if (onBackClick != null) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        if (titleContent != null || title != null) {
            Box(modifier = Modifier.align(Alignment.Center)) {
                if (titleContent != null) {
                    titleContent()
                } else if (title != null) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        if (actions != null) {
            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                verticalAlignment = Alignment.CenterVertically,
                content = actions
            )
        }
    }
}

/**
 * Large, static-title variant for entry/onboarding screens (e.g. login) —
 * wraps Material3's [LargeTopAppBar] with the app's typography and colors.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLargeTopBar(
    title: String,
    modifier: Modifier = Modifier,
    titleStyle: TextStyle? = null,
    onBackClick: (() -> Unit)? = null,
) {
    LargeTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                style = titleStyle ?: MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        },
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@Composable
private fun PreviewTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = FakeStoreTypography,
        shapes = FakeStoreShapes,
        content = content
    )
}

@Preview(showBackground = true)
@Composable
private fun AppTopBarBackAndActionPreview() {
    PreviewTheme {
        AppTopBar(
            onBackClick = {},
            actions = {
                IconButton(onClick = {}) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppTopBarBackOnlyPreview() {
    PreviewTheme {
        AppTopBar(onBackClick = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun AppLargeTopBarPreview() {
    PreviewTheme {
        AppLargeTopBar(title = "Welcome\nBack!")
    }
}
