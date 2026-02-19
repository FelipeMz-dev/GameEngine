package com.mc.gameengine.engine.collision

import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.render.Renderer

abstract class Collider(open val owner: Instance) {
    var isEnabled: Boolean = true
        private set

    fun disable() {
        isEnabled = false
    }

    fun enable() {
        isEnabled = true
    }

    fun intersects(other: Collider) = CollisionResolver.test(this, other)

    abstract var state: ColliderState

    abstract fun Renderer.debugDraw()

    fun update(block: (ColliderState) -> ColliderState) {
        val newState = block(state)
        if (newState != state) {
            state = newState
            onUpdate()
        }
    }

    open fun onUpdate() = Unit
}