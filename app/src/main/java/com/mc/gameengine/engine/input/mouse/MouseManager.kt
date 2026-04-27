package com.mc.gameengine.engine.input.mouse

import com.mc.gameengine.engine.input.ListenerRegistry

class MouseManager {

    private val listeners = ListenerRegistry<MouseListener>()

    fun register(listener: MouseListener) {
        listeners.add(listener)
    }

    fun unregister(listener: MouseListener) {
        listeners.remove(listener)
    }

    fun dispatchEvent(event: MouseEvent) {
        listeners.forEach { it.onMouseEvent(event) }
    }
}
