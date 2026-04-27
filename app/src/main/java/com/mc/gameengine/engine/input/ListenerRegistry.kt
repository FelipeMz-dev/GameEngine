package com.mc.gameengine.engine.input

internal class ListenerRegistry<T> {

    private val listeners = linkedSetOf<T>()

    fun add(listener: T) {
        listeners += listener
    }

    fun remove(listener: T) {
        listeners -= listener
    }

    inline fun forEach(action: (T) -> Unit) {
        listeners.forEach(action)
    }
}
