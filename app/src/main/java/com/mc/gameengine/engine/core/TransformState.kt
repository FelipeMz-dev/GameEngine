package com.mc.gameengine.engine.core

import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.render.Pivot

data class TransformState(
    var position: Vec2 = Vec2(0f, 0f),
    var angle: Float = 0f,
    var scale: Vec2 = Vec2(1f, 1f),
    var pivot: Pivot = Pivot.TopLeft,
    var flipX: Boolean = false,
    var flipY: Boolean = false
)