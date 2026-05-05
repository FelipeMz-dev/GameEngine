package com.mc.gameengine.engine.collision

enum class CollisionPhase {
    Enter,
    Stay,
    Exit
}

data class CollisionEvent(
    val self: Any, // Puede ser Collider o Fixture
    val other: Any, // Puede ser Collider o Fixture
    val phase: CollisionPhase
)
