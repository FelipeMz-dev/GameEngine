package com.mc.gameengine.engine.core

import androidx.compose.ui.graphics.Color
import com.mc.gameengine.core.math.Vec2

data class TransformState(
    var position: Vec2 = Vec2(0f, 0f),
    var rotation: Float = 0f,
    var scale: Vec2 = Vec2(1f, 1f),
    var color: Color = Color.White,
)