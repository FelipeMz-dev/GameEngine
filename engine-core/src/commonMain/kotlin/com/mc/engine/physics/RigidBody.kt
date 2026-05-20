package com.mc.engine.physics

import com.mc.engine.core.Instance
import com.mc.engine.core.TransformState
import org.dyn4j.dynamics.Body
import java.util.concurrent.atomic.AtomicInteger

class RigidBody internal constructor(
    val owner: Instance,
    internal val body: Body,
    manager: PhysicsManager,
    layer: Int,
    mask: Int,
) : RigidBodyBehavior by BodyBehaviorImpl(body, manager) {

    companion object {
        private val idCounter = AtomicInteger(0)
    }

    val id: Int = idCounter.incrementAndGet()

    var layer: Int = layer
        private set

    var mask: Int = mask
        private set

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
