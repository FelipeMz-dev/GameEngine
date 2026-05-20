package com.mc.engine.input.mouse

import com.mc.engine.input.MouseButton
import com.mc.engine.math.Vec2

sealed interface MouseEvent {

    data class MouseMoveCursorEvent(
        val position: Vec2
    ) : MouseEvent

    data class MouseClickEvent(
        val position: Vec2,
        val button: MouseButton
    ) : MouseEvent

    data class MouseReleaseEvent(
        val position: Vec2,
        val button: MouseButton
    ) : MouseEvent

    data class MouseScrollEvent(
        val position: Vec2,
        val scrollX: Float,
        val scrollY: Float
    ) : MouseEvent

    data class MouseDragEvent(
        val start: Vec2,
        val current: Vec2,
        val delta: Vec2,
        val button: MouseButton
    ) : MouseEvent
}