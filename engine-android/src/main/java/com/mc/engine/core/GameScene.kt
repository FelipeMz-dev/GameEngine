package com.mc.engine.core

import com.mc.engine.AudioManager
import com.mc.engine.assets.SpriteManager
import com.mc.engine.audio.AudioSystem
import com.mc.engine.compose.RenderDepth
import com.mc.engine.input.GameInput
import com.mc.engine.math.Vec2
import com.mc.engine.math.div
import com.mc.engine.math.minus
import com.mc.engine.math.plus
import com.mc.engine.math.rotate
import com.mc.engine.math.rotateAround
import com.mc.engine.math.times
import com.mc.engine.physics.PhysicsManager
import com.mc.engine.render.Renderer

abstract class GameScene(
    private val physicsManager: PhysicsManager = PhysicsManager(),
) : WorldContext, GameSceneInterface {

    private val entities = SceneEntityManager()
    private val lifecycleDispatcher = SceneLifecycleDispatcher()
    private val fixedStepDispatcher = SceneFixedStepDispatcher()

    private lateinit var spriteManager: SpriteManager
    private lateinit var audioSystem: AudioSystem
    private lateinit var gameInput: GameInput

    internal var camera2D: Camera2D = Camera2D()
    private lateinit var viewport: Viewport

    override fun viewport() = viewport
    override fun physicsManager() = physicsManager
    override fun audioPlayer() = audioSystem
    override fun camera2D() = camera2D

    override fun spriteSize(id: SpriteId) = spriteManager.getSize(id)

    override fun addInstance(instance: Instance) {
        entities.enqueueAdd(instance)
    }

    override fun deleteInstance(instance: Instance) {
        entities.enqueueRemove(instance)
    }

    fun attach(dependencies: SceneDependencies) {
        attachSpriteManager(dependencies.spriteManager)
        attachAudioManager(dependencies.audioManager)
        attachInput(dependencies.gameInput)
    }

    fun attachSpriteManager(spriteManager: SpriteManager) {
        this.spriteManager = spriteManager
    }

    fun attachAudioManager(audioSystem: AudioSystem) {
        this.audioSystem = audioSystem
        lifecycleDispatcher.register(
            key = AudioManager::class.java,
            onAdded = (this.audioSystem as AudioManager)::registerListener,
            onRemoved = (this.audioSystem as AudioManager)::unregisterListener
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

    override fun update(dt: Float) {
        syncInstances()
        entities.forEach { it.onUpdate(dt) }
    }

    override fun fixedUpdate(dt: Float) {
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
    }
}