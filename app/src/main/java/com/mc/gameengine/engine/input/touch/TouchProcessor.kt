package com.mc.gameengine.engine.input.touch

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
        touchManager.dispatch(TouchEvent.PressEvent(pos))
    }

    fun onMove(pos: Vec2) {
        touchManager.dispatch(
            TouchEvent.DragEvent(
                start = startPos,
                current = pos,
                delta = pos - startPos
            )
        )
    }

    fun onStop() = touchManager.dispatch(TouchEvent.StopDragEvent)

    fun onUp(pos: Vec2) {
        val duration = (System.currentTimeMillis() - downTime) / 1000f

        if (duration > 0.4f) {
            touchManager.dispatch(TouchEvent.LongPressEvent(pos, duration))
        } else {
            touchManager.dispatch(TouchEvent.TapEvent(pos))
        }
    }
}