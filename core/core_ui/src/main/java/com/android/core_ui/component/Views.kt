package com.android.core_ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.android.fakestore.core.core_resources.R

/**
 * Execute an effect based on [Lifecycle.Event]
 *
 *  @param lifecycleOwner The lifecycle owner
 *  @param lifecycleEvent The lifecycle event that code needs to be executed
 */
@Composable
fun LifecycleEffect(
    lifecycleOwner: LifecycleOwner, lifecycleEvent: Lifecycle.Event, block: () -> Unit,
) {
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == lifecycleEvent) {
                block()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}


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
                .padding(top = 4.dp)
                .clickable { expanded = !expanded }
        )
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = {
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        )
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

