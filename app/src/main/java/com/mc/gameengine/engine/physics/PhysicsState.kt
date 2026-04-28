package com.mc.gameengine.engine.physics

import com.mc.gameengine.engine.math.Vec2

/**
 * Estado base del cuerpo físico por instancia.
 * Está diseñado para convivir con el sistema de colisiones actual
 * y facilitar un futuro solver de físicas.
 */
data class PhysicsState(
    val velocity: Vec2 = Vec2.Zero,
    val acceleration: Vec2 = Vec2.Zero,
    val accumulatedForce: Vec2 = Vec2.Zero,
    val mass: Float = 1f,
    val linearDamping: Float = 0f,
    val gravityScale: Float = 0f,
    val maxSpeed: Float = Float.POSITIVE_INFINITY,
    val isKinematic: Boolean = false,
    val isGrounded: Boolean = false
) {
    val inverseMass: Float
        get() = if (mass <= 0f) 0f else 1f / mass
}

enum class ForceMode {
    Force,
    Acceleration,
    Impulse,
    VelocityChange
}
