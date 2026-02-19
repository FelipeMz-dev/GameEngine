package com.mc.gameengine.game.instance

import com.mc.gameengine.engine.math.Vec2

class BoostManager(
    private val speed: Float,
    private val duration: Int
) {
    private var time: Int = 0
    private var velocity = Vec2.Companion.Zero

    fun compute(current: Vec2): Vec2 {
        if (time > 0) time--
        else velocity = current
        return velocity
    }

    fun moveLeft() {
        if (time == 0) {
            time = duration
            velocity = velocity.copy(x = -speed)
        }
    }

    fun moveRight() {
        if (time == 0) {
            time = duration
            velocity = velocity.copy(x = speed)
        }
    }
}