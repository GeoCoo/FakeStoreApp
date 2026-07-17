package com.android.core_ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.android.fakestore.core.core_resources.R

@Composable
fun NetworkImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.FillBounds
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        placeholder = painterResource(R.drawable.ic_logo),
        modifier = modifier,
        contentScale = contentScale
    )
}

/**
 * Renders the placeholder only in the Android Studio preview surface (no
 * network access there); the same composable loads real images at runtime.
 */
@Preview(showBackground = true)
@Composable
private fun NetworkImagePreview() {
    PreviewTheme {
        NetworkImage(
            url = "https://fakestoreapi.com/img/placeholder.jpg",
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )
    }
}
