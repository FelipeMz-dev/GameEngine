package com.mc.gameengine.engine.particles

import com.mc.gameengine.core.math.Vec2

data class Particle(
    var position: Vec2,
    var velocity: Vec2,
    var life: Float,
    var size: Float,
    var rotation: Float
)