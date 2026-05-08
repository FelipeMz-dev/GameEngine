package com.mc.gameengine.engine.physics

import com.mc.gameengine.engine.math.Vec2

data class PhysicState(
    val linearVelocity: Vec2,
    val angularVelocity: Float,
    val linearDamping: Float,
    val angularDamping: Float,
    val bullet: Boolean,
)