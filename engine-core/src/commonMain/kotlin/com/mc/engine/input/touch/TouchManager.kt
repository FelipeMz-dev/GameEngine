package com.mc.engine.input.touch

import com.mc.engine.input.ListenerRegistry
import com.mc.engine.input.TouchEvent
import com.mc.engine.input.TouchPhase
import com.mc.engine.input.TouchProcessor as AgnosticTouchProcessor

class TouchManager : AgnosticTouchProcessor {

    private val listeners = ListenerRegistry<TouchListener>()
    private val activeTouches = mutableListOf<TouchEvent>()

    override fun start() {
        // Nothing to do for manager
    }

    override fun stop() {
        activeTouches.clear()
    }

    override fun getTouches(): List<TouchEvent> = activeTouches.toList()

    fun register(listener: TouchListener) {
        listeners.add(listener)
    }

    fun unregister(listener: TouchListener) {
        listeners.remove(listener)
    }

    fun dispatch(event: TouchEvent) {
        when (event.phase) {
            TouchPhase.Began -> activeTouches.add(event)
            TouchPhase.Moved -> {
                val index = activeTouches.indexOfFirst { it.id == event.id }
                if (index >= 0) activeTouches[index] = event
            }
            TouchPhase.Ended, TouchPhase.Cancelled -> {
                activeTouches.removeIf { it.id == event.id }
            }
        }
        listeners.forEach { it.onTouchEvent(event) }
    }

    fun dispatchLocal(event: com.mc.engine.input.touch.TouchEvent) {
        // Handle local events if needed
    }
}
