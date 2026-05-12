package com.mc.gameengine.engine.physics

import com.mc.gameengine.engine.collision.CollisionBodyType
import com.mc.gameengine.engine.collision.PhysicsMaterial
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.math.Vec2
import org.dyn4j.collision.CategoryFilter
import org.dyn4j.dynamics.Body
import java.util.concurrent.atomic.AtomicInteger

class RigidBody internal constructor(
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

    val material: PhysicsMaterial?
        get() = material()

    val shape: Shape?
        get() = shape()

    val type: CollisionBodyType
        get() = type()

    val isSensor: Boolean
        get() = manager.isSensor(body)

    fun setAsSensor(value: Boolean = true) {
        manager.updateBodySensor(body, value)
    }

    fun setCollisionFilter(layer: Int = this.layer, mask: Int = this.mask) {
        this.layer = layer
        this.mask = mask
        body.fixtures.forEach { fixture ->
            fixture.filter = CategoryFilter(layer.toLong(), mask.toLong())
        }
    }

    internal fun canNotify(otherLayer: Int, otherMask: Int): Boolean {
        return (mask and otherLayer) != 0 && (otherMask and layer) != 0
    }

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

    companion object {
        private val idCounter = AtomicInteger(0)
    }
}
