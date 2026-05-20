package com.mc.engine.input.touch

import com.mc.engine.input.TouchEvent
import com.mc.engine.input.TouchPhase
import com.mc.engine.math.Vec2
import com.mc.engine.math.minus

class TouchProcessor(
    val touchManager: TouchManager
) {
    private var downTime = 0L
    private var startPos = Vec2(0f, 0f)
    private var touchId = 0

    fun onDown(pos: Vec2) {
        downTime = System.currentTimeMillis()
        startPos = pos
        touchManager.dispatch(TouchEvent(touchId, pos, TouchPhase.Began))
    }

    fun onMove(pos: Vec2) {
        touchManager.dispatch(TouchEvent(touchId, pos, TouchPhase.Moved))
    }

    fun onUp(pos: Vec2) {
        touchManager.dispatch(TouchEvent(touchId, pos, TouchPhase.Ended))
        touchId++
    }

    fun onCancel(pos: Vec2) {
        touchManager.dispatch(TouchEvent(touchId, pos, TouchPhase.Cancelled))
        touchId++
    }
}