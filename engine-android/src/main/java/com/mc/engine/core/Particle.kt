package com.mc.engine.core.particles

import androidx.compose.ui.graphics.Color
import com.mc.engine.math.Vec2

data class Particle(
    var position: Vec2,
    var velocity: Vec2,
    var angle: Float,
    var life: Float,
    val rotation: Float,
    val scale: Vec2,
    val color: Color
)