package com.mc.gameengine.game.instance

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import com.mc.gameengine.engine.collision.BoxCollider
import com.mc.gameengine.engine.collision.Collider
import com.mc.gameengine.engine.collision.CollisionBodyType
import com.mc.gameengine.engine.collision.CollisionEvent
import com.mc.gameengine.engine.collision.CollisionLayers
import com.mc.gameengine.engine.collision.CollisionListener
import com.mc.gameengine.engine.collision.CollisionPhase
import com.mc.gameengine.engine.collision.EllipseCollider
import com.mc.gameengine.engine.collision.PolygonalCollider
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.input.keyboard.KeyboardEvent
import com.mc.gameengine.engine.input.keyboard.KeyboardListener
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.minus
import com.mc.gameengine.engine.math.plus
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer
import com.mc.gameengine.game.instance.Obstacle

class ControllableEntity : Instance(), CollisionListener, KeyboardListener {

    private val speed = 400f

    private val rotation = 200f

    private var collisioned: Collider? = null

    private val collider: Collider = PolygonalCollider(
        this,
        listOf(
            Vec2(100f, 0f),
            Vec2(200f, 100f),
            Vec2(150f, 200f),
            Vec2(50f, 200f),
            Vec2(0f, 100f)
        )
    )

    private var centerCollider = Vec2.Companion.Zero

    override fun onEnterScene() {
        updatePosition { Vec2.Companion.from(350f) }
        collider.update { it.copy(pivot = Pivot.Custom(20f, 70f)) }
        collider.setBodyType(CollisionBodyType.Dynamic)
        collider.setCollisionFilter(layer = CollisionLayers.Player, mask = CollisionLayers.World)
        addCollider(collider)
    }

    override fun fixedUpdate(dt: Float) {
        collider.update { it.copy(position = current.position) }
        centerCollider = when (collider) {
            is BoxCollider -> collider.getCenter()
            is EllipseCollider -> collider.getCenter()
            is PolygonalCollider -> collider.getCenter()
            else -> Vec2.Companion.Zero
        }
        collisioned = null
    }

    override fun Renderer.onRender(state: TransformState) {
        drawOval(
            state = TransformState(
                position = centerCollider,
                pivot = Pivot.Center
            ),
            size = Vec2.Companion.from(50f),
            color = when (collisioned) {
                is EllipseCollider -> Color.Companion.Green
                is BoxCollider -> Color.Companion.Blue
                else -> Color.Companion.Red
            },
        )

        drawText(
            "position: ${collider.state.position} \n angle: ${collider.state.angle} \n scale: ${collider.state.scale}",
            position = Vec2.Companion.from(100f)
        )
    }

    override fun onCollision(event: CollisionEvent) {
        val obstacle = when (event.other) {
            is Collider -> event.other.owner as? Obstacle
            else -> null // Para fixtures de JBox2D, no aplicable aquí
        } ?: return
        when (event.phase) {
            CollisionPhase.Enter, CollisionPhase.Stay -> {
                obstacle.collisionText = "colliding with ${event.other::class.simpleName}"
                collisioned = event.other as? Collider
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
                    Key.Companion.A -> collider.update { it.copy(angle = it.angle + rotation * event.dt) }
                    Key.Companion.S -> collider.update { it.copy(angle = it.angle - rotation * event.dt) }
                    Key.Companion.Q -> collider.update { it.copy(scale = it.scale + event.dt) }
                    Key.Companion.W -> collider.update { it.copy(scale = it.scale - event.dt) }
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