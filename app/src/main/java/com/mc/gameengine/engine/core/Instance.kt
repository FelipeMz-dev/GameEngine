package com.mc.gameengine.engine.core

import com.mc.gameengine.engine.assets.Sprite
import com.mc.gameengine.engine.collision.Collider
import com.mc.gameengine.engine.collision.CollisionBodyType
import com.mc.gameengine.engine.collision.PhysicsMaterial
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.lerp
import com.mc.gameengine.engine.physics.Shape
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer
import org.jbox2d.dynamics.Body

open class Instance {

    private lateinit var context: WorldContext
    private var previous = TransformState()
    protected var current = TransformState()
    private var body: Body? = null

    fun currentState(): TransformState = current

    internal fun onAddedToScene(context: WorldContext) {
        this.context = context
        onEnterScene()
    }

    internal fun onRemovedFromScene() {
        onExitScene()
        context.clearInstanceColliders(this)
        body?.let { context.physicsWorld().destroyBody(it) }
    }

    internal fun onUpdate(dt: Float) {
        update(dt)
    }

    internal fun onFixedUpdate(dt: Float) {
        previous = current.copy()
        fixedUpdate(dt)
    }

    internal fun Renderer.render(alpha: Float) {
        val renderState = previous.lerp(current, alpha)
        onRender(renderState)
    }

    protected open fun update(dt: Float) = Unit

    protected open fun fixedUpdate(dt: Float) = Unit

    protected open fun onEnterScene() = Unit

    protected open fun onExitScene() = Unit

    protected open fun Renderer.onRender(state: TransformState) = Unit

    protected fun camera2D() = context.camera2D()

    protected fun audioPlayer() = context.audioPlayer()

    protected fun viewportSize() = context.viewportSize()

    protected fun viewportScale() = context.viewportScale()

    protected fun fromViewport(position: Vec2) = context.calculateFromViewport(position)

    protected fun spriteSize(id: SpriteId) = context.spriteSize(id)

    protected fun addInstance(instance: Instance) = context.addInstance(instance)

    protected fun deleteInstance(instance: Instance) = context.deleteInstance(instance)

    protected fun addCollider(collider: Collider) = context.addCollider(collider)

    protected fun removeCollider(collider: Collider) = context.removeCollider(collider)

    protected fun screenToWorld(screenPos: Vec2) = context.screenToWorld(screenPos)

    protected fun worldToScreen(worldPos: Vec2) = context.worldToScreen(worldPos)

    protected fun Sprite.size() = context.spriteSize(this.spriteId)

    protected fun updatePosition(block: (Vec2) -> Vec2) {
        current = current.copy(position = block(current.position))
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

    protected fun bodyCollider(): Collider? {
        return (body?.userData as? Collider)?.apply {
            update { context.physicsWorld().getTransformState(body!!) }
        }
    }

    protected fun createRigidBody(
        shape: Shape,
        state: TransformState,
        type: CollisionBodyType = CollisionBodyType.Dynamic,
        material: PhysicsMaterial = PhysicsMaterial()
    ) {
        body = context.physicsWorld().createRigidBody(shape, state, type, material)
    }

    protected fun applyImpulseTowards(
        target: Vec2,
        force: Float = 40f,
        point: Vec2? = null
    ) {
        body?.let {
            context.physicsWorld().applyImpulseTowards(
                it,
                target,
                force,
                point
            )
        }
    }
}