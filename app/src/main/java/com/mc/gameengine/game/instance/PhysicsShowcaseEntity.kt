package com.mc.gameengine.game.instance

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.minus
import com.mc.gameengine.engine.math.plus
import com.mc.gameengine.engine.physics.ForceMode
import com.mc.gameengine.engine.physics.PhysicsSimulationMode
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer
import kotlin.math.cos
import kotlin.math.sin

class PhysicsShowcaseEntity(
    private val label: String,
    private val color: Color,
    private val initialPosition: Vec2,
    private val radius: Float,
    private val physicsMode: PhysicsSimulationMode,
    private val gravityScale: Float,
    private val linearDamping: Float,
    private val mass: Float,
    private val maxSpeed: Float,
    private val behavior: Behavior
) : Instance() {

    enum class Behavior {
        FreeFall,
        ConstantForce,
        ConstantAcceleration,
        TimedImpulse,
        VelocityPulse,
        KinematicOrbit,
        StaticAnchor
    }

    private var elapsed = 0f
    private var impulseCooldown = 0f

    override fun onEnterScene() {
        current = current.copy(position = initialPosition)
        configurePhysicsConfig {
            it.copy(
                mode = physicsMode,
                gravityScale = gravityScale,
                linearDamping = linearDamping,
                mass = mass,
                maxSpeed = maxSpeed
            )
        }

        if (behavior == Behavior.TimedImpulse) {
            applyForce(Vec2(250f, -320f), ForceMode.Impulse)
        }
    }

    override fun fixedUpdate(dt: Float) {
        elapsed += dt
        impulseCooldown -= dt

        when (behavior) {
            Behavior.FreeFall -> Unit
            Behavior.ConstantForce -> {
                val wave = 0.5f + (sin(elapsed * 2f) * 0.5f)
                applyForce(Vec2(420f * wave, 0f), ForceMode.Force)
            }

            Behavior.ConstantAcceleration -> {
                val wave = cos(elapsed * 1.5f)
                applyForce(Vec2(180f * wave, -55f), ForceMode.Acceleration)
            }

            Behavior.TimedImpulse -> {
                if (impulseCooldown <= 0f) {
                    impulseCooldown = 1.2f
                    applyForce(Vec2(350f, -330f), ForceMode.Impulse)
                }
            }

            Behavior.VelocityPulse -> {
                val pulse = sin(elapsed * 4f)
                applyForce(Vec2(0f, pulse * 9f), ForceMode.VelocityChange)
            }

            Behavior.KinematicOrbit -> {
                val origin = initialPosition
                val x = origin.x + cos(elapsed * 1.6f) * 90f
                val y = origin.y + sin(elapsed * 1.6f) * 36f
                current = current.copy(position = Vec2(x, y))
            }

            Behavior.StaticAnchor -> Unit
        }

        computePosition(dt)
        confineToViewport()
    }

    private fun confineToViewport() {
        if (physicsState().config.mode != PhysicsSimulationMode.Dynamic) return

        val padding = radius + 8f
        val min = Vec2(padding, padding)
        val max = viewportSize() - padding

        var position = current.position
        var velocity = physicsState().velocity

        if (position.x < min.x) {
            position = position.copy(x = min.x)
            velocity = velocity.copy(x = kotlin.math.abs(velocity.x) * 0.9f)
        } else if (position.x > max.x) {
            position = position.copy(x = max.x)
            velocity = velocity.copy(x = -kotlin.math.abs(velocity.x) * 0.9f)
        }

        if (position.y < min.y) {
            position = position.copy(y = min.y)
            velocity = velocity.copy(y = kotlin.math.abs(velocity.y) * 0.85f)
        } else if (position.y > max.y) {
            position = position.copy(y = max.y)
            velocity = velocity.copy(y = -kotlin.math.abs(velocity.y) * 0.75f)
        }

        current = current.copy(position = position)
        updateVelocity { velocity }
    }

    override fun Renderer.onRender(state: TransformState) {
        drawOval(
            size = Vec2.from(radius * 2),
            state = current,
            color = color
        )

        val stats = physicsState()
        val debugText = "$label\nmode=${stats.config.mode}\nv=${stats.velocity.x.toInt()},${stats.velocity.y.toInt()}"
        drawText(
            text = debugText,
            position = current.position,
            style = TextStyle(
                color = Color(0xFF222222),
                fontSize = TextUnit(11f, TextUnitType.Sp)
            )
        )
    }
}
