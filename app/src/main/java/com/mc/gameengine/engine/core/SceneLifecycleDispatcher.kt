package com.mc.gameengine.engine.core

internal class SceneLifecycleDispatcher {

    private data class Handler(
        val onAdded: (GameObject) -> Unit,
        val onRemoved: (GameObject) -> Unit
    )

    private val handlersByType = linkedMapOf<Class<*>, Handler>()

    fun register(
        key: Class<*>,
        onAdded: (GameObject) -> Unit,
        onRemoved: (GameObject) -> Unit
    ) {
        handlersByType[key] = Handler(onAdded, onRemoved)
    }

    fun notifyAdded(instance: GameObject) {
        handlersByType.values.forEach { it.onAdded(instance) }
    }

    fun notifyRemoved(instance: GameObject) {
        handlersByType.values.forEach { it.onRemoved(instance) }
    }
}
