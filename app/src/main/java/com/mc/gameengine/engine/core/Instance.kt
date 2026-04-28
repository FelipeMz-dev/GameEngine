package com.mc.gameengine.engine.core

import com.mc.gameengine.engine.assets.Sprite
import com.mc.gameengine.engine.audio.AudioPlayer
import com.mc.gameengine.engine.collision.Collider
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.clamp
import com.mc.gameengine.engine.math.lerp
import com.mc.gameengine.engine.math.plus
import com.mc.gameengine.engine.math.times
import com.mc.gameengine.engine.physics.ForceMode
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

    /**
     * Paso físico básico listo para evolucionar con el futuro sistema de físicas.
     * Se mantiene como API estable para que gameplay no dependa del solver final.
     */
    internal fun computePosition(dt: Float) {
        stepPhysics(dt)
    }

    protected open fun onEnterScene() {}

    protected open fun onExitScene() {}

    protected open fun Renderer.onRender(state: TransformState) {}

    open fun fixedUpdate(dt: Float) {}

    open fun update(dt: Float) {}

    /** Hook para inyectar gravedad personalizada por instancia. */
    protected open fun gravity(): Vec2 = Vec2.Zero

    /** Hook para modificar fuerzas antes de integrar (viento, campos, etc). */
    protected open fun onBeforePhysicsStep(dt: Float) = Unit

    /** Hook para reaccionar tras integrar (resolver suelo, clamping custom, etc). */
    protected open fun onAfterPhysicsStep(dt: Float) = Unit

    /** Integración física semi-implícita simple, configurable por estado físico. */
    protected fun stepPhysics(dt: Float) {
        if (physics.isKinematic) {
            onBeforePhysicsStep(dt)
            onAfterPhysicsStep(dt)
            physics = physics.copy(accumulatedForce = Vec2.Zero, acceleration = Vec2.Zero)
            return
        }

        onBeforePhysicsStep(dt)

        val gravityForce = gravity() * physics.gravityScale * physics.mass
        val totalForce = physics.accumulatedForce + gravityForce
        val forceAcceleration = if (physics.mass <= 0f) Vec2.Zero else totalForce * physics.inverseMass
        val acceleration = forceAcceleration + physics.acceleration
        val nextVelocity = (physics.velocity + acceleration * dt) * (1f - physics.linearDamping * dt)
        val clampedVelocity = nextVelocity.clamp(physics.maxSpeed)

        current = current.copy(position = current.position + clampedVelocity * dt)
        physics = physics.copy(
            velocity = clampedVelocity,
            acceleration = acceleration,
            accumulatedForce = Vec2.Zero
        )

        onAfterPhysicsStep(dt)
    }

    protected fun configurePhysics(block: (PhysicsState) -> PhysicsState) {
        physics = block(physics)
    }

    protected fun applyForce(force: Vec2, mode: ForceMode = ForceMode.Force) {
        physics = when (mode) {
            ForceMode.Force -> physics.copy(accumulatedForce = physics.accumulatedForce + force)
            ForceMode.Acceleration -> physics.copy(accumulatedForce = physics.accumulatedForce + force * physics.mass)
            ForceMode.Impulse -> physics.copy(velocity = physics.velocity + force * physics.inverseMass)
            ForceMode.VelocityChange -> physics.copy(velocity = physics.velocity + force)
        }
    }

    protected fun stopPhysicsMotion() {
        physics = physics.copy(
            velocity = Vec2.Zero,
            acceleration = Vec2.Zero,
            accumulatedForce = Vec2.Zero
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
