package com.mc.gameengine.engine.particles

import com.mc.gameengine.core.math.Vec2

data class ParticleConfig(
    val life: ClosedRange<Float>,
    val speed: ClosedRange<Float>,
    val angle: ClosedRange<Float>,
    val scale: ClosedRange<Float>,
    val gravity: Vec2 = Vec2.Zero,
    val damping: Float = 1f
)