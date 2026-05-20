package com.mc.engine.physics

import com.mc.engine.core.TransformState
import com.mc.engine.math.Vec2

interface BodyBehavior {
    fun transformState(): TransformState
    fun physicState(): PhysicState
    fun shape(): Shape?
    fun updateTransform(block: (TransformState) -> TransformState)
    fun updatePhysic(block: (PhysicState) -> PhysicState)
    fun updateShape(block: (Shape?) -> Shape)
    fun stop()
    fun remove()
}

interface RigidBodyBehavior : BodyBehavior {
    fun material(): PhysicsMaterial?
    fun type(): CollisionBodyType
    fun updateMaterial(block: (PhysicsMaterial?) -> PhysicsMaterial)
    fun updateType(block: (CollisionBodyType) -> CollisionBodyType)
    fun applyImpulseTowards(target: Vec2, force: Float = 40f, point: Vec2? = null)
    fun applyImpulse(impulse: Vec2, point: Vec2? = null)
    fun applyForce(force: Vec2, point: Vec2? = null)
    fun applyTorque(torque: Float)
}

interface SensorColliderBehavior : BodyBehavior {
    fun setCollisionFilter(layer: Int, mask: Int)
}