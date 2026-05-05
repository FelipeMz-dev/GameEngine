package com.mc.gameengine.game.instance.spec

import androidx.compose.ui.graphics.Color
import com.mc.gameengine.engine.math.Vec2

data class BlockSpec(
    val position: Vec2,
    val size: Vec2,
    val mass: Float,
    val color: Color
)