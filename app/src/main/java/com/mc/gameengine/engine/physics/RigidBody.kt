package com.mc.gameengine.engine.physics

import com.mc.gameengine.engine.collision.CollisionBodyType
import com.mc.gameengine.engine.collision.PhysicsMaterial
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.core.TransformState
import org.dyn4j.dynamics.Body

class RigidBody private constructor(
    body: Body,
    manager: PhysicsManager,
) : RigidBodyBehavior(body, manager) {

    val transformState: TransformState
        get() = transformState()

    val physicState: PhysicState
        get() = physicState()

    val material: PhysicsMaterial?
        get() = material()

    val shape: Shape?
        get() = shape()

    val type: CollisionBodyType
        get() = type()

    companion object {
        internal fun Instance.createBody(
            shape: Shape,
            state: TransformState,
            type: CollisionBodyType = CollisionBodyType.Dynamic,
            material: PhysicsMaterial = PhysicsMaterial(),
        ): RigidBody {
            val body = this.context.physicsManager().createRigidBody(
                owner = this,
                shape = shape,
                state = state,
                type = type,
                material = material
            )
            return RigidBody(
                body = body,
                manager = this.context.physicsManager(),
            )
        }
    }
}