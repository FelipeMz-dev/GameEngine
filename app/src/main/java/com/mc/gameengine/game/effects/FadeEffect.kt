package com.mc.gameengine.game.effects

class FadeEffect(
    private var step: Float = 0.1f
) {
    var alpha = 1f
        private set

    val isGone: Boolean
        get() = alpha == 0f

    fun animate() {
        if (isGone) return
        alpha -= step
        if (alpha < 0f) alpha = 0f
    }

    fun restart() {
        alpha = 1f
    }
}