package com.mc.gameengine.engine.input

import com.mc.gameengine.core.math.Vec2
import com.mc.gameengine.engine.math.Vec3

sealed interface InputEvent

data class PressEvent(
    val position: Vec2
) : InputEvent

data class TapEvent(
    val position: Vec2
) : InputEvent

data class LongPressEvent(
    val position: Vec2,
    val duration: Float
) : InputEvent

data class DragEvent(
    val start: Vec2,
    val current: Vec2,
    val delta: Vec2
) : InputEvent

object StopDragEvent : InputEvent

data class AxisEvent(
    val id: String,
    val value: Vec2   // (-1..1)
) : InputEvent

data class ButtonEvent(
    val id: String,
    val pressed: Boolean
) : InputEvent

data class KeyEvent(
    val key: KeyCode,
    val pressed: Boolean
) : InputEvent

data class AccelerometerEvent(
    val value: Vec3
) : InputEvent