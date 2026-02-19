package com.mc.gameengine.engine.core

import com.mc.gameengine.engine.math.Vec2

data class TransformState(
    var position: Vec2 = Vec2(0f, 0f),
    var angle: Float = 0f,
    var scale: Vec2 = Vec2(1f, 1f)
)