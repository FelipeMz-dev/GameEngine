package com.mc.engine.input.touch

import com.mc.engine.math.Vec2
import com.mc.engine.math.clamp
import com.mc.engine.math.div
import com.mc.engine.math.minus

class VirtualAxis(
    private val id: String,
    private val center: Vec2,
    private val radius: Float,
    private val input: TouchManager
) {
    fun onDrag(pos: Vec2) {
        val delta = (pos - center).clamp(radius)
        input.dispatchLocal(
            TouchEvent.AxisEvent(
                id = id,
                value = delta / radius
            )
        )
    }
}