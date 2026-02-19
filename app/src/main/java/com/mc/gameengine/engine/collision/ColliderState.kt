package com.mc.gameengine.engine.collision

import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.render.Pivot

data class ColliderState(
    val position: Vec2 = Vec2.Zero,
    val angle: Float = 0f,
    val pivot: Pivot = Pivot.TopLeft,
    val scale: Vec2 = Vec2.from(1f)
)