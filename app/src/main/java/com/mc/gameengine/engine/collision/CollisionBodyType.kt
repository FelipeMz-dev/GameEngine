package com.mc.gameengine.engine.collision

enum class CollisionBodyType {
    Static,
    Dynamic,
    Kinematic
}

data class PhysicsMaterial(
    val density: Float = 1f,
    val friction: Float = 0.5f,
    val restitution: Float = 0f,
)
