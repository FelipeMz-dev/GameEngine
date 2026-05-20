package com.mc.engine.input.keyboard

import com.mc.engine.input.KeyEvent
import com.mc.engine.input.KeyEventType
import com.mc.engine.input.KeyboardProcessor as AgnosticKeyboardProcessor
import com.mc.engine.input.ListenerRegistry

class KeyboardManager : AgnosticKeyboardProcessor {

    private val listeners = ListenerRegistry<KeyboardListener>()
    private val pressedKeys = mutableSetOf<KeyEvent>()

    override fun start() {
        // Nothing to do for manager
    }

    override fun stop() {
        pressedKeys.clear()
    }

    override fun getPressedKeys(): Set<KeyEvent> = pressedKeys.toSet()

    fun register(listener: KeyboardListener) {
        listeners.add(listener)
    }

    fun unregister(listener: KeyboardListener) {
        listeners.remove(listener)
    }

    fun dispatchKeyEvent(event: KeyboardEvent) {
        when (event) {
            is KeyboardEvent.KeyDown -> pressedKeys.add(
                KeyEvent(
                    key = event.key.toCommonKey(),
                    type = KeyEventType.Pressed
                )
            )
            is KeyboardEvent.KeyUp -> pressedKeys.removeIf { it.key == event.key.toCommonKey() }
            is KeyboardEvent.KeyHeld -> Unit
        }
        listeners.forEach { it.onKeyEvent(event) }
    }

    private fun androidx.compose.ui.input.key.Key.toCommonKey(): com.mc.engine.input.Key =
        when (this) {
            androidx.compose.ui.input.key.Key.DirectionUp -> com.mc.engine.input.Key.ArrowUp
            androidx.compose.ui.input.key.Key.DirectionDown -> com.mc.engine.input.Key.ArrowDown
            androidx.compose.ui.input.key.Key.DirectionLeft -> com.mc.engine.input.Key.ArrowLeft
            androidx.compose.ui.input.key.Key.DirectionRight -> com.mc.engine.input.Key.ArrowRight
            androidx.compose.ui.input.key.Key.Spacebar -> com.mc.engine.input.Key.Space
            androidx.compose.ui.input.key.Key.Enter -> com.mc.engine.input.Key.Enter
            androidx.compose.ui.input.key.Key.Escape -> com.mc.engine.input.Key.Escape
            androidx.compose.ui.input.key.Key.Backspace -> com.mc.engine.input.Key.Backspace
            androidx.compose.ui.input.key.Key.Tab -> com.mc.engine.input.Key.Tab
            androidx.compose.ui.input.key.Key.ShiftLeft,
            androidx.compose.ui.input.key.Key.ShiftRight -> com.mc.engine.input.Key.Shift
            androidx.compose.ui.input.key.Key.CtrlLeft,
            androidx.compose.ui.input.key.Key.CtrlRight -> com.mc.engine.input.Key.Control
            androidx.compose.ui.input.key.Key.AltLeft,
            androidx.compose.ui.input.key.Key.AltRight -> com.mc.engine.input.Key.Alt
            else -> runCatching { enumValueOf<com.mc.engine.input.Key>(toString()) }
                .getOrDefault(com.mc.engine.input.Key.Space)
        }
}
