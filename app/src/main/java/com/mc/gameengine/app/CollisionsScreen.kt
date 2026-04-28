package com.mc.gameengine.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.mc.gameengine.engine.compose.GameSceneView
import com.mc.gameengine.game.scene.PhysicsShowcaseScene

@Composable
fun CollisionsScreen(modifier: Modifier = Modifier) {

    val scene = remember { PhysicsShowcaseScene() }

    GameSceneView(
        modifier = modifier.fillMaxSize(),
        scene = scene,
    )
}