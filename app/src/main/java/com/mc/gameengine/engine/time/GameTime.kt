package com.mc.gameengine.core.time

internal class GameTime {

    var deltaTime: Float = 0f
        private set

    var alpha: Float = 0f
        private set

    var accumulator: Float = 0f

    private var lastFrameTime = 0L


    fun alignTo(frameTimeNanos: Long) {
        lastFrameTime = frameTimeNanos
        deltaTime = 0f
        accumulator = 0f
        alpha = 0f
    }

    fun fixedUpdate(frameTimeNanos: Long) {
        if (lastFrameTime == 0L) {
            lastFrameTime = frameTimeNanos
            return
        }

        var frameDelta = (frameTimeNanos - lastFrameTime) / 1_000_000_000f
        lastFrameTime = frameTimeNanos
        frameDelta = frameDelta.coerceAtMost(TimeConfig.MAX_FRAME_TIME)
        deltaTime = frameDelta
        accumulator += frameDelta

        alpha = (accumulator / TimeConfig.FIXED_DELTA_60).coerceIn(0f, 1f)
    }
}