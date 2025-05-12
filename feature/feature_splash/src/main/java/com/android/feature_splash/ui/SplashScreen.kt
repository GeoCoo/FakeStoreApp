package com.android.feature_splash.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.android.core_ui.component.LifecycleEffect
import com.android.fakestore.core.core_resources.R

@Composable
fun SplashScreen(onNavigate: (Boolean) -> Unit) {
    val viewModel = hiltViewModel<SplashViewModel>()
    val lifecycleOwner = LocalLifecycleOwner.current

    LifecycleEffect(
        lifecycleOwner = lifecycleOwner, lifecycleEvent = Lifecycle.Event.ON_CREATE
    ) {
        viewModel.setEvent(Event.CheckToken)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentDescription = "",
                    modifier = Modifier.size(96.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Stylish",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color(0xFFE91E63),
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            LinearProgressIndicator(
                modifier = Modifier
                    .width(120.dp)
                    .height(4.dp),
                color = MaterialTheme.colorScheme.background,
                trackColor = MaterialTheme.colorScheme.onBackground
            )
        }

    }
        LaunchedEffect(Unit) {
            viewModel.effect.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { effect ->
                    when (effect) {
                        is Effect.Navigate -> {
                            onNavigate(effect.isLogged)
                        }
                    }
                }
        }
    }
