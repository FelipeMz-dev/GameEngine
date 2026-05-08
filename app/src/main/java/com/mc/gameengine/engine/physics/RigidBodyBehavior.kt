package com.mc.gameengine.engine.physics

import com.mc.gameengine.engine.collision.CollisionBodyType
import com.mc.gameengine.engine.collision.PhysicsMaterial
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.math.Vec2
import org.dyn4j.dynamics.Body

open class RigidBodyBehavior(
    protected val body: Body,
    private val manager: PhysicsManager
) {

    protected fun transformState(): TransformState {
        return manager.getTransformState(body)
    }

    protected fun physicState(): PhysicState {
        return manager.getPhysicState(body)
    }

    protected fun material(): PhysicsMaterial? {
        return manager.getMaterial(body)
    }

    protected fun shape(): Shape? {
        return manager.getShape(body)
    }

    protected fun type(): CollisionBodyType {
        return manager.getType(body)
    }

    fun remove() {
        manager.removeBody(body)
    }

    fun applyImpulseTowards(
        target: Vec2,
        force: Float = 40f,
        point: Vec2? = null
    ) {
        manager.applyImpulseTowards(body, target, force, point)
    }

    fun applyImpulse(
        impulse: Vec2,
        point: Vec2? = null
    ) {
        manager.applyImpulse(body, impulse, point)
    }

    fun applyForce(
        force: Vec2,
        point: Vec2? = null
    ) {
        manager.applyForce(body, force, point)
    }

    fun applyTorque(torque: Float) {
        manager.applyTorque(body, torque)
    }

    fun stop() {
        manager.stopBody(body)
    }

    fun updateTransform(block: (TransformState) -> TransformState) {
        val currentState = transformState()
        val newState = block(currentState)
        if (currentState == newState) return
        manager.updateBodyTransform(body, newState)
    }

    fun updatePhysic(block: (PhysicState) -> PhysicState) {
        val currentState = physicState()
        val newState = block(currentState)
        if (currentState == newState) return
        manager.updateBodyPhysicState(body, newState)
    }

    fun updateMaterial(block: (PhysicsMaterial?) -> PhysicsMaterial) {
        val currentState = material()
        val newState = block(currentState)
        if (currentState == newState) return
        manager.updateBodyMaterial(body, newState)
    }

    fun updateShape(block: (Shape?) -> Shape) {
        val currentShape = shape()
        val newShape = block(currentShape)
        if (currentShape == newShape) return
        manager.updateBodyShape(body, transformState(), newShape)
        material()?.let { manager.updateBodyMaterial(body, it) }
    }

    fun updateType(block: (CollisionBodyType) -> CollisionBodyType) {
        val currentState = type()
        val newState = block(currentState)
        if (currentState == newState) return
        manager.updateBodyType(body, newState)
    }
}