package com.mc.gameengine.engine.time

class SmoothValue(
    private val alpha: Float = 0.15f
) {
    var value = 0f
        private set

    fun update(newValue: Float): Float {
        value += alpha * (newValue - value)
        return value
    }
}