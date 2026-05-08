package com.mc.gameengine.engine.core

import com.mc.gameengine.engine.assets.SpriteManager
import com.mc.gameengine.engine.audio.AudioManager
import com.mc.gameengine.engine.collision.Collider
import com.mc.gameengine.engine.collision.CollisionSystem
import com.mc.gameengine.engine.collision.MaskCollider
import com.mc.gameengine.engine.compose.Camera2D
import com.mc.gameengine.engine.compose.RenderDepth
import com.mc.gameengine.engine.input.GameInput
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.div
import com.mc.gameengine.engine.math.minus
import com.mc.gameengine.engine.math.plus
import com.mc.gameengine.engine.math.rotate
import com.mc.gameengine.engine.math.rotateAround
import com.mc.gameengine.engine.math.times
import com.mc.gameengine.engine.physics.PhysicsManager
import com.mc.gameengine.engine.render.Renderer

abstract class GameScene() : WorldContext {

    private val entities = SceneEntityManager()
    private val collisionSystem = CollisionSystem()
    private val lifecycleDispatcher = SceneLifecycleDispatcher()
    private val fixedStepDispatcher = SceneFixedStepDispatcher()

    init {
        fixedStepDispatcher.register(CollisionSystem::class.java) {
            collisionSystem.check()
        }
    }

    private lateinit var spriteManager: SpriteManager
    private lateinit var audioManager: AudioManager
    private lateinit var gameInput: GameInput

    internal var camera2D: Camera2D = Camera2D()
    private var physicsManager = PhysicsManager()

    private lateinit var viewport: Viewport

    override fun viewport() = viewport
    override fun physicsManager() = physicsManager
    override fun audioPlayer() = audioManager
    override fun camera2D() = camera2D

    override fun spriteSize(id: SpriteId) = spriteManager.getSize(id)

    override fun addInstance(instance: Instance) {
        entities.enqueueAdd(instance)
    }

    override fun deleteInstance(instance: Instance) {
        entities.enqueueRemove(instance)
    }

    override fun addCollider(collider: Collider) {
        if (collider is MaskCollider) loadSourceMask(collider)
        collisionSystem.addCollider(collider)
    }

    override fun removeCollider(collider: Collider) {
        collisionSystem.removeCollider(collider)
    }

    override fun clearInstanceColliders(instance: Instance) {
        collisionSystem.clearInstanceColliders(instance)
    }

    private fun loadSourceMask(collider: MaskCollider) {
        collider.apply { spriteManager.loadSource() }
    }

    fun attach(dependencies: SceneDependencies) {
        attachSpriteManager(dependencies.spriteManager)
        attachAudioManager(dependencies.audioManager)
        attachInput(dependencies.gameInput)
    }

    fun attachSpriteManager(spriteManager: SpriteManager) {
        this.spriteManager = spriteManager
    }

    fun attachAudioManager(audioManager: AudioManager) {
        this.audioManager = audioManager
        lifecycleDispatcher.register(
            key = AudioManager::class.java,
            onAdded = audioManager::registerListener,
            onRemoved = audioManager::unregisterListener
        )
    }

    fun attachInput(gameInput: GameInput) {
        this.gameInput = gameInput
        lifecycleDispatcher.register(
            key = GameInput::class.java,
            onAdded = gameInput::register,
            onRemoved = gameInput::unregister
        )
        fixedStepDispatcher.register(GameInput::class.java) { dt ->
            gameInput.keyboardProcessor?.update(dt)
        }
    }

    fun updateViewport(viewport: Viewport) {
        this.viewport = viewport
        camera2D.viewportSize = viewport.size
    }

    override fun calculateFromViewport(position: Vec2): Vec2 {
        return position / viewport.scale
    }

    override fun screenToWorld(position: Vec2): Vec2 {
        val scaled = (position - camera2D.zoom.from) / camera2D.zoom.value + camera2D.zoom.from
        val rotated = (scaled - camera2D.rotation.point)
            .rotate(-camera2D.rotation.angle) + camera2D.rotation.point
        return rotated - camera2D.position
    }

    override fun worldToScreen(position: Vec2): Vec2 {
        val rotated = (position + camera2D.position)
            .rotateAround(camera2D.rotation.point, camera2D.rotation.angle)
        val scaled = (rotated - camera2D.zoom.from) * camera2D.zoom.value + camera2D.zoom.from
        return scaled
    }

    open fun update(dt: Float) {
        syncInstances()
        entities.forEach { it.onUpdate(dt) }
    }

    open fun fixedUpdate(dt: Float) {
        syncInstances()
        physicsManager.update(dt)
        entities.forEach { it.onFixedUpdate(dt) }
        fixedStepDispatcher.dispatch(dt)
    }

    open fun render(
        renderer: Renderer,
        alpha: Float
    ) {
        entities.forEach {
            it.apply { renderer.render(alpha) }
            //renderer.debug(it)
        }
        renderer.flush()
    }

    private fun syncInstances() {
        entities.sync(
            onRemoved = { instance ->
                instance.onRemovedFromScene()
                lifecycleDispatcher.notifyRemoved(instance)
            },
            onAdded = { instance ->
                instance.onAddedToScene(this)
                lifecycleDispatcher.notifyAdded(instance)
            }
        )
    }

    private fun Renderer.debug(instance: Instance) {
        drawAxis(
            position = instance.currentState().position,
            deep = RenderDepth.DEBUG
        )

        collisionSystem.colliders.forEach {
            it.apply { debugDraw() }
        }
    }
}