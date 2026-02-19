package com.mc.gameengine.engine.particles

import androidx.compose.ui.graphics.Color
import com.mc.gameengine.engine.math.Vec2

data class ParticleConfig(
    val life: ClosedRange<Float>,
    val speed: ClosedRange<Float>,
    val angle: ClosedRange<Float> = 0f..0f,
    val scale: ClosedRange<Float> = 1f..1f,
    val rotation: ClosedRange<Float> = 0f..0f,
    val direction: ClosedRange<Float> = 0f..360f,
    val color: List<Color> = listOf(Color.White),
    val gravity: Vec2 = Vec2.Zero,
    val damping: Float = 1f
)