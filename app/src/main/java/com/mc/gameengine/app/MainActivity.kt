package com.mc.gameengine.app

import android.content.Context
import android.os.Bundle
import android.view.SurfaceView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.mc.gameengine.app.ui.theme.GameEngineTheme
import com.mc.gameengine.engine.compose.GameSceneView
import com.mc.gameengine.engine.compose.rememberAssetsManager
import com.mc.gameengine.engine.input.GameInput
import com.mc.gameengine.engine.render.VirtualResolution
import com.mc.gameengine.game.assets.registerMainSprites
import com.mc.gameengine.game.scene.CollisionScene
import com.mc.gameengine.game.scene.MainScene

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideBars()
        setContent {
            GameEngineTheme {
                Scaffold(Modifier.background(Color.White)) {
                    CollisionsScreen(Modifier.padding(it))
                }
            }
        }
    }

    private fun hideBars() {
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.systemBarsBehavior = WindowInsetsControllerCompat
            .BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
    }
}

@Composable
fun CollisionsScreen(modifier: Modifier) {

    val scene = remember { CollisionScene() }

    GameSceneView(
        modifier = modifier.fillMaxSize(),
        scene = scene,
    )
}

@Composable
fun ExampleScreen(modifier: Modifier) {

    val assetManager = rememberAssetsManager().registerMainSprites()
    val scene = remember { MainScene() }
    var paused by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        GameSceneView(
            modifier = modifier
                .fillMaxHeight()
                .aspectRatio(VirtualResolution.LandscapeHD.aspectRatio())
                .align(Alignment.Center),
            scene = scene,
            assetsManager = assetManager,
            virtualResolution = VirtualResolution.LandscapeHD,
            contentScale = ContentScale.Fit,
            isPaused = paused
        )

        FilledTonalIconButton(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopEnd),
            onClick = { paused = !paused }
        ) {
            Icon(Icons.Default.Settings, null)
        }
    }
}
