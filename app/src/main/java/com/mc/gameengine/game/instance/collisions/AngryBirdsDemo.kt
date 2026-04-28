package com.mc.gameengine.game.instance.collisions

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.mc.gameengine.engine.collision.BoxCollider
import com.mc.gameengine.engine.collision.Collider
import com.mc.gameengine.engine.collision.CollisionBodyType
import com.mc.gameengine.engine.collision.CollisionEvent
import com.mc.gameengine.engine.collision.CollisionLayers
import com.mc.gameengine.engine.collision.CollisionListener
import com.mc.gameengine.engine.collision.CollisionPhase
import com.mc.gameengine.engine.collision.EllipseCollider
import com.mc.gameengine.engine.compose.RenderDepth
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.input.touch.TouchEvent
import com.mc.gameengine.engine.input.touch.TouchListener
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.clamp
import com.mc.gameengine.engine.math.div
import com.mc.gameengine.engine.math.minus
import com.mc.gameengine.engine.math.plus
import com.mc.gameengine.engine.math.times
import com.mc.gameengine.engine.physics.PhysicsSimulationMode
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer
import kotlin.math.max

class BallLauncher(
    private val launchPoint: Vec2 = Vec2(180f, 620f)
) : Instance(), TouchListener {

    private val ballSpecs = listOf(
        BallSpec(radius = 24f, mass = 0.7f, color = Color(0xFFe53935)),
        BallSpec(radius = 30f, mass = 1.4f, color = Color(0xFF1e88e5)),
        BallSpec(radius = 38f, mass = 2.6f, color = Color(0xFF43a047)),
    )
    private var nextBall = 0

    override fun onTouchEvent(event: TouchEvent) {
        if (event !is TouchEvent.TapEvent) return

        val worldTap = screenToWorld(event.position)
        val direction = (worldTap - launchPoint)
        if (direction.length() < 8f) return

        val launchVelocity = direction.clamp(680f) * 2.2f
        val spec = ballSpecs[nextBall]
        nextBall = (nextBall + 1) % ballSpecs.size

        addInstance(
            ProjectileBall(
                start = launchPoint,
                velocity = launchVelocity,
                spec = spec
            )
        )
    }

    override fun Renderer.onRender(state: TransformState) {
        drawCircle(
            radius = 18f,
            state = TransformState(position = launchPoint, pivot = Pivot.Center),
            color = Color.DarkGray,
            deep = RenderDepth.DEBUG
        )

        drawText(
            text = "Tap para lanzar bolas de distinto peso/tamaño",
            position = Vec2(24f, 28f),
            style = TextStyle(color = Color.DarkGray),
            deep = RenderDepth.DEBUG
        )
    }
}

data class BallSpec(
    val radius: Float,
    val mass: Float,
    val color: Color
)

class ProjectileBall(
    private val start: Vec2,
    private val velocity: Vec2,
    private val spec: BallSpec
) : Instance(), CollisionListener {

    private val collider: Collider = EllipseCollider(this, spec.radius * 2f, spec.radius * 2f)
    private var livedSeconds = 0f

    override fun onEnterScene() {
        current = current.copy(position = start, pivot = Pivot.Center)
        configurePhysicsConfig {
            it.copy(
                mode = PhysicsSimulationMode.Dynamic,
                mass = spec.mass,
                gravityScale = 1f,
                linearDamping = 0.12f,
                maxSpeed = 1800f
            )
        }
        updateVelocity { velocity }

        collider.setBodyType(CollisionBodyType.Dynamic)
        collider.setCollisionFilter(layer = CollisionLayers.Player, mask = CollisionLayers.World)
        collider.update { it.copy(position = current.position, pivot = Pivot.Center) }
        addCollider(collider)
    }

    override fun fixedUpdate(dt: Float) {
        livedSeconds += dt
        computePosition(dt)
        collider.update { it.copy(position = current.position) }

        if (livedSeconds >= 10f || current.position.y > viewportSize().y + 250f) {
            deleteInstance(this)
        }
    }

    fun impactImpulse(): Vec2 {
        val speed = physicsState().velocity
        return speed * max(spec.mass, 0.2f) * 0.55f
    }

    override fun onCollision(event: CollisionEvent) {
        if (event.phase != CollisionPhase.Enter) return
        val block = event.other.owner as? StackBlock ?: return
        block.applyHitImpulse(impactImpulse())
    }

    override fun Renderer.onRender(state: TransformState) {
        drawCircle(
            radius = spec.radius,
            state = TransformState(position = state.position, pivot = Pivot.Center),
            color = spec.color
        )
    }
}

class StackBlock(
    private val start: Vec2,
    private val size: Vec2,
    val mass: Float,
    private val color: Color,
    private val floorY: Float
) : Instance(), CollisionListener {

    private val collider = BoxCollider(this, size.x, size.y)

    override fun onEnterScene() {
        current = current.copy(position = start, pivot = Pivot.Center)
        configurePhysicsConfig {
            it.copy(
                mode = PhysicsSimulationMode.Dynamic,
                mass = mass,
                gravityScale = 0.95f,
                linearDamping = 0.85f,
                maxSpeed = 900f
            )
        }

        collider.setBodyType(CollisionBodyType.Dynamic)
        collider.setCollisionFilter(layer = CollisionLayers.World, mask = CollisionLayers.Player or CollisionLayers.World)
        collider.update { it.copy(position = current.position, pivot = Pivot.Center) }
        addCollider(collider)
    }

    override fun fixedUpdate(dt: Float) {
        computePosition(dt)

        val min = Vec2(0f, 0f)
        val max = viewportSize() + Vec2(300f, 0f)
        val clampedPos = current.position.clamp(min, max)
        current = current.copy(position = clampedPos)

        val lowestPoint = current.position.y + size.y / 2f
        if (lowestPoint >= floorY) {
            current = current.copy(position = current.position.copy(y = floorY - size.y / 2f))
            updateVelocity { it.copy(y = 0f) }
        }

        collider.update { it.copy(position = current.position) }
    }

    fun applyHitImpulse(impulse: Vec2) {
        val normalizedByMass = impulse / max(mass, 0.1f)
        updateVelocity { it + normalizedByMass }
    }

    override fun onCollision(event: CollisionEvent) {
        if (event.phase != CollisionPhase.Enter) return
        val other = event.other.owner as? StackBlock ?: return
        if (other === this) return

        val transfer = physicsState().velocity * 0.3f
        if (transfer.length() > 10f) {
            other.applyHitImpulse(transfer * other.mass)
        }
    }

    override fun Renderer.onRender(state: TransformState) {
        drawRect(
            size = size,
            state = TransformState(position = state.position, pivot = Pivot.Center),
            color = color
        )
    }
}
