package com.mc.gameengine.engine.input.touch

class TouchManager {

    private val listeners = mutableSetOf<TouchListener>()

    fun register(listener: TouchListener) {
        listeners += listener
    }

    fun unregister(listener: TouchListener) {
        listeners -= listener
    }

    fun dispatch(event: TouchEvent) {
        listeners.forEach { it.onTouchEvent(event) }
    }
}