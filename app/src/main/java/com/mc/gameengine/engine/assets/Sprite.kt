package com.mc.gameengine.engine.assets

import com.mc.gameengine.core.rendering.SpriteDefinition
import com.mc.gameengine.engine.core.SpriteId

class Sprite(
    private val sprite: SpriteDefinition,
    var endFrame: Int = sprite.totalFrames,
    var startFrame: Int = 0,
    var frameDuration: Float = 0.1f
) {
    private var inMovement = true
    private var timer = 0f

    var currentFrame = startFrame
        private set

    val spriteId get() = sprite.spriteId

    fun update(dt: Float) {
        if (inMovement) timer += dt
        if (timer >= frameDuration) {
            timer = 0f
            currentFrame++

            if (currentFrame > endFrame) {
                currentFrame = startFrame
            }
        }
    }

    fun spriteIs(id: SpriteId) = id == sprite.spriteId

    fun isLastFrame() = currentFrame == endFrame

    fun stop() {
        inMovement = false
    }

    fun start() {
        inMovement = true
    }
}