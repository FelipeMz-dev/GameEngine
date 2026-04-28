package com.mc.gameengine.engine.core

import com.mc.gameengine.engine.assets.Sprite
import com.mc.gameengine.engine.audio.AudioPlayer
import com.mc.gameengine.engine.collision.Collider
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.lerp
import com.mc.gameengine.engine.math.plus
import com.mc.gameengine.engine.math.times
import com.mc.gameengine.engine.physics.ForceMode
import com.mc.gameengine.engine.physics.PhysicsConfig
import com.mc.gameengine.engine.physics.PhysicsIntegrationResult
import com.mc.gameengine.engine.physics.PhysicsIntegrator
import com.mc.gameengine.engine.physics.PhysicsSimulationMode
import com.mc.gameengine.engine.physics.PhysicsState
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer

open class Instance {

    private lateinit var context: WorldContext
    private var previous = TransformState()
    protected var current = TransformState()
    protected var physics = PhysicsState()
    protected val audioPlayer: AudioPlayer
        get() = context.audioPlayer

    fun currentState(): TransformState = current

    protected fun physicsState(): PhysicsState = physics

    internal fun addCollider(collider: Collider) {
        context.addCollider(collider)
    }

    internal fun removeCollider(collider: Collider) {
        context.removeCollider(collider)
    }

    internal fun onAddedToScene(context: WorldContext) {
        this.context = context
        onEnterScene()
    }

    internal fun onRemovedFromScene() {
        onExitScene()
        context.clearInstanceColliders(this)
    }

    internal fun snapshot() {
        previous = current.copy()
    }

    protected fun rotateCamera(angle: Float, from: Vec2 = Vec2.Zero) {
        context.rotateCamera(angle, from)
    }

    protected fun translateCamera(to: Vec2) {
        context.translateCamera(to)
    }

    protected fun zoomCamera(value: Float, from: Vec2 = Vec2.Zero) {
        context.zoomCamera(value, from)
    }

    fun screenToWorld(screenPos: Vec2): Vec2 {
        return context.screenToWorld(screenPos)
    }

    fun worldToScreen(worldPos: Vec2): Vec2 {
        return context.worldToScreen(worldPos)
    }

    internal fun Renderer.render(alpha: Float) {
        val renderState = previous.lerp(current, alpha)
        onRender(renderState)
    }

    internal fun computePosition(dt: Float) {
        stepPhysics(dt)
    }

    protected open fun onEnterScene() {}

    protected open fun onExitScene() {}

    protected open fun Renderer.onRender(state: TransformState) {}

    open fun fixedUpdate(dt: Float) {}

    open fun update(dt: Float) {}

    /** Gravedad custom por instancia. Devuelve null para usar la gravedad global del mundo. */
    protected open fun gravityOverride(): Vec2? = null

    protected open fun onBeforePhysicsStep(dt: Float) = Unit

    protected open fun onAfterPhysicsStep(dt: Float) = Unit

    /** Integración física base reutilizable por todas las entidades. */
    protected fun stepPhysics(dt: Float) {
        onBeforePhysicsStep(dt)

        val integration = PhysicsIntegrator.integrate(
            state = physics,
            dt = dt,
            world = context.physicsWorld(),
            gravityOverride = gravityOverride()
        )

        applyPhysicsIntegration(integration)
        onAfterPhysicsStep(dt)
    }

    protected open fun applyPhysicsIntegration(integration: PhysicsIntegrationResult) {
        current = current.copy(position = current.position + integration.displacement)
        physics = integration.nextState
    }

    protected fun configurePhysics(block: (PhysicsState) -> PhysicsState) {
        physics = block(physics)
    }

    protected fun configurePhysicsConfig(block: (PhysicsConfig) -> PhysicsConfig) {
        physics = physics.copy(config = block(physics.config))
    }

    protected fun setPhysicsMode(mode: PhysicsSimulationMode) {
        configurePhysicsConfig { it.copy(mode = mode) }
    }

    protected fun applyForce(force: Vec2, mode: ForceMode = ForceMode.Force) {
        physics = when (mode) {
            ForceMode.Force -> physics.copy(accumulatedForce = physics.accumulatedForce + force)
            ForceMode.Acceleration -> physics.copy(externalAcceleration = physics.externalAcceleration + force)
            ForceMode.Impulse -> physics.copy(velocity = physics.velocity + force * physics.inverseMass)
            ForceMode.VelocityChange -> physics.copy(velocity = physics.velocity + force)
        }
    }

    protected fun stopPhysicsMotion() {
        physics = physics.copy(
            velocity = Vec2.Zero,
            acceleration = Vec2.Zero,
            accumulatedForce = Vec2.Zero,
            externalAcceleration = Vec2.Zero
        )
    }

    protected fun updatePosition(block: (Vec2) -> Vec2) {
        current = current.copy(position = block(current.position))
    }

    protected fun updateVelocity(block: (Vec2) -> Vec2) {
        physics = physics.copy(velocity = block(physics.velocity))
    }

    protected fun updateAngle(block: (Float) -> Float) {
        current = current.copy(angle = block(current.angle))
    }

    protected fun updateScale(block: (Vec2) -> Vec2) {
        current = current.copy(scale = block(current.scale))
    }

    protected fun updatePivot(block: (Pivot) -> Pivot) {
        current = current.copy(pivot = block(current.pivot))
    }

    protected fun viewportSize(): Vec2 = context.viewportSize()

    protected fun viewportScale(): Vec2 = context.viewportScale()

    protected fun fromViewport(position: Vec2): Vec2 = context.calculateFromViewport(position)

    protected fun spriteSize(id: SpriteId): Vec2 = context.spriteSize(id)

    protected fun Sprite.size(): Vec2 = context.spriteSize(this.spriteId)

    protected fun deleteInstance(instance: Instance) = context.deleteInstance(instance)

    protected fun addInstance(instance: Instance) = context.addInstance(instance)
}
