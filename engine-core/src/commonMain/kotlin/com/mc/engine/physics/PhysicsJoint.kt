package com.mc.engine.physics

import com.mc.engine.math.Vec2
import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.joint.Joint

class PhysicsJoint internal constructor(
    internal val joint: Joint<Body>,
    internal val bodyA: Body,
    internal val bodyB: Body,
    private val manager: PhysicsManager,
) {

    val anchorA: Vec2
        get() = manager.toEngine(bodyA.transform.translation)

    val anchorB: Vec2
        get() = manager.toEngine(bodyB.transform.translation)

    val collisionAllowed: Boolean
        get() = joint.isCollisionAllowed

    fun setCollisionAllowed(value: Boolean) {
        joint.isCollisionAllowed = value
    }

    fun remove() {
        manager.removeJoint(this)
    }

    internal fun includes(body: Body): Boolean = bodyA == body || bodyB == body
}
