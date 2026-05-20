package com.mc.engine.physics

import com.mc.engine.math.Vec2

data class PhysicState(
    val linearVelocity: Vec2 = Vec2(0f, 0f),
    val angularVelocity: Float = 0f,
    val linearDamping: Float = 0f,
    val angularDamping: Float = 0f,
    val bullet: Boolean = false,
)
