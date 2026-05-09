package com.mc.gameengine.engine.physics

import com.mc.gameengine.engine.collision.CollisionBodyType
import com.mc.gameengine.engine.collision.PhysicsMaterial
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.math.Vec2
import org.dyn4j.dynamics.Body

class RigidBody internal constructor(
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

    fun distanceJointWith(
        other: RigidBody,
        anchorA: Vec2 = transformState.position,
        anchorB: Vec2 = other.transformState.position,
        collisionAllowed: Boolean = false,
    ): PhysicsJoint {
        return manager.createJoint(
            PhysicsJointConfig.Distance(
                bodyA = this,
                bodyB = other,
                anchorA = anchorA,
                anchorB = anchorB,
                collisionAllowed = collisionAllowed,
            )
        )
    }

    fun revoluteJointWith(
        other: RigidBody,
        anchor: Vec2 = transformState.position,
        collisionAllowed: Boolean = false,
    ): PhysicsJoint {
        return manager.createJoint(
            PhysicsJointConfig.Revolute(
                bodyA = this,
                bodyB = other,
                anchor = anchor,
                collisionAllowed = collisionAllowed,
            )
        )
    }

    fun weldJointWith(
        other: RigidBody,
        anchor: Vec2 = transformState.position,
        collisionAllowed: Boolean = false,
    ): PhysicsJoint {
        return manager.createJoint(
            PhysicsJointConfig.Weld(
                bodyA = this,
                bodyB = other,
                anchor = anchor,
                collisionAllowed = collisionAllowed,
            )
        )
    }

    companion object {}
}
