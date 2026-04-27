package com.mc.gameengine.engine.collision

import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.math.AABB
import com.mc.gameengine.engine.render.Renderer

abstract class Collider(open val owner: Instance) {

    var aabb = AABB(0f, 0f, 0f, 0f)
        private set

    var isEnabled: Boolean = true
        private set

    fun disable() {
        isEnabled = false
    }

    fun enable() {
        isEnabled = true
    }

    var state: TransformState = TransformState()
        private set

    abstract fun Renderer.debugDraw()

    abstract fun onUpdateAABB(): AABB

    open fun onUpdate() = Unit

    fun update(block: (TransformState) -> TransformState) {
        val newState = block(state)
        if (newState != state) {
            state = newState
            onUpdate()
            syncAABB()
        }
    }

    fun intersects(other: Collider) = CollisionResolver.test(this, other)

    protected fun syncAABB() {
        aabb = onUpdateAABB()
    }
}