package com.mc.gameengine.engine.input.touch

import com.mc.gameengine.engine.math.Vec2

sealed interface TouchEvent {

    data class PressEvent(
        val position: Vec2
    ) : TouchEvent

    data class TapEvent(
        val position: Vec2
    ) : TouchEvent

    data class LongPressEvent(
        val position: Vec2,
        val duration: Float
    ) : TouchEvent

    data class DragEvent(
        val start: Vec2,
        val current: Vec2,
        val delta: Vec2
    ) : TouchEvent

    object StopDragEvent : TouchEvent

    data class AxisEvent(
        val id: String,
        val value: Vec2
    ) : TouchEvent

    data class ButtonEvent(
        val id: String,
        val pressed: Boolean
    ) : TouchEvent
}