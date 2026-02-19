package com.mc.gameengine.game.instance.collisions

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import com.mc.gameengine.engine.collision.BoxCollider
import com.mc.gameengine.engine.collision.Collider
import com.mc.gameengine.engine.collision.CollisionEvent
import com.mc.gameengine.engine.collision.CollisionListener
import com.mc.gameengine.engine.collision.EllipseCollider
import com.mc.gameengine.engine.collision.PolygonalCollider
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.input.KeyHeld
import com.mc.gameengine.engine.input.KeyboardEvent
import com.mc.gameengine.engine.input.KeyboardListener
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.minus
import com.mc.gameengine.engine.math.plus
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer

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

    private var centerCollider = Vec2.Zero

    override fun onEnterScene() {
        updatePosition { Vec2.from(350f) }
        collider.update { it.copy(pivot = Pivot.Custom(20f, 70f)) }
        addCollider(collider)
    }

    override fun fixedUpdate(dt: Float) {
        computePosition(dt)
        collider.update { it.copy(position = current.position) }
        centerCollider = when (collider) {
            is BoxCollider -> collider.getCenter()
            is EllipseCollider -> collider.getCenter()
            is PolygonalCollider -> collider.getCenter()
            else -> Vec2.Zero
        }
        collisioned = null
    }

    override fun Renderer.onRender(state: TransformState) {
        drawOval(
            position = centerCollider,
            size = Vec2.from(50f),
            color = when (collisioned){
                is EllipseCollider -> Color.Green
                is BoxCollider -> Color.Blue
                else -> Color.Red
            },
            pivot = Pivot.Center
        )

        drawText(
            "position: ${collider.state.position} \n angle: ${collider.state.angle} \n scale: ${collider.state.scale}",
            position = Vec2.from(100f)
        )
    }

    override fun onCollision(event: CollisionEvent) {
        (event.other.owner as Obstacle).collisionText = event.other.toString()
        collisioned = event.other
    }

    override fun onKeyEvent(event: KeyboardEvent) {
        when (event) {
            is KeyHeld -> {
                when (event.key) {
                    Key.A -> collider.update { it.copy(angle = it.angle + rotation * event.dt) }
                    Key.S -> collider.update { it.copy(angle = it.angle - rotation * event.dt) }
                    Key.Q -> collider.update { it.copy(scale = it.scale + event.dt) }
                    Key.W -> collider.update { it.copy(scale = it.scale - event.dt) }
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