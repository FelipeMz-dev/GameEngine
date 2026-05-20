package com.mc.engine.core

import com.mc.engine.assets.SpriteManager
import com.mc.engine.audio.AudioSystem
import com.mc.engine.input.GameInput

class SceneDependencies(
    val spriteManager: SpriteManager,
    val audioManager: AudioSystem,
    val gameInput: GameInput
)
