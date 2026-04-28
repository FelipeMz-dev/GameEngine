package com.mc.gameengine.engine.collision

enum class CollisionPhase {
    Enter,
    Stay,
    Exit
}

data class CollisionEvent(
    val self: Collider,
    val other: Collider,
    val phase: CollisionPhase
)
