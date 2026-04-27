package com.mc.gameengine.engine.core

import androidx.compose.ui.layout.ScaleFactor
import com.mc.gameengine.engine.assets.SpriteManager
import com.mc.gameengine.engine.audio.AudioManager
import com.mc.gameengine.engine.audio.AudioPlayer
import com.mc.gameengine.engine.collision.Collider
import com.mc.gameengine.engine.collision.CollisionSystem
import com.mc.gameengine.engine.collision.MaskCollider
import com.mc.gameengine.engine.compose.Camera2D
import com.mc.gameengine.engine.compose.RenderDepth
import com.mc.gameengine.engine.compose.Rotation
import com.mc.gameengine.engine.compose.Zoom
import com.mc.gameengine.engine.input.GameInput
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.div
import com.mc.gameengine.engine.math.minus
import com.mc.gameengine.engine.math.plus
import com.mc.gameengine.engine.math.rotate
import com.mc.gameengine.engine.math.rotateAround
import com.mc.gameengine.engine.math.times
import com.mc.gameengine.engine.render.Renderer
import com.mc.gameengine.engine.render.VirtualResolution

abstract class GameScene() : WorldContext {

    private val instances = mutableListOf<Instance>()
    private val toAdd = mutableListOf<Instance>()
    private val toRemove = mutableListOf<Instance>()
    private val collisionSystem = CollisionSystem()

    protected var camera: Camera2D? = null
    private lateinit var spriteManager: SpriteManager
    private lateinit var audioManager: AudioManager
    private lateinit var gameInput: GameInput

    override val audioPlayer: AudioPlayer
        get() = audioManager

    private var viewportSize = Vec2.Zero
    private var viewportScale = Vec2.Zero

    override fun spriteSize(id: SpriteId) = spriteManager.getSize(id)

    override fun viewportSize() = viewportSize

    override fun viewportScale() = viewportScale

    override fun addInstance(instance: Instance) {
        toAdd += instance
    }

    override fun deleteInstance(instance: Instance) {
        toRemove += instance
    }

    protected fun deleteAllInstances(block: (Instance) -> Boolean) {
        toRemove.addAll(instances.filter(block))
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

    fun attachSpriteManager(spriteManager: SpriteManager) {
        this.spriteManager = spriteManager
    }

    fun attachAudioManager(audioManager: AudioManager) {
        this.audioManager = audioManager
    }

    fun attachInput(gameInput: GameInput) {
        this.gameInput = gameInput
    }

    fun attachCamera2D(camera: Camera2D) {
        this.camera = camera
    }

    fun updateViewportSize(resolution: VirtualResolution) {
        viewportSize = Vec2(resolution.width, resolution.height)
    }

    fun updateViewportScale(scale: ScaleFactor) {
        viewportScale = Vec2(scale.scaleX, scale.scaleY)
    }

    override fun calculateFromViewport(position: Vec2): Vec2 {
        return position / viewportScale
    }

    override fun screenToWorld(position: Vec2): Vec2 {
        val cam = camera ?: return position
        val scaled = (position - cam.zoom.from) / cam.zoom.value + cam.zoom.from
        val rotated = (scaled - cam.rotation.from)
            .rotate(-cam.rotation.angle) + cam.rotation.from
        return rotated - cam.position
    }

    override fun worldToScreen(position: Vec2): Vec2 {
        val cam = camera ?: return position
        val rotated = (position + cam.position)
            .rotateAround(cam.rotation.from, cam.rotation.angle)
        val scaled = (rotated - cam.zoom.from) * cam.zoom.value + cam.zoom.from
        return scaled
    }

    override fun rotateCamera(angle: Float, from: Vec2) {
        camera?.apply { rotation = Rotation(angle, from) }
    }

    override fun translateCamera(to: Vec2) {
        camera?.apply { position = to }
    }

    override fun zoomCamera(value: Float, from: Vec2) {
        camera?.apply { zoom = Zoom(value, from) }
    }

    open fun update(dt: Float) {
        syncInstances()
        instances.forEach { it.update(dt) }
    }

    open fun fixedUpdate(dt: Float) {
        syncInstances()
        instances.forEach {
            it.snapshot()
            it.fixedUpdate(dt)
        }
        gameInput.keyboardProcessor?.update(dt)
        collisionSystem.check()
    }

    open fun render(
        renderer: Renderer,
        alpha: Float
    ) {
        instances.forEach {
            it.apply { renderer.render(alpha) }
            //renderer.debug(it)
        }
        renderer.flush()
    }

    private fun syncInstances() {
        if (toRemove.isNotEmpty()) {
            toRemove.forEach { instance ->
                instances -= instance
                instance.onRemovedFromScene()
                gameInput.unregister(instance)
                audioManager.unregisterListener(instance)
            }
            toRemove.clear()
        }

        if (toAdd.isNotEmpty()) {
            toAdd.forEach { instance ->
                instances += instance
                instance.onAddedToScene(this)
                gameInput.register(instance)
                audioManager.registerListener(instance)
            }
            toAdd.clear()
        }
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