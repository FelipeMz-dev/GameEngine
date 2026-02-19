package com.mc.gameengine.engine.physics

import com.mc.gameengine.engine.math.Vec2

data class PhysicsState(
    var velocity: Vec2 = Vec2(0f, 0f),
    var acceleration: Vec2 = Vec2(0f, 0f),
    var isGrounded: Boolean = false
)