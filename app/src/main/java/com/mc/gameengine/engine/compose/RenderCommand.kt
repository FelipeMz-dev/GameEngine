package com.mc.gameengine.engine.compose

import androidx.compose.ui.graphics.drawscope.DrawScope

data class RenderCommand(
    val order: Int,
    val draw: DrawScope.() -> Unit
)