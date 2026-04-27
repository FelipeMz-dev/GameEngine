package com.mc.gameengine.engine.core

internal class SceneFixedStepDispatcher {

    private val actionsByType = linkedMapOf<Class<*>, (Float) -> Unit>()

    fun register(key: Class<*>, action: (Float) -> Unit) {
        actionsByType[key] = action
    }

    fun dispatch(dt: Float) {
        actionsByType.values.forEach { action -> action(dt) }
    }
}
