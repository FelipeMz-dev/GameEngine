package com.mc.gameengine.engine.physics

import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.clamp
import com.mc.gameengine.engine.math.plus
import com.mc.gameengine.engine.math.times
import kotlin.math.max

data class PhysicsIntegrationResult(
    val nextState: PhysicsState,
    val displacement: Vec2,
    val angularDisplacement: Float
)

object PhysicsIntegrator {

    fun integrate(
        state: PhysicsState,
        dt: Float,
        world: PhysicsWorld,
        gravityOverride: Vec2? = null
    ): PhysicsIntegrationResult {
        if (state.config.mode != PhysicsSimulationMode.Dynamic) {
            return PhysicsIntegrationResult(
                nextState = state.copy(
                    acceleration = Vec2.Zero,
                    accumulatedForce = Vec2.Zero,
                    angularAcceleration = 0f,
                    accumulatedTorque = 0f
                ),
                displacement = Vec2.Zero,
                angularDisplacement = 0f
            )
        }

        // --- Integración Lineal ---
        val gravity = gravityOverride ?: world.gravity
        val gravityForce = gravity * state.config.gravityScale * state.config.mass
        val totalForce = state.accumulatedForce + gravityForce

        val forceAcceleration = if (state.inverseMass <= 0f) Vec2.Zero else totalForce * state.inverseMass
        val acceleration = forceAcceleration + state.externalAcceleration

        val dampingFactor = max(0f, 1f - state.config.linearDamping * dt)
        val nextVelocity = (state.velocity + acceleration * dt) * dampingFactor

        val speedCap = minOf(state.config.maxSpeed, world.maxLinearSpeed)
        val clampedVelocity = nextVelocity.clamp(speedCap)
        val displacement = clampedVelocity * dt

        // --- Integración Angular ---
        val torqueAcceleration = state.accumulatedTorque * state.inverseMomentOfInertia
        val angularAcceleration = torqueAcceleration + state.externalAngularAcceleration
        
        val angularDampingFactor = max(0f, 1f - state.config.angularDamping * dt)
        var nextAngularVelocity = (state.angularVelocity + angularAcceleration * dt) * angularDampingFactor
        
        val angularSpeedCap = state.config.maxAngularSpeed
        nextAngularVelocity = nextAngularVelocity.coerceIn(-angularSpeedCap, angularSpeedCap)
        val angularDisplacement = nextAngularVelocity * dt

        return PhysicsIntegrationResult(
            nextState = state.copy(
                velocity = clampedVelocity,
                acceleration = acceleration,
                accumulatedForce = Vec2.Zero,
                externalAcceleration = Vec2.Zero,
                
                angularVelocity = nextAngularVelocity,
                angularAcceleration = angularAcceleration,
                accumulatedTorque = 0f,
                externalAngularAcceleration = 0f
            ),
            displacement = displacement,
            angularDisplacement = angularDisplacement
        )
    }
}
