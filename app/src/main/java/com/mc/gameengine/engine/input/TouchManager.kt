package com.mc.gameengine.engine.input

interface TouchListener {
    fun onTouchEvent(event: TouchEvent)
}

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