package com.mc.gameengine.engine.assets

import androidx.compose.ui.graphics.Color
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.render.Pivot

data class SpriteMetrics(
    val position: Vec2 = Vec2.Companion.Zero,
    val scale: Vec2 = Vec2(1f, 1f),
    val angle: Float = 0f,
    val color: Color = Color.Companion.White,
    val pivot: Pivot = Pivot.TopLeft,
    val flipX: Boolean = false,
    val flipY: Boolean = false,
    val deep: Int = 0
)