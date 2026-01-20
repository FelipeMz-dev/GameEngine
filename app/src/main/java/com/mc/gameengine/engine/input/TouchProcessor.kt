package com.mc.gameengine.engine.input

import com.mc.gameengine.core.math.Vec2
import com.mc.gameengine.core.math.minus

class TouchProcessor(
    private val input: InputManager
) {
    private var downTime = 0L
    private var startPos = Vec2(0f, 0f)

    fun onDown(pos: Vec2) {
        downTime = System.currentTimeMillis()
        startPos = pos
        input.dispatch(PressEvent(pos))

    }

    fun onMove(pos: Vec2) {
        input.dispatch(
            DragEvent(
                start = startPos,
                current = pos,
                delta = pos - startPos
            )
        )
    }

    fun onStop() = input.dispatch(StopDragEvent)

    fun onUp(pos: Vec2) {
        val duration = (System.currentTimeMillis() - downTime) / 1000f

        if (duration > 0.4f) {
            input.dispatch(LongPressEvent(pos, duration))
        } else {
            input.dispatch(TapEvent(pos))
        }
    }
}