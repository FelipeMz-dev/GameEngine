package com.mc.gameengine.engine.physics

import com.mc.gameengine.engine.collision.CollisionBodyType
import com.mc.gameengine.engine.collision.PhysicsMaterial
import com.mc.gameengine.engine.core.TransformState
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

}