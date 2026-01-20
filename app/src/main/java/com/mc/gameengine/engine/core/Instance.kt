package com.mc.gameengine.engine.core

import com.mc.gameengine.core.math.Vec2
import com.mc.gameengine.engine.assets.Sprite
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

    internal fun collider(id: ColliderId): Collider? = colliders.find { it.id == id }

    internal fun addCollider(collider: Collider) {
        colliders += collider
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

    internal fun render(renderer: Renderer, alpha: Float) {
        val renderState = previous.lerp(current, alpha)
        renderer.onRender(renderState)
    }

    protected open fun onEnterScene() {}

    protected open fun onExitScene() {}

    protected open fun Renderer.onRender(state: TransformState) {}

    open fun fixedUpdate(dt: Float) {}

    open fun update(dt: Float) {}

    protected fun viewportSize(): Vec2 = context.viewportSize()

    protected fun spriteSize(id: SpriteId): Vec2 = context.spriteSize(id)

    protected fun Sprite.size(): Vec2 = context.spriteSize(this.spriteId)

    protected fun deleteInstance(instance: Instance) = context.deleteInstance(instance)

    protected fun addInstance(instance: Instance) = context.addInstance(instance)
}