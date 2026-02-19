package com.mc.gameengine.engine.core

import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.plus
import com.mc.gameengine.engine.math.times
import com.mc.gameengine.engine.assets.SpriteDefinition
import com.mc.gameengine.engine.collision.Collider
import com.mc.gameengine.engine.math.lerp
import com.mc.gameengine.engine.physics.PhysicsState
import com.mc.gameengine.engine.render.Renderer

open class Instance {

    private lateinit var context: WorldContext
    private var previous = TransformState()
    protected var current = TransformState()
    protected var physics = PhysicsState()

    private val colliders = mutableListOf<Collider>()

    fun currentState(): TransformState = current

    internal fun allColliders(): List<Collider> = colliders

    internal fun addCollider(collider: Collider) {
        colliders += collider
    }

    internal fun removeCollider(collider: Collider) {
        colliders -= collider
    }

    internal fun onAddedToScene(context: WorldContext) {
        this.context = context
        onEnterScene()
    }

    internal fun onRemovedFromScene() {
        onExitScene()
    }

    internal fun snapshot() {
        previous = current.copy()
    }

    internal fun Renderer.render(alpha: Float) {
        val renderState = previous.lerp(current, alpha)
        onRender(renderState)
    }

    internal fun computePosition(dt: Float) {
        current = current.copy(position = current.position + physics.velocity * dt)
    }

    protected open fun onEnterScene() {}

    protected open fun onExitScene() {}

    protected open fun Renderer.onRender(state: TransformState) {}

    open fun fixedUpdate(dt: Float) {}

    open fun update(dt: Float) {}

    protected fun updatePosition(block: (Vec2) -> Vec2) {
        current = current.copy(position = block(current.position))
    }

    protected fun updateVelocity(block: (Vec2) -> Vec2) {
        physics = physics.copy(velocity = block(physics.velocity))
    }

    protected fun viewportSize(): Vec2 = context.viewportSize()

    protected fun viewportScale(): Vec2 = context.viewportScale()

    protected fun spriteSize(id: SpriteId): Vec2 = context.spriteSize(id)

    protected fun SpriteDefinition.size(): Vec2 = context.spriteSize(this.spriteId)

    protected fun deleteInstance(instance: Instance) = context.deleteInstance(instance)

    protected fun addInstance(instance: Instance) = context.addInstance(instance)
}