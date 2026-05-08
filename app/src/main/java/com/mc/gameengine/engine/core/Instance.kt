package com.mc.gameengine.engine.core

import com.mc.gameengine.engine.assets.Sprite
import com.mc.gameengine.engine.collision.Collider
import com.mc.gameengine.engine.collision.CollisionBodyType
import com.mc.gameengine.engine.collision.CollisionLayers
import com.mc.gameengine.engine.collision.PhysicsMaterial
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.lerp
import com.mc.gameengine.engine.physics.PhysicState
import com.mc.gameengine.engine.physics.RigidBody
import com.mc.gameengine.engine.physics.RigidBodyConfig
import com.mc.gameengine.engine.physics.SensorCollider
import com.mc.gameengine.engine.physics.SensorColliderConfig
import com.mc.gameengine.engine.physics.Shape
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer

open class Instance {

    internal lateinit var context: WorldContext
    private var previous = TransformState()
    protected var current = TransformState()

    fun currentState(): TransformState = current

    internal fun onAddedToScene(context: WorldContext) {
        this.context = context
        onEnterScene()
    }

    internal fun onRemovedFromScene() {
        onExitScene()
        context.physicsManager().verifyOwnerRemoved(this)
        context.clearInstanceColliders(this)
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

    protected fun viewport() = context.viewport()

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

    protected fun createRigidBody(config: RigidBodyConfig): RigidBody {
        return context.physicsManager().createRigidBody(
            owner = this,
            config = config
        )
    }

    protected fun createRigidBody(
        shape: Shape,
        state: TransformState = current,
        type: CollisionBodyType = CollisionBodyType.Dynamic,
        material: PhysicsMaterial = PhysicsMaterial(),
        physicState: PhysicState = PhysicState(),
    ): RigidBody {
        return createRigidBody(
            RigidBodyConfig(
                shape = shape,
                state = state,
                type = type,
                material = material,
                physicState = physicState,
            )
        )
    }

    protected fun createSensorCollider(config: SensorColliderConfig): SensorCollider {
        return context.physicsManager().createSensorCollider(
            owner = this,
            config = config
        )
    }

    protected fun createSensorCollider(
        shape: Shape,
        state: TransformState = current,
        type: CollisionBodyType = CollisionBodyType.Static,
        layer: Int = CollisionLayers.Default,
        mask: Int = CollisionLayers.All,
        physicState: PhysicState = PhysicState(),
    ): SensorCollider {
        return createSensorCollider(
            SensorColliderConfig(
                shape = shape,
                state = state,
                type = type,
                layer = layer,
                mask = mask,
                physicState = physicState,
            )
        )
    }

    @Deprecated(
        message = "Use createRigidBody(...) directly from Instance instead.",
        replaceWith = ReplaceWith("createRigidBody(shape, state, type, material, physicState)")
    )
    protected fun RigidBody.Companion.create(
        shape: Shape,
        state: TransformState = current,
        type: CollisionBodyType = CollisionBodyType.Dynamic,
        material: PhysicsMaterial = PhysicsMaterial(),
        physicState: PhysicState = PhysicState(),
    ): RigidBody {
        return createRigidBody(
            shape = shape,
            state = state,
            type = type,
            material = material,
            physicState = physicState,
        )
    }
}
