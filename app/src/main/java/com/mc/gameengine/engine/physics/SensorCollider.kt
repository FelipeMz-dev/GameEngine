package com.mc.gameengine.engine.physics

import com.mc.gameengine.engine.collision.CollisionBodyType
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.core.TransformState
import org.dyn4j.collision.CategoryFilter
import org.dyn4j.dynamics.Body
import java.util.concurrent.atomic.AtomicInteger

class SensorCollider internal constructor(
    val owner: Instance,
    body: Body,
    manager: PhysicsManager,
    layer: Int,
    mask: Int,
) : RigidBodyBehavior(body, manager) {

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

    val type: CollisionBodyType
        get() = type()

    fun setCollisionFilter(layer: Int = this.layer, mask: Int = this.mask) {
        this.layer = layer
        this.mask = mask
        body.fixtures.forEach { fixture ->
            fixture.filter = CategoryFilter(layer.toLong(), mask.toLong())
        }
    }

    internal fun canNotify(other: SensorCollider): Boolean {
        return (mask and other.layer) != 0 && (other.mask and layer) != 0
    }

    private companion object {
        val idCounter = AtomicInteger(0)
    }
}
