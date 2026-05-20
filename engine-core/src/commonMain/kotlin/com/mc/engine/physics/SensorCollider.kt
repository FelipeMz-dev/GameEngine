package com.mc.engine.physics

import com.mc.engine.core.Instance
import com.mc.engine.core.TransformState
import org.dyn4j.dynamics.Body
import java.util.concurrent.atomic.AtomicInteger

open class SensorCollider internal constructor(
    val owner: Instance,
    internal val body: Body,
    manager: PhysicsManager,
    layer: Int,
    mask: Int,
) : SensorColliderBehavior by BodyBehaviorImpl(body, manager) {

    val id: Int = idCounter.incrementAndGet()

    var layer: Int = layer
        private set

    var mask: Int = mask
        private set

    val transformState: TransformState
        get() = transformState()

    val physicState: PhysicState
        get() = physicState()

    val shape: Shape?
        get() = shape()

    internal fun canNotify(other: SensorCollider): Boolean {
        return (mask and other.layer) != 0 && (other.mask and layer) != 0
    }

    private companion object {
        val idCounter = AtomicInteger(0)
    }
}
