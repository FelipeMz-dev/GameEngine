package com.mc.engine.math

import com.mc.engine.core.TransformState

fun TransformState.lerp(
    to: TransformState,
    alpha: Float
) = TransformState(
    position = position.lerp(to.position, alpha),
    angle = lerpAngle(angle, to.angle, alpha),
    scale = scale.lerp(to.scale, alpha),
)

private fun lerpAngle(a: Float, b: Float, t: Float): Float {
    val diff = ((b - a + Math.PI).mod(2 * Math.PI) - Math.PI).toFloat()
    return a + diff * t
}