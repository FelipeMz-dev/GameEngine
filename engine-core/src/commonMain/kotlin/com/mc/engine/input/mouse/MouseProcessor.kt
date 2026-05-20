package com.mc.engine.input.mouse

import com.mc.engine.input.MouseButton
import com.mc.engine.math.Vec2
import com.mc.engine.math.minus

class MouseProcessor(
    val mouseManager: MouseManager
) {

    private var dragStart: Vec2? = null
    private var previousButton: MouseButton? = null

    fun mouseDown(position: Vec2, button: MouseButton) {
        dragStart?.let { return }
        val event = MouseEvent.MouseClickEvent(position, button)
        mouseManager.dispatchEvent(event)
        previousButton = button
        dragStart = position
    }

    fun mouseMove(position: Vec2) {
        val event = dragStart?.let { start ->

            val delta = position - start

            MouseEvent.MouseDragEvent(
                start = start,
                current = position,
                delta = delta,
                button = MouseButton.Left
            )
        } ?: MouseEvent.MouseMoveCursorEvent(position)

        mouseManager.dispatchEvent(event)
    }

    fun mouseUp(position: Vec2) {
        val button = previousButton ?: MouseButton.Left
        val event = MouseEvent.MouseReleaseEvent(position, button)
        mouseManager.dispatchEvent(event)
        dragStart = null
        previousButton = null
    }

    fun scroll(position: Vec2, scrollX: Float, scrollY: Float) {
        val event = MouseEvent.MouseScrollEvent(position, scrollX, scrollY)
        mouseManager.dispatchEvent(event)
    }

}