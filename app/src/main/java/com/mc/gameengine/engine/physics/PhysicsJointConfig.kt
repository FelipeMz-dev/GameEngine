package com.mc.gameengine.engine.physics

import com.mc.gameengine.engine.math.Vec2

sealed class PhysicsJointConfig {
    abstract val bodyA: RigidBody
    abstract val bodyB: RigidBody
    abstract val collisionAllowed: Boolean

    data class Distance(
        override val bodyA: RigidBody,
        override val bodyB: RigidBody,
        val anchorA: Vec2 = bodyA.transformState.position,
        val anchorB: Vec2 = bodyB.transformState.position,
        override val collisionAllowed: Boolean = false,
    ) : PhysicsJointConfig()

    data class Revolute(
        override val bodyA: RigidBody,
        override val bodyB: RigidBody,
        val anchor: Vec2 = bodyA.transformState.position,
        override val collisionAllowed: Boolean = false,
    ) : PhysicsJointConfig()

    data class Weld(
        override val bodyA: RigidBody,
        override val bodyB: RigidBody,
        val anchor: Vec2 = bodyA.transformState.position,
        override val collisionAllowed: Boolean = false,
    ) : PhysicsJointConfig()
}
