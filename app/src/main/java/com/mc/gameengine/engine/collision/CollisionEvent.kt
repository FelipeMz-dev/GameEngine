package com.mc.gameengine.engine.collision

enum class CollisionPhase {
    Enter,
    Stay,
    Exit
}

data class CollisionEvent(
    val self: Any, // Collider legacy o SensorCollider backed by dyn4j.
    val other: Any, // Collider legacy o SensorCollider backed by dyn4j.
    val phase: CollisionPhase
)
