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
import com.mc.gameengine.engine.math.dot
import com.mc.gameengine.engine.math.minus
import com.mc.gameengine.engine.math.normalized
import com.mc.gameengine.engine.math.plus
import com.mc.gameengine.engine.math.times
import com.mc.gameengine.engine.physics.PhysicsSimulationMode
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer
import kotlin.math.max

class BallLauncher(
    private val launchPoint: Vec2 = Vec2(180f, 620f),
    private val floorY: Float = 700f
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
                spec = spec,
                floorY = floorY
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
    private val spec: BallSpec,
    private val floorY: Float
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
        collider.setCollisionFilter(layer = CollisionLayers.Player, mask = CollisionLayers.World or CollisionLayers.Player)
        collider.update { it.copy(position = current.position, pivot = Pivot.Center) }
        addCollider(collider)
    }

    override fun fixedUpdate(dt: Float) {
        livedSeconds += dt
        computePosition(dt)
        resolveFloorBounce()
        collider.update { it.copy(position = current.position) }

        if (livedSeconds >= 10f || isOutOfScreen()) {
            deleteInstance(this)
        }
    }

    fun impactImpulse(): Vec2 {
        val speed = physicsState().velocity
        return speed * max(spec.mass, 0.2f) * 0.55f
    }

    override fun onCollision(event: CollisionEvent) {
        if (event.phase != CollisionPhase.Enter) return
        when (val otherOwner = event.other.owner) {
            is StackBlock -> {
                otherOwner.applyHitImpulse(impactImpulse())
                bounceAgainstBlock(otherOwner)
            }
            is ProjectileBall -> {
                if (System.identityHashCode(this) < System.identityHashCode(otherOwner)) {
                    bounceAgainstBall(otherOwner)
                }
            }
        }
    }

    private fun bounceAgainstBlock(block: StackBlock) {
        val incomingVelocity = physicsState().velocity
        val collisionNormal = (position() - block.centerPosition()).normalized()
        val normal = if (collisionNormal.length() <= 0.001f) Vec2(0f, -1f) else collisionNormal

        val velocityOnNormal = incomingVelocity.dot(normal)
        if (velocityOnNormal >= 0f) return

        val tangentComponent = incomingVelocity - normal * velocityOnNormal
        val restitution = block.bounceRestitution(
            ballMass = spec.mass,
            impactSpeed = incomingVelocity.length(),
            impactNormalSpeed = kotlin.math.abs(velocityOnNormal)
        )
        val bouncedVelocity = tangentComponent * 0.92f - normal * velocityOnNormal * restitution

        current = current.copy(position = current.position + normal * 3f)
        setVelocity(bouncedVelocity)
    }

    private fun bounceAgainstBall(other: ProjectileBall) {
        val normalRaw = (position() - other.position())
        val normal = if (normalRaw.length() <= 0.001f) Vec2(1f, 0f) else normalRaw.normalized()
        val v1 = physicsState().velocity
        val v2 = other.physicsState().velocity
        val v1n = v1.dot(normal)
        val v2n = v2.dot(normal)
        if (v1n - v2n >= 0f) return

        val m1 = max(spec.mass, 0.1f)
        val m2 = max(other.spec.mass, 0.1f)
        val restitution = 0.72f

        val nextV1n = ((m1 - restitution * m2) * v1n + (1f + restitution) * m2 * v2n) / (m1 + m2)
        val nextV2n = ((m2 - restitution * m1) * v2n + (1f + restitution) * m1 * v1n) / (m1 + m2)

        val correctedV1 = v1 + normal * (nextV1n - v1n)
        val correctedV2 = v2 + normal * (nextV2n - v2n)
        setVelocity(correctedV1 * 0.96f)
        other.setVelocity(correctedV2 * 0.96f)
    }

    private fun resolveFloorBounce() {
        val bottom = current.position.y + spec.radius
        if (bottom < floorY) return
        current = current.copy(position = current.position.copy(y = floorY - spec.radius))
        val velocity = physicsState().velocity
        if (velocity.y > 0f) {
            val restitution = (0.36f + (spec.mass / 3f) * 0.18f).coerceIn(0.3f, 0.58f)
            setVelocity(Vec2(velocity.x * 0.92f, -velocity.y * restitution))
        }
    }

    private fun isOutOfScreen(): Boolean {
        val margin = 180f
        val view = viewportSize()
        return current.position.x < -margin ||
            current.position.x > view.x + margin ||
            current.position.y < -margin ||
            current.position.y > view.y + margin
    }

    private fun position(): Vec2 = current.position

    private fun setVelocity(velocity: Vec2) {
        updateVelocity { velocity }
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

    fun centerPosition(): Vec2 = current.position

    fun bounceRestitution(
        ballMass: Float,
        impactSpeed: Float,
        impactNormalSpeed: Float
    ): Float {
        val heavyBlockFactor = (mass / (mass + max(ballMass, 0.1f))).coerceIn(0.2f, 0.9f)
        val impactFactor = (impactNormalSpeed / 900f).coerceIn(0f, 1f)
        val speedFactor = (impactSpeed / 1200f).coerceIn(0f, 1f)
        return (0.18f + heavyBlockFactor * 0.37f + impactFactor * 0.25f + speedFactor * 0.1f)
            .coerceIn(0.2f, 0.88f)
    }

    override fun onCollision(event: CollisionEvent) {
        if (event.phase != CollisionPhase.Enter && event.phase != CollisionPhase.Stay) return

        when (val otherOwner = event.other.owner) {
            is StackBlock -> {
                if (otherOwner === this) return
                if (System.identityHashCode(this) < System.identityHashCode(otherOwner)) {
                    resolveBlockCollision(otherOwner)
                }
            }
        }
    }

    private fun resolveBlockCollision(other: StackBlock) {
        val delta = other.position() - position()
        val overlapX = (size.x + other.size.x) * 0.5f - kotlin.math.abs(delta.x)
        val overlapY = (size.y + other.size.y) * 0.5f - kotlin.math.abs(delta.y)

        if (overlapX <= 0f || overlapY <= 0f) return

        val normal = if (overlapX < overlapY) {
            val sign = if (delta.x >= 0f) 1f else -1f
            translate(Vec2(-overlapX * 0.5f * sign, 0f))
            other.translate(Vec2(overlapX * 0.5f * sign, 0f))
            Vec2(sign, 0f)
        } else {
            val sign = if (delta.y >= 0f) 1f else -1f
            translate(Vec2(0f, -overlapY * 0.5f * sign))
            other.translate(Vec2(0f, overlapY * 0.5f * sign))
            Vec2(0f, sign)
        }

        val v1 = velocity()
        val v2 = other.velocity()
        val v1n = v1.dot(normal)
        val v2n = v2.dot(normal)
        val m1 = max(mass, 0.1f)
        val m2 = max(other.mass, 0.1f)

        val nextV1n = ((m1 - m2) * v1n + 2f * m2 * v2n) / (m1 + m2)
        val nextV2n = ((m2 - m1) * v2n + 2f * m1 * v1n) / (m1 + m2)

        val correction1 = normal * (nextV1n - v1n) * 0.85f
        val correction2 = normal * (nextV2n - v2n) * 0.85f

        setVelocity(v1 + correction1)
        other.setVelocity(v2 + correction2)
    }

    private fun position(): Vec2 = current.position

    private fun velocity(): Vec2 = physicsState().velocity

    private fun translate(delta: Vec2) {
        current = current.copy(position = current.position + delta)
        collider.update { it.copy(position = current.position) }
    }

    private fun setVelocity(velocity: Vec2) {
        updateVelocity { velocity }
    }

    override fun Renderer.onRender(state: TransformState) {
        drawRect(
            size = size,
            state = TransformState(position = state.position, pivot = Pivot.Center),
            color = color
        )
    }
}
