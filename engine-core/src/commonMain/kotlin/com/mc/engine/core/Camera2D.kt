package com.mc.engine.core

import com.mc.engine.math.Vec2

data class Camera2D(
    var position: Vec2 = Vec2.Zero,
    var zoom: Zoom = Zoom(1f),
    var rotation: CameraRotation = CameraRotation(0f),
    var viewportSize: Vec2 = Vec2.Zero
)

data class Zoom(
    val value: Float,
    val from: Vec2 = Vec2.Zero
)

data class CameraRotation(
    val angle: Float,
    val point: Vec2 = Vec2.Zero
)