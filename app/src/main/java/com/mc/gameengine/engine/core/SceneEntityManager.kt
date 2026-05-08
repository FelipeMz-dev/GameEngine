package com.mc.gameengine.engine.core

internal class SceneEntityManager {

    private val activeInstances = mutableListOf<GameObject>()
    private val pendingAdditions = mutableListOf<GameObject>()
    private val pendingRemovals = mutableListOf<GameObject>()

    fun enqueueAdd(instance: GameObject) {
        pendingAdditions += instance
    }

    fun enqueueRemove(instance: GameObject) {
        pendingRemovals += instance
    }

    fun enqueueRemoveWhere(predicate: (GameObject) -> Boolean) {
        pendingRemovals += activeInstances.filter(predicate)
    }

    fun sync(
        onRemoved: (GameObject) -> Unit,
        onAdded: (GameObject) -> Unit
    ) {
        if (pendingRemovals.isNotEmpty()) {
            pendingRemovals.forEach { instance ->
                activeInstances -= instance
                onRemoved(instance)
            }
            pendingRemovals.clear()
        }

        if (pendingAdditions.isNotEmpty()) {
            pendingAdditions.forEach { instance ->
                activeInstances += instance
                onAdded(instance)
            }
            pendingAdditions.clear()
        }
    }

    inline fun forEach(action: (GameObject) -> Unit) {
        activeInstances.forEach(action)
    }
}
