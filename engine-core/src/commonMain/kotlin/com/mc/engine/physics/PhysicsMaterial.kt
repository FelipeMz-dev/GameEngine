package com.mc.engine.physics

data class PhysicsMaterial(
    val density: Float = 1f,
    val friction: Float = 0.5f,
    val restitution: Float = 0.5f,
)