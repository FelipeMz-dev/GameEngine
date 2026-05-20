package com.mc.engine.assets

import com.mc.engine.core.SpriteId
import com.mc.engine.core.TransformState

open class Sprite(
    private val sprite: SpriteDefinition,
    var state: TransformState = TransformState(),
    var endFrame: Int = sprite.totalFrames - 1,
    var startFrame: Int = 0,
    var frameDuration: Float = 0.1f
) {
    private var inMovement = true
    private var timer = 0f

    var currentFrame = startFrame
        private set

    val spriteId get() = sprite.spriteId

    fun animate(dt: Float) {
        if (inMovement) timer += dt
        if (timer >= frameDuration) {
            timer = 0f
            currentFrame++

            if (currentFrame > endFrame) {
                currentFrame = startFrame
            }
        }
    }

    fun update(block: (TransformState) -> TransformState) {
        state = block(state)
    }

    fun nextFrame() {
        currentFrame++
        if (currentFrame > endFrame) currentFrame = startFrame
    }

    fun previousFrame() {
        currentFrame--
        if (currentFrame < startFrame) currentFrame = endFrame
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
