package com.mc.gameengine.engine.collision

import com.mc.gameengine.engine.math.Vec2

enum class CollisionPhase {
    Enter,
    Stay,
    Exit
}

data class CollisionDetails(
    val point: Vec2? = null,
    val normal: Vec2 = Vec2.Zero,
    val depth: Float = 0f,
    val relativeVelocity: Vec2 = Vec2.Zero,
    val relativeSpeed: Float = 0f,
    val normalSpeed: Float = 0f,
    val normalImpulse: Float = 0f,
    val tangentImpulse: Float = 0f,
    val estimatedForce: Float = 0f,
    val isSensor: Boolean = false,
) {
    val force: Float
        get() = estimatedForce

    val impulse: Float
        get() = kotlin.math.sqrt(normalImpulse * normalImpulse + tangentImpulse * tangentImpulse)
}

data class CollisionEvent(
    val self: Any, // Collider legacy, SensorCollider o RigidBody backed by dyn4j.
    val other: Any, // Collider legacy, SensorCollider o RigidBody backed by dyn4j.
    val phase: CollisionPhase,
    val details: CollisionDetails = CollisionDetails(),
)
