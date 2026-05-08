package com.mc.gameengine.engine.input

import com.mc.gameengine.engine.core.GameObject

internal class ListenerBinding<T : Any>(
    private val listenerClass: Class<T>,
    private val onRegister: (T) -> Unit,
    private val onUnregister: (T) -> Unit
) : InstanceBinding {

    override fun register(instance: GameObject) {
        if (listenerClass.isInstance(instance)) {
            onRegister(listenerClass.cast(instance))
        }
    }

    override fun unregister(instance: GameObject) {
        if (listenerClass.isInstance(instance)) {
            onUnregister(listenerClass.cast(instance))
        }
    }
}
