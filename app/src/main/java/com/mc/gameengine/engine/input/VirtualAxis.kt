package com.mc.gameengine.engine.input

import com.mc.gameengine.core.math.Vec2
import com.mc.gameengine.core.math.clamp
import com.mc.gameengine.core.math.div
import com.mc.gameengine.core.math.minus

class VirtualAxis(
    private val id: String,
    private val center: Vec2,
    private val radius: Float,
    private val input: InputManager
) {
    fun onDrag(pos: Vec2) {
        val delta = (pos - center).clamp(radius)
        input.dispatch(
            AxisEvent(
                id = id,
                value = delta / radius
            )
        )
    }
}

