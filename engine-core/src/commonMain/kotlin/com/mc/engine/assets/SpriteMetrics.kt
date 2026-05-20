package com.mc.engine.assets

import com.mc.engine.math.Vec2
import com.mc.engine.render.Pivot

data class SpriteMetrics(
    val position: Vec2 = Vec2.Zero,
    val scale: Vec2 = Vec2(1f, 1f),
    val angle: Float = 0f,
    val pivot: Pivot = Pivot.TopLeft,
    val flipX: Boolean = false,
    val flipY: Boolean = false,
)