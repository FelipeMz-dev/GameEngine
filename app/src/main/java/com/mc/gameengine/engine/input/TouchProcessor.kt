package com.mc.gameengine.engine.input

import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.minus

class TouchProcessor(
    val touchManager: TouchManager
) {
    private var downTime = 0L
    private var startPos = Vec2(0f, 0f)

    fun onDown(pos: Vec2) {
        downTime = System.currentTimeMillis()
        startPos = pos
        touchManager.dispatch(PressEvent(pos))
    }

    fun onMove(pos: Vec2) {
        touchManager.dispatch(
            DragEvent(
                start = startPos,
                current = pos,
                delta = pos - startPos
            )
        )
    }

    fun onStop() = touchManager.dispatch(StopDragEvent)

    fun onUp(pos: Vec2) {
        val duration = (System.currentTimeMillis() - downTime) / 1000f

        if (duration > 0.4f) {
            touchManager.dispatch(LongPressEvent(pos, duration))
        } else {
            touchManager.dispatch(TapEvent(pos))
        }
    }
}