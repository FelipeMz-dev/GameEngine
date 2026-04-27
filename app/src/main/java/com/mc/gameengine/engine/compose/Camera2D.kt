package com.mc.gameengine.engine.compose

import com.mc.gameengine.engine.math.Vec2

data class Camera2D(
    var position: Vec2 = Vec2.Companion.Zero,
    var zoom: Zoom = Zoom(1f),
    var rotation: Rotation = Rotation(0f),
    var viewportSize: Vec2 = Vec2.Companion.Zero
)

data class Zoom(
    val value: Float,
    val from: Vec2 = Vec2.Companion.Zero
)

data class Rotation(
    val angle: Float,
    val from: Vec2 = Vec2.Companion.Zero
)