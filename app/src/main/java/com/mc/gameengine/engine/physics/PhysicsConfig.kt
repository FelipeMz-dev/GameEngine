package com.mc.gameengine.engine.physics

enum class PhysicsSimulationMode {
    Dynamic,
    Kinematic,
    Static
}

data class PhysicsConfig(
    val mass: Float = 1f,
    val linearDamping: Float = 0f,
    val gravityScale: Float = 1f,
    val maxSpeed: Float = Float.POSITIVE_INFINITY,
    val mode: PhysicsSimulationMode = PhysicsSimulationMode.Dynamic
)
