package com.mc.gameengine.engine.input.keyboard

import com.mc.gameengine.engine.input.ListenerRegistry

class KeyboardManager {

    private val listeners = ListenerRegistry<KeyboardListener>()

    fun register(listener: KeyboardListener) {
        listeners.add(listener)
    }

    fun unregister(listener: KeyboardListener) {
        listeners.remove(listener)
    }

    fun dispatchKeyEvent(event: KeyboardEvent) {
        listeners.forEach { it.onKeyEvent(event) }
    }
}
