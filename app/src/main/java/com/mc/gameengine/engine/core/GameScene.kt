package com.mc.gameengine.engine.core

import androidx.compose.ui.layout.ScaleFactor
import com.mc.gameengine.engine.assets.AssetsManager
import com.mc.gameengine.engine.collision.CollisionSystem
import com.mc.gameengine.engine.compose.RenderDepth
import com.mc.gameengine.engine.input.GameInput
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.render.Renderer
import com.mc.gameengine.engine.render.VirtualResolution

abstract class GameScene() : WorldContext {

    private val instances = mutableListOf<Instance>()
    private val toAdd = mutableListOf<Instance>()
    private val toRemove = mutableListOf<Instance>()

    private lateinit var assets: AssetsManager
    private lateinit var gameInput: GameInput

    private var viewportSize = Vec2.Zero
    private var viewportScale = Vec2.Zero

    private val collisionSystem = CollisionSystem()

    fun attachAssets(assets: AssetsManager) {
        this.assets = assets
    }

    fun attachInput(gameInput: GameInput) {
        this.gameInput = gameInput
    }

    override fun spriteSize(id: SpriteId) = assets.getSize(id)

    override fun viewportSize() = viewportSize

    override fun viewportScale() = viewportScale

    fun updateViewportSize(resolution: VirtualResolution) {
        viewportSize = Vec2(resolution.width, resolution.height)
    }

    fun updateViewportScale(scale: ScaleFactor) {
        viewportScale = Vec2(scale.scaleX, scale.scaleY)
    }

    override fun addInstance(instance: Instance) {
        toAdd += instance
    }

    override fun deleteInstance(instance: Instance) {
        toRemove += instance
    }

    private fun syncInstances() {
        if (toRemove.isNotEmpty()) {
            toRemove.forEach { instance ->
                instances -= instance
                instance.onRemovedFromScene()
                gameInput.unregister(instance)
            }
            toRemove.clear()
        }

        if (toAdd.isNotEmpty()) {
            toAdd.forEach { instance ->
                instances += instance
                instance.onAddedToScene(this)
                gameInput.register(instance)
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
        gameInput.keyboardProcessor?.update(dt)
        collisionSystem.check(instances)
    }

    open fun render(
        renderer: Renderer,
        alpha: Float
    ) {
        instances.forEach {
            it.apply { renderer.render(alpha) }
            renderer.debug(it)
        }
        renderer.flush()
    }

    private fun Renderer.debug(instance: Instance) {
        drawAxis(
            position = instance.currentState().position,
            deep = RenderDepth.DEBUG
        )

        instance.allColliders().forEach {
            it.apply { debugDraw() }
        }
    }
}