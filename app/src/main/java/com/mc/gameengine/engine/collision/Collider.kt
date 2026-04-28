package com.mc.gameengine.engine.collision

import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.math.AABB
import com.mc.gameengine.engine.render.Renderer
import java.util.concurrent.atomic.AtomicInteger

abstract class Collider(open val owner: Instance) {

    val id: Int = idCounter.incrementAndGet()

    var aabb = AABB(0f, 0f, 0f, 0f)
        private set

    var isEnabled: Boolean = true
        private set

    var isTrigger: Boolean = true
        private set

    var layer: Int = CollisionLayers.Default
        private set

    var mask: Int = CollisionLayers.All
        private set

    var bodyType: CollisionBodyType = CollisionBodyType.Static
        private set

    var physicsMaterial: PhysicsMaterial = PhysicsMaterial()
        private set

    fun disable() {
        isEnabled = false
    }

    fun enable() {
        isEnabled = true
    }

    fun setAsTrigger(value: Boolean) {
        isTrigger = value
    }

    fun setCollisionFilter(layer: Int = this.layer, mask: Int = this.mask) {
        this.layer = layer
        this.mask = mask
    }

    fun setBodyType(type: CollisionBodyType) {
        bodyType = type
    }

    fun setPhysicsMaterial(material: PhysicsMaterial) {
        physicsMaterial = material
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

    private companion object {
        val idCounter = AtomicInteger(0)
    }
}
