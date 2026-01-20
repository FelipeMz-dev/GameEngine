package com.mc.gameengine.engine.core

import com.mc.gameengine.core.math.Vec2
import com.mc.gameengine.engine.assets.AssetsManager
import com.mc.gameengine.engine.collision.CollisionSystem
import com.mc.gameengine.engine.input.InputListener
import com.mc.gameengine.engine.input.InputManager
import com.mc.gameengine.engine.render.Renderer
import com.mc.gameengine.engine.render.VirtualResolution

abstract class GameScene(
    private val input: InputManager,
    private val assets: AssetsManager
): WorldContext {

    private val instances = mutableListOf<Instance>()

    private val toAdd = mutableListOf<Instance>()
    private val toRemove = mutableListOf<Instance>()

    private var viewportSize = Vec2(0f, 0f)

    private val collisionSystem = CollisionSystem()

    override fun spriteSize(id: SpriteId) = assets.getSize(id)

    override fun viewportSize() = viewportSize

    fun updateViewportSize(resolution: VirtualResolution) {
        viewportSize = Vec2(resolution.width, resolution.height)
    }

    override fun addInstance(instance: Instance) {
        toAdd += instance
        instance.onAddedToScene(this)
        if (instance is InputListener) {
            input.register(instance)
        }
    }

    override fun deleteInstance(instance: Instance) {
        toRemove += instance
        instance.onRemovedFromScene()
        if (instance is InputListener) {
            input.unregister(instance)
        }
    }

    private fun syncInstances() {
        if (toRemove.isNotEmpty()) {
            toRemove.forEach { instance ->
                instances -= instance
                instance.onRemovedFromScene()
                if (instance is InputListener) {
                    input.unregister(instance)
                }
            }
            toRemove.clear()
        }

        if (toAdd.isNotEmpty()) {
            toAdd.forEach { instance ->
                instances += instance
                instance.onAddedToScene(this)
                if (instance is InputListener) {
                    input.register(instance)
                }
            }
            toAdd.clear()
        }
    }

    open fun update(dt: Float) {
        syncInstances()
        instances.forEach { it.update(dt) }
    }

    open fun fixedUpdate(dt: Float) {
        syncInstances()
        instances.forEach { it.snapshot() }
        instances.forEach { it.fixedUpdate(dt) }
        collisionSystem.check(instances)
    }

    open fun render(
        renderer: Renderer,
        alpha: Float
    ) {
        instances.forEach { it.render(renderer, alpha) }
    }
}