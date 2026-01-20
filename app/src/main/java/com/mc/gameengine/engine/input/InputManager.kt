package com.mc.gameengine.engine.input

interface InputListener {
    fun onInput(event: InputEvent)
}

class InputManager {

    private val listeners = mutableSetOf<InputListener>()

    fun register(listener: InputListener) {
        listeners += listener
    }

    fun unregister(listener: InputListener) {
        listeners -= listener
    }

    fun dispatch(event: InputEvent) {
        listeners.forEach { it.onInput(event) }
    }
}