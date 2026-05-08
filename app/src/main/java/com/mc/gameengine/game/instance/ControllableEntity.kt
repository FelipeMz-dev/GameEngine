package com.mc.gameengine.game.instance

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import com.mc.gameengine.engine.collision.CollisionBodyType
import com.mc.gameengine.engine.collision.CollisionEvent
import com.mc.gameengine.engine.collision.CollisionLayers
import com.mc.gameengine.engine.collision.CollisionListener
import com.mc.gameengine.engine.collision.CollisionPhase
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.input.keyboard.KeyboardEvent
import com.mc.gameengine.engine.input.keyboard.KeyboardListener
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.minus
import com.mc.gameengine.engine.math.plus
import com.mc.gameengine.engine.physics.SensorCollider
import com.mc.gameengine.engine.physics.Shape
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer

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
            type = CollisionBodyType.Dynamic,
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
        val obstacle = when (event.other) {
            is SensorCollider -> event.other.owner as? Obstacle
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
                    Key.Companion.A -> updateAngle { it + rotation * event.dt }
                    Key.Companion.S -> updateAngle { it - rotation * event.dt }
                    Key.Companion.Q -> updateScale { it + event.dt }
                    Key.Companion.W -> updateScale { it - event.dt }
                    Key.Companion.DirectionLeft -> updatePosition { it.copy(x = it.x - speed * event.dt) }
                    Key.Companion.DirectionRight -> updatePosition { it.copy(x = it.x + speed * event.dt) }
                    Key.Companion.DirectionUp -> updatePosition { it.copy(y = it.y - speed * event.dt) }
                    Key.Companion.DirectionDown -> updatePosition { it.copy(y = it.y + speed * event.dt) }
                }
            }

            else -> Unit
        }
    }
}
