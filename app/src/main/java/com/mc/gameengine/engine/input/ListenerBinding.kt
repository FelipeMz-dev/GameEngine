package com.mc.gameengine.engine.input

import com.mc.gameengine.engine.core.Instance

internal class ListenerBinding<T : Any>(
    private val listenerClass: Class<T>,
    private val onRegister: (T) -> Unit,
    private val onUnregister: (T) -> Unit
) : InstanceBinding {

    override fun register(instance: Instance) {
        if (listenerClass.isInstance(instance)) {
            onRegister(listenerClass.cast(instance))
        }
    }

    override fun unregister(instance: Instance) {
        if (listenerClass.isInstance(instance)) {
            onUnregister(listenerClass.cast(instance))
        }
    }
}
