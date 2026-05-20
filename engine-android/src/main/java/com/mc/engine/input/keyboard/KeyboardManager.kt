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
        when (event.type) {
            KeyEventType.Pressed -> pressedKeys.add(KeyEvent(event.key, KeyEventType.Pressed))
            KeyEventType.Released -> pressedKeys.removeIf { it.key == event.key }
        }
        listeners.forEach { it.onKeyEvent(event) }
    }
}
