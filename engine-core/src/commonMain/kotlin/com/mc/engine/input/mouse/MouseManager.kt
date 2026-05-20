package com.mc.engine.input.mouse

import com.mc.engine.input.ListenerRegistry
import com.mc.engine.input.MouseButton
import com.mc.engine.input.MouseProcessor as AgnosticMouseProcessor
import com.mc.engine.math.Vec2

class MouseManager : AgnosticMouseProcessor {

    private val listeners = ListenerRegistry<MouseListener>()
    private var mousePosition = Vec2.Zero
    private val pressedButtons = mutableSetOf<MouseButton>()

    override fun start() {
        // Nothing to do for manager
    }

    override fun stop() {
        pressedButtons.clear()
        mousePosition = Vec2.Zero
    }

    override fun getMousePosition(): Vec2 = mousePosition

    override fun getMouseButtons(): Set<MouseButton> = pressedButtons.toSet()

    fun register(listener: MouseListener) {
        listeners.add(listener)
    }

    fun unregister(listener: MouseListener) {
        listeners.remove(listener)
    }

    fun dispatchEvent(event: MouseEvent) {
        when (event) {
            is MouseEvent.MouseMoveCursorEvent -> mousePosition = event.position
            is MouseEvent.MouseClickEvent -> pressedButtons.add(event.button)
            is MouseEvent.MouseReleaseEvent -> pressedButtons.remove(event.button)
            else -> {} // Scroll and drag don't affect state
        }
        listeners.forEach { it.onMouseEvent(event) }
    }
}
