package com.mc.engine.core

class SceneLifecycleDispatcher {

    private data class Handler(
        val onAdded: (Instance) -> Unit,
        val onRemoved: (Instance) -> Unit
    )

    private val handlersByType = linkedMapOf<Class<*>, Handler>()

    fun register(
        key: Class<*>,
        onAdded: (Instance) -> Unit,
        onRemoved: (Instance) -> Unit
    ) {
        handlersByType[key] = Handler(onAdded, onRemoved)
    }

    fun notifyAdded(instance: Instance) {
        handlersByType.values.forEach { it.onAdded(instance) }
    }

    fun notifyRemoved(instance: Instance) {
        handlersByType.values.forEach { it.onRemoved(instance) }
    }
}
