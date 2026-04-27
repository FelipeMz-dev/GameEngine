package com.mc.gameengine.engine.input.mouse

class MouseManager {

    private val mouseListeners = mutableListOf<MouseListener>()

    fun register(listener: MouseListener) {
        mouseListeners += listener
    }

    fun unregister(listener: MouseListener) {
        mouseListeners -= listener
    }

    fun dispatchEvent(event: MouseEvent) {
        mouseListeners.forEach { it.onMouseEvent(event) }
    }

}