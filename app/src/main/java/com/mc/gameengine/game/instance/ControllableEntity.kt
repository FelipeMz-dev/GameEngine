package com.mc.gameengine.game.instance

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import com.mc.engine.core.Instance
import com.mc.engine.core.TransformState
import com.mc.engine.input.keyboard.KeyboardEvent
import com.mc.engine.input.keyboard.KeyboardListener
import com.mc.engine.math.Vec2
import com.mc.engine.math.minus
import com.mc.engine.math.plus
import com.mc.engine.physics.CollisionEvent
import com.mc.engine.physics.CollisionLayers
import com.mc.engine.physics.CollisionListener
import com.mc.engine.physics.CollisionPhase
import com.mc.engine.physics.SensorCollider
import com.mc.engine.physics.Shape
import com.mc.engine.render.Pivot
import com.mc.engine.render.Renderer

class ControllableEntity : Instance(), CollisionListener, KeyboardListener {

    private val speed = 400f
    private val rotation = 200f
    private val sensorSize = Vec2(160f, 160f)

    private lateinit var sensor: SensorCollider
    private var collisioned: SensorCollider? = null
    private var centerCollider = Vec2.Zero

    override fun onEnterScene() {
        updatePosition { Vec2.from(350f) }
        sensor = createSensorCollider(
            shape = Shape.BoxShape(sensorSize),
            state = current.copy(pivot = Pivot.Center),
            layer = CollisionLayers.Player,
            mask = CollisionLayers.World,
        )
    }

    override fun fixedUpdate(dt: Float) {
        sensor.updateTransform { bodyState ->
            bodyState.copy(
                position = current.position,
                angle = current.angle,
                scale = current.scale,
                pivot = Pivot.Center,
            )
        }
        centerCollider = sensor.transformState.position
        collisioned = null
    }

    override fun Renderer.onRender(state: TransformState) {
        drawOval(
            state = TransformState(
                position = centerCollider,
                pivot = Pivot.Center
            ),
            size = Vec2.from(50f),
            color = if (collisioned != null) Color.Green else Color.Red,
        )

        drawText(
            "position: ${sensor.transformState.position} \n angle: ${sensor.transformState.angle} \n scale: ${current.scale}",
            position = Vec2.from(100f)
        )
    }

    override fun onCollision(event: CollisionEvent) {
        val obstacle = when (val other = event.other) {
            is SensorCollider -> other.owner as? Obstacle
            else -> null
        } ?: return

        when (event.phase) {
            CollisionPhase.Enter, CollisionPhase.Stay -> {
                obstacle.collisionText = "colliding with ${event.other::class.simpleName}"
                collisioned = event.other as? SensorCollider
            }

            CollisionPhase.Exit -> {
                obstacle.collisionText = "not collisioned"
                collisioned = null
            }
        }
    }

    override fun onKeyEvent(event: KeyboardEvent) {
        when (event) {
            is KeyboardEvent.KeyHeld -> {
                when (event.key) {
                    Key.A -> updateAngle { it + rotation * event.dt }
                    Key.S -> updateAngle { it - rotation * event.dt }
                    Key.Q -> updateScale { it + event.dt }
                    Key.W -> updateScale { it - event.dt }
                    Key.DirectionLeft -> updatePosition { it.copy(x = it.x - speed * event.dt) }
                    Key.DirectionRight -> updatePosition { it.copy(x = it.x + speed * event.dt) }
                    Key.DirectionUp -> updatePosition { it.copy(y = it.y - speed * event.dt) }
                    Key.DirectionDown -> updatePosition { it.copy(y = it.y + speed * event.dt) }
                }
            }

            else -> Unit
        }
    }
}
