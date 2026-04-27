package com.mc.gameengine.game.effects

import kotlin.random.Random

class ShakeEffect(
    private val time: Float = 1f,
    private val intensity: Float = 1f,
) {
    private var timer = time

    private val zoomOnAction = 1.02f

    var angle = 0f
        private set

    var zoom: Float = 1f
        private set

    val isFinished: Boolean
        get() = timer == 0f

    fun animate(dt: Float) {
        if (isFinished) return
        timer -= dt
        angle = Random.nextFloat() * intensity * if (angle < 0) 1 else -1
        zoom = zoomOnAction
        if (timer < 0f) {
            timer = 0f
            angle = 0f
            zoom = 1f
        }
    }

    fun restart() {
        timer = time
    }
}