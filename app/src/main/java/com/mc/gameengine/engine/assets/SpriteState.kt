package com.mc.gameengine.engine.assets

import com.mc.gameengine.core.rendering.SpriteDefinition

data class SpriteState(
    val sprite: SpriteDefinition,
    val endFrame: Int = sprite.totalFrames,
    val startFrame: Int = 0,
    val frameDuration: Float = 0.1f,
    val loop: Boolean = true
)