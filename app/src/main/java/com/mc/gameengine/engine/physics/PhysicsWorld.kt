package com.mc.gameengine.engine.physics

import com.mc.gameengine.engine.math.Vec2

data class PhysicsWorld(
    val gravity: Vec2 = Vec2(0f, 980f),
    val maxLinearSpeed: Float = 5000f
)
