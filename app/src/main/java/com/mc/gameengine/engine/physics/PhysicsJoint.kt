package com.mc.gameengine.engine.physics

import com.mc.gameengine.engine.math.Vec2
import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.joint.Joint

class PhysicsJoint internal constructor(
    internal val joint: Joint<Body>,
    internal val bodyA: Body,
    internal val bodyB: Body,
    private val manager: PhysicsManager,
) {

    val anchorA: Vec2
        get() = manager.toEngine(joint.anchor1)

    val anchorB: Vec2
        get() = manager.toEngine(joint.anchor2)

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
