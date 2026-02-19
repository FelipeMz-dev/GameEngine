package com.mc.gameengine.engine.input

interface KeyboardListener {
    fun onKeyEvent(event: KeyboardEvent)
}

class KeyboardManager {

    private val keyboardListeners = mutableSetOf<KeyboardListener>()

    fun register(listener: Any) {
        if (listener is KeyboardListener) keyboardListeners += listener
    }

    fun unregister(listener: Any) {
        if (listener is KeyboardListener) keyboardListeners -= listener
    }

    fun dispatchKeyEvent(event: KeyboardEvent) {
        keyboardListeners.forEach { it.onKeyEvent(event) }
    }
}

