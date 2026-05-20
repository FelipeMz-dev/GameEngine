package com.mc.engine.input.keyboard

import androidx.compose.ui.input.key.Key

sealed interface KeyboardEvent {
    val key: Key

    data class KeyDown(override val key: Key) : KeyboardEvent

    data class KeyUp(override val key: Key) : KeyboardEvent

    data class KeyHeld(override val key: Key, val dt: Float) : KeyboardEvent
}

