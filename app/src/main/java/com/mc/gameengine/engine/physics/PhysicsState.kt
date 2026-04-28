package com.mc.gameengine.engine.physics

import com.mc.gameengine.engine.math.Vec2

/**
 * Estado físico por instancia.
 * Mantiene únicamente datos; la integración se centraliza en [PhysicsIntegrator].
 */
data class PhysicsState(
    val velocity: Vec2 = Vec2.Zero,
    val acceleration: Vec2 = Vec2.Zero,
    val accumulatedForce: Vec2 = Vec2.Zero,
    val externalAcceleration: Vec2 = Vec2.Zero,
    val config: PhysicsConfig = PhysicsConfig(),
    val isGrounded: Boolean = false
) {
    val inverseMass: Float
        get() = if (config.mass <= 0f || config.mode != PhysicsSimulationMode.Dynamic) 0f else 1f / config.mass
}
