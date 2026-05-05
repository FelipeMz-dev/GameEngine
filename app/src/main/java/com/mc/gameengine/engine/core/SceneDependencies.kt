package com.mc.gameengine.engine.core

import com.mc.gameengine.engine.assets.SpriteManager
import com.mc.gameengine.engine.audio.AudioManager
import com.mc.gameengine.engine.compose.Camera2D
import com.mc.gameengine.engine.input.GameInput

class SceneDependencies(
    val spriteManager: SpriteManager,
    val audioManager: AudioManager,
    val gameInput: GameInput
)
