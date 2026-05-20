package com.mc.engine.core

import com.mc.engine.assets.Sprite
import com.mc.engine.math.Vec2
import com.mc.engine.math.lerp
import com.mc.engine.physics.CollisionBodyType
import com.mc.engine.physics.CollisionLayers
import com.mc.engine.physics.PhysicState
import com.mc.engine.physics.PhysicsJoint
import com.mc.engine.physics.PhysicsJointConfig
import com.mc.engine.physics.PhysicsMaterial
import com.mc.engine.physics.RigidBody
import com.mc.engine.physics.RigidBodyConfig
import com.mc.engine.physics.SensorCollider
import com.mc.engine.physics.SensorColliderConfig
import com.mc.engine.physics.Shape
import com.mc.engine.render.Pivot
import com.mc.engine.render.Renderer
import com.mc.engine.render.RendererInterface

open class Instance {

    internal lateinit var context: WorldContext
    private var previous = TransformState()
    protected var current = TransformState()

    fun currentState(): TransformState = current

    fun onAddedToScene(context: WorldContext) {
        this.context = context
        onEnterScene()
    }

    fun onRemovedFromScene() {
        onExitScene()
        context.physicsManager().verifyOwnerRemoved(this)
    }

    fun onUpdate(dt: Float) {
        update(dt)
    }

    fun onFixedUpdate(dt: Float) {
        previous = current.copy()
        fixedUpdate(dt)
    }

    fun Renderer.render(alpha: Float) {
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
        layer: Int = CollisionLayers.Default,
        mask: Int = CollisionLayers.All,
    ): RigidBody {
        return createRigidBody(
            RigidBodyConfig(
                shape = shape,
                state = state,
                type = type,
                material = material,
                physicState = physicState,
                layer = layer,
                mask = mask,
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
        layer: Int = CollisionLayers.Default,
        mask: Int = CollisionLayers.All,
        physicState: PhysicState = PhysicState(),
    ): SensorCollider {
        return createSensorCollider(
            SensorColliderConfig(
                shape = shape,
                state = state,
                layer = layer,
                mask = mask,
                physicState = physicState,
            )
        )
    }

    protected fun createJoint(config: PhysicsJointConfig): PhysicsJoint {
        return context.physicsManager().createJoint(config)
    }

    protected fun createDistanceJoint(
        bodyA: RigidBody,
        bodyB: RigidBody,
        anchorA: Vec2 = bodyA.transformState.position,
        anchorB: Vec2 = bodyB.transformState.position,
        collisionAllowed: Boolean = false,
    ): PhysicsJoint {
        return createJoint(
            PhysicsJointConfig.Distance(
                bodyA = bodyA,
                bodyB = bodyB,
                anchorA = anchorA,
                anchorB = anchorB,
                collisionAllowed = collisionAllowed,
            )
        )
    }

    protected fun createRevoluteJoint(
        bodyA: RigidBody,
        bodyB: RigidBody,
        anchor: Vec2 = bodyA.transformState.position,
        collisionAllowed: Boolean = false,
    ): PhysicsJoint {
        return createJoint(
            PhysicsJointConfig.Revolute(
                bodyA = bodyA,
                bodyB = bodyB,
                anchor = anchor,
                collisionAllowed = collisionAllowed,
            )
        )
    }

    protected fun createWeldJoint(
        bodyA: RigidBody,
        bodyB: RigidBody,
        anchor: Vec2 = bodyA.transformState.position,
        collisionAllowed: Boolean = false,
    ): PhysicsJoint {
        return createJoint(
            PhysicsJointConfig.Weld(
                bodyA = bodyA,
                bodyB = bodyB,
                anchor = anchor,
                collisionAllowed = collisionAllowed,
            )
        )
    }
}
