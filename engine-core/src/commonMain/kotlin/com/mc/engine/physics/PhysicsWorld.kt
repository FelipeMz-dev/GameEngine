package com.mc.engine.physics

import com.mc.engine.math.Vec2

data class PhysicsWorld(
    val gravity: Vec2 = Vec2(0f, 980f),
    val maxLinearSpeed: Float = 5000f,
    val jbox2dManager: PhysicsManager = PhysicsManager(gravity)
)
