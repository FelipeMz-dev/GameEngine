package com.mc.gameengine.engine.core

internal class SceneEntityManager {

    private val activeInstances = mutableListOf<Instance>()
    private val pendingAdditions = mutableListOf<Instance>()
    private val pendingRemovals = mutableListOf<Instance>()

    fun enqueueAdd(instance: Instance) {
        pendingAdditions += instance
    }

    fun enqueueRemove(instance: Instance) {
        pendingRemovals += instance
    }

    fun enqueueRemoveWhere(predicate: (Instance) -> Boolean) {
        pendingRemovals += activeInstances.filter(predicate)
    }

    fun sync(
        onRemoved: (Instance) -> Unit,
        onAdded: (Instance) -> Unit
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

    inline fun forEach(action: (Instance) -> Unit) {
        activeInstances.forEach(action)
    }
}
