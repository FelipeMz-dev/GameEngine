package com.mc.engine.physics

data class CollisionEvent(
    val self: Any, // Collider legacy, SensorCollider o RigidBody backed by dyn4j.
    val other: Any, // Collider legacy, SensorCollider o RigidBody backed by dyn4j.
    val phase: CollisionPhase,
    val details: CollisionDetails? = null,
)