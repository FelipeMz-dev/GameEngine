package com.mc.gameengine.game.instance

class HitAnimation(private val duration: Float) {
    private var timer = 0f

    var isAnimating = false

    fun update(dt: Float) {
        if (!isAnimating) return
        if (timer > duration) {
            timer = 0f
            isAnimating = false
            return
        }
        timer += dt
    }

    fun animate() {
        isAnimating = true
    }

}