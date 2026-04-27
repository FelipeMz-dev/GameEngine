package com.mc.gameengine.game.instance

import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.distanceTo
import com.mc.gameengine.engine.math.minus
import com.mc.gameengine.engine.math.normalize

abstract class FallObject(
    private val initialHorizontalPosition: Float,
    private val gravity: Float = 300f
) : Instance() {
    protected var gravityValue: Float = gravity
        private set

    protected val groundPosition = 370f

    abstract val initialVerticalPosition: Float

    private var hasSlowGravity = false

    override fun onEnterScene() {
        updatePosition { it.copy(x = initialHorizontalPosition, y = initialVerticalPosition) }
    }

    override fun fixedUpdate(dt: Float) {
        computePosition(dt)
        updateVelocity { it.copy(y = it.y + gravityValue * dt) }

        if (current.position.y > groundPosition) {
            onGroundFell()
        }
    }

    protected fun stopFalling() {
        updateVelocity { Vec2.Zero }
        gravityValue = 0f
    }

    protected fun resumeFalling() {
        gravityValue = gravity
    }

    protected fun lowGravity() {
        if (!hasSlowGravity && gravityValue > 0f) {
            hasSlowGravity = true
            updateVelocity { Vec2.Zero }
            gravityValue = gravity / 4f
        }
    }

    abstract fun onGroundFell()

    fun magneticAttraction(from: Vec2) {
        if (gravity == 0f) return
        val distance = current.position.distanceTo(from)
        val direction = (from - current.position).normalize()
        val force = if (distance < 800) 200000f / (distance * distance) else 0f
        updateVelocity { it.copy(x = it.x + direction.x * force) }
    }
}