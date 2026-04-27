package com.mc.gameengine.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mc.gameengine.R
import com.mc.gameengine.engine.compose.GameSceneView
import com.mc.gameengine.engine.compose.rememberAudioManager
import com.mc.gameengine.engine.compose.rememberSpriteManager
import com.mc.gameengine.game.assets.registerMainSprites
import com.mc.gameengine.game.scene.CollisionScene

@Composable
fun CollisionsScreen(modifier: Modifier, block: () -> Unit) {

    val spriteManager = rememberSpriteManager().registerMainSprites()
    val audioManager = rememberAudioManager {
        loadSound("explosion", resId = R.raw.snd_explosion)
        loadMusic("music_sample", resId = R.raw.music_sample)
    }
    val scene = remember { CollisionScene() }

    GameSceneView(
        modifier = modifier.fillMaxSize(),
        spriteManager = spriteManager,
        audioManager = audioManager,
        scene = scene,
    )

    Box(modifier.fillMaxSize()) {
        Button(
            modifier = Modifier.Companion.padding(10.dp),
            onClick = block
        ) { Text("show Keyboard") }

        Row(Modifier.Companion.align(Alignment.Companion.BottomCenter)) {
            Button(
                modifier = Modifier.Companion.padding(10.dp),
                onClick = { audioManager.playMusic("music_sample") }
            ) { Text("Play Music") }

            Button(
                modifier = Modifier.Companion.padding(10.dp),
                onClick = { audioManager.stopMusic("music_sample") }
            ) { Text("Stop Music") }

            Button(
                modifier = Modifier.Companion.padding(10.dp),
                onClick = { audioManager.pauseMusic("music_sample") }
            ) { Text("Pause Music") }

            Button(
                modifier = Modifier.Companion.padding(10.dp),
                onClick = { audioManager.resumeMusic("music_sample") }
            ) { Text("Resume Music") }
        }
    }
}