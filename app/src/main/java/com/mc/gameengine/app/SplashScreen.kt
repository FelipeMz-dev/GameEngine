package com.mc.gameengine.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.mc.gameengine.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    modifier: Modifier,
    timeOut: Long = 6000L,
    onFinished: () -> Unit
) {
    val painter = painterResource(R.drawable.splash_screen)

    LaunchedEffect(Unit) {
        delay(timeOut)
        onFinished()
    }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.5f
        )

        Image(
            modifier = Modifier.fillMaxSize().shimmerEffect(),
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Fit,
        )
    }
}