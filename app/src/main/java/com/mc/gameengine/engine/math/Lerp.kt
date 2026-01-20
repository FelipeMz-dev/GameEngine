package com.mc.gameengine.engine.math

import androidx.compose.ui.graphics.Color
import com.mc.gameengine.engine.core.TransformState

fun TransformState.lerp(
    to: TransformState,
    alpha: Float
) = TransformState(
    position = position.lerp(to.position, alpha),
    rotation = lerpAngle(rotation, to.rotation, alpha),
    scale = scale.lerp(to.scale, alpha),
    color = lerpColor(color, to.color, alpha),
)

private fun lerpAngle(a: Float, b: Float, t: Float): Float {
    val diff = ((b - a + Math.PI).mod(2 * Math.PI) - Math.PI).toFloat()
    return a + diff * t
}

private fun lerpColor(a: Color, b: Color, t: Float) = Color(
    red = a.red + (b.red - a.red) * t,
    green = a.green + (b.green - a.green) * t,
    blue = a.blue + (b.blue - a.blue) * t,
    alpha = a.alpha + (b.alpha - a.alpha) * t
)