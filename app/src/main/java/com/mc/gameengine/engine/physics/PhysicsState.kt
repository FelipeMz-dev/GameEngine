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
    
    val angularVelocity: Float = 0f,
    val angularAcceleration: Float = 0f,
    val accumulatedTorque: Float = 0f,
    val externalAngularAcceleration: Float = 0f,
    
    val config: PhysicsConfig = PhysicsConfig(),
    val isGrounded: Boolean = false
) {
    val inverseMass: Float
        get() = if (config.mass <= 0f || config.mode != PhysicsSimulationMode.Dynamic) 0f else 1f / config.mass

    val inverseMomentOfInertia: Float
        get() = if (config.momentOfInertia <= 0f || config.mode != PhysicsSimulationMode.Dynamic) 0f else 1f / config.momentOfInertia
}
