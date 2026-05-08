package com.mc.gameengine.engine.physics

import com.mc.gameengine.engine.collision.CollisionBodyType
import com.mc.gameengine.engine.collision.CollisionEvent
import com.mc.gameengine.engine.collision.CollisionListener
import com.mc.gameengine.engine.collision.CollisionPhase
import com.mc.gameengine.engine.collision.PhysicsMaterial
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.times
import com.mc.gameengine.engine.render.Pivot
import org.dyn4j.collision.CategoryFilter
import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.BodyFixture
import org.dyn4j.dynamics.contact.Contact
import org.dyn4j.geometry.Transform
import org.dyn4j.geometry.Vector2
import org.dyn4j.world.ContactCollisionData
import org.dyn4j.world.World
import org.dyn4j.world.listener.ContactListenerAdapter
import kotlin.math.sqrt

class PhysicsManager(
    gravity: Vec2 = Vec2(0f, 9.8),
    pixelsPerMeter: Float = 100f
) {

    private val factor = Dyn4jFactor(pixelsPerMeter)
    private val sensorPairs = mutableMapOf<SensorPairKey, Int>()
    internal val world = World<Body>().apply {
        this@apply.gravity = Vector2(gravity.x.toDouble(), gravity.y.toDouble())
        addContactListener(SensorContactListener())
    }

    internal fun update(dt: Float) {
        world.update(dt.toDouble())
    }

    internal fun createRigidBody(
        owner: Instance,
        config: RigidBodyConfig,
    ): RigidBody {
        val body = createBody(owner, config)
        return RigidBody(body = body, manager = this)
    }

    internal fun createSensorCollider(
        owner: Instance,
        config: SensorColliderConfig,
    ): SensorCollider {
        val body = createBody(
            owner = owner,
            config = RigidBodyConfig(
                shape = config.shape,
                state = config.state,
                type = config.type,
                physicState = config.physicState,
            ),
            isSensor = true,
        )
        val sensor = SensorCollider(
            owner = owner,
            body = body,
            manager = this,
            layer = config.layer,
            mask = config.mask,
        )
        body.fixtures.forEach { fixture ->
            fixture.userData = sensor
            fixture.filter = CategoryFilter(config.layer.toLong(), config.mask.toLong())
        }
        return sensor
    }

    internal fun createRigidBody(
        owner: Instance,
        shape: Shape,
        state: TransformState = TransformState(),
        type: CollisionBodyType = CollisionBodyType.Dynamic,
        material: PhysicsMaterial = PhysicsMaterial(),
        physicState: PhysicState = PhysicState(),
    ): RigidBody {
        return createRigidBody(
            owner = owner,
            config = RigidBodyConfig(
                shape = shape,
                state = state,
                type = type,
                material = material,
                physicState = physicState,
            )
        )
    }

    private fun createBody(
        owner: Instance,
        config: RigidBodyConfig,
        isSensor: Boolean = false,
    ): Body {
        val body = Body()
        val angleRadians = factor.degToRad(config.state.angle)
        val positionMeters = factor.toDyn4j(config.state.position)
        val massType = factor.toDyn4j(config.type)
        val scaledShape = scaleShape(config.shape, config.state.scale)
        val jShape = factor.toDyn4j(scaledShape)
        val fixture = body.addFixture(jShape)

        fixture.density = config.material.density.toDouble()
        fixture.friction = config.material.friction.toDouble()
        fixture.restitution = config.material.restitution.toDouble()
        fixture.isSensor = isSensor

        body.userData = owner
        if (isSensor) body.gravityScale = 0.0
        body.setMass(massType)
        body.rotate(angleRadians)
        body.translate(positionMeters)
        updateBodyPhysicState(body, config.physicState)

        world.addBody(body)
        return body
    }

    internal fun getTransformState(body: Body): TransformState {
        return TransformState(
            position = factor.toEngine(body.transform.translation),
            angle = body.transform.rotation.toDegrees().toFloat(),
            scale = Vec2(1f, 1f),
            pivot = Pivot.Center,
        )
    }

    internal fun updateBodyTransform(body: Body, state: TransformState) {
        val transform = Transform()
        transform.setRotation(factor.degToRad(state.angle))
        transform.setTranslation(factor.toDyn4j(state.position))
        body.setTransform(transform)
    }

    internal fun getPhysicState(body: Body): PhysicState {
        return PhysicState(
            linearVelocity = factor.toEngine(body.linearVelocity),
            angularVelocity = body.angularVelocity.toFloat(),
            linearDamping = body.linearDamping.toFloat(),
            angularDamping = body.angularDamping.toFloat(),
            bullet = body.isBullet
        )
    }

    internal fun updateBodyPhysicState(body: Body, physicState: PhysicState) {
        body.linearVelocity = factor.toDyn4j(physicState.linearVelocity)
        body.angularVelocity = physicState.angularVelocity.toDouble()
        body.linearDamping = physicState.linearDamping.toDouble()
        body.angularDamping = physicState.angularDamping.toDouble()
        body.isBullet = physicState.bullet
    }

    internal fun getMaterial(body: Body): PhysicsMaterial? {
        val fixture = body.fixtures.firstOrNull()
        return fixture?.let {
            PhysicsMaterial(
                density = fixture.density.toFloat(),
                friction = fixture.friction.toFloat(),
                restitution = fixture.restitution.toFloat()
            )
        }
    }

    internal fun getShape(body: Body): Shape? {
        val fixture = body.fixtures.firstOrNull()
        return fixture?.let {
            factor.toEngine(fixture.shape)
        }
    }

    internal fun getType(body: Body): CollisionBodyType {
        return factor.toEngine(body.mass.type)
    }

    internal fun updateBodyType(body: Body, type: CollisionBodyType) {
        val massType = factor.toDyn4j(type)
        body.setMass(massType)
    }

    internal fun updateBodyMaterial(body: Body, material: PhysicsMaterial) {
        body.fixtures.forEach { fixture ->
            fixture.density = material.density.toDouble()
            fixture.friction = material.friction.toDouble()
            fixture.restitution = material.restitution.toDouble()
        }
    }

    internal fun updateBodyShape(body: Body, transformState: TransformState, shape: Shape) {
        val previousFixture = body.fixtures.firstOrNull()
        val scaledShape = scaleShape(shape, transformState.scale)
        val jShape = factor.toDyn4j(scaledShape)
        body.removeAllFixtures()
        val fixture = body.addFixture(jShape)
        previousFixture?.copyRuntimeFlagsTo(fixture)
    }

    internal fun verifyOwnerRemoved(owner: Instance) {
        val bodies = world.bodies.filter { it.userData == owner }
        bodies.forEach { removeBody(it) }
        sensorPairs.keys.removeIf { key -> key.includes(owner) }
    }

    internal fun applyImpulseTowards(
        body: Body,
        target: Vec2,
        force: Float = 40f,
        point: Vec2? = null
    ) {
        val targetXM = factor.pxToM(target.x)
        val targetYM = factor.pxToM(target.y)

        val bodyPos = body.transform.translation

        val dirX = targetXM - bodyPos.x
        val dirY = targetYM - bodyPos.y

        val length = sqrt((dirX * dirX + dirY * dirY)).toFloat()

        if (length > 0) {
            val normal = Vec2(dirX / length, dirY / length)
            val impulse = factor.toDyn4j(normal * force)
            val jPoint = point?.let { factor.toDyn4j(it) }
            jPoint?.apply { body.applyImpulse(impulse, jPoint) } ?: body.applyImpulse(impulse)
        }
    }

    internal fun applyImpulse(body: Body, impulse: Vec2, point: Vec2? = null) {
        val jImpulse = factor.toDyn4j(impulse)
        val jPoint = point?.let { factor.toDyn4j(it) }
        jPoint?.apply { body.applyImpulse(jImpulse, jPoint) } ?: body.applyImpulse(jImpulse)
    }

    internal fun applyForce(body: Body, force: Vec2, point: Vec2? = null) {
        val jForce = factor.toDyn4j(force)
        val jPoint = point?.let { factor.toDyn4j(it) }
        jPoint?.let { body.applyForce(jForce, it) } ?: body.applyForce(jForce)
    }

    internal fun applyTorque(body: Body, torque: Float) {
        body.applyTorque(torque.toDouble())
    }

    internal fun stopBody(body: Body) {
        body.linearVelocity = Vector2()
        body.angularVelocity = 0.0
    }

    internal fun removeBody(body: Body) {
        val sensors = body.fixtures.mapNotNull { it.userData as? SensorCollider }
        if (sensors.isNotEmpty()) {
            sensorPairs.keys.removeIf { key -> sensors.any { sensor -> key.includes(sensor) } }
        }
        world.removeBody(body)
    }

    private fun scaleShape(shape: Shape, scale: Vec2): Shape {
        return when (shape) {
            is Shape.BoxShape -> Shape.BoxShape(shape.size * scale)
            is Shape.EllipseShape -> Shape.EllipseShape(shape.radius * scale)
            is Shape.CapsuleShape -> Shape.CapsuleShape(shape.size * scale)
            is Shape.SliceShape -> Shape.SliceShape(shape.radius * scale.x, shape.theta)
            is Shape.PolygonShape -> Shape.PolygonShape(shape.vertices.map { it * scale })
            is Shape.SegmentShape -> Shape.SegmentShape(shape.start * scale, shape.end * scale)
            is Shape.CircleShape -> {
                val avgScale = (scale.x + scale.y) / 2f
                Shape.CircleShape(shape.radius * avgScale)
            }
        }
    }

    private fun BodyFixture.copyRuntimeFlagsTo(target: BodyFixture) {
        target.density = density
        target.friction = friction
        target.restitution = restitution
        target.isSensor = isSensor
        target.filter = filter
        target.userData = userData
    }

    private fun handleSensorContact(
        data: ContactCollisionData<Body>,
        phase: CollisionPhase,
    ) {
        val sensorA = data.fixture1.userData as? SensorCollider ?: return
        val sensorB = data.fixture2.userData as? SensorCollider ?: return
        if (phase != CollisionPhase.Exit && !sensorA.canNotify(sensorB)) return

        val key = SensorPairKey.from(sensorA, sensorB)
        when (phase) {
            CollisionPhase.Enter -> {
                val contacts = sensorPairs[key] ?: 0
                sensorPairs[key] = contacts + 1
                if (contacts == 0) dispatch(sensorA, sensorB, CollisionPhase.Enter)
            }

            CollisionPhase.Stay -> dispatch(sensorA, sensorB, CollisionPhase.Stay)

            CollisionPhase.Exit -> {
                val remainingContacts = ((sensorPairs[key] ?: 1) - 1).coerceAtLeast(0)
                if (remainingContacts == 0) {
                    sensorPairs.remove(key)
                    dispatch(sensorA, sensorB, CollisionPhase.Exit)
                } else {
                    sensorPairs[key] = remainingContacts
                }
            }
        }
    }

    private fun dispatch(a: SensorCollider, b: SensorCollider, phase: CollisionPhase) {
        (a.owner as? CollisionListener)?.onCollision(CollisionEvent(a, b, phase))
        (b.owner as? CollisionListener)?.onCollision(CollisionEvent(b, a, phase))
    }

    private inner class SensorContactListener : ContactListenerAdapter<Body>() {
        override fun begin(collision: ContactCollisionData<Body>, contact: Contact) {
            handleSensorContact(collision, CollisionPhase.Enter)
        }

        override fun persist(
            collision: ContactCollisionData<Body>,
            oldContact: Contact,
            newContact: Contact,
        ) {
            handleSensorContact(collision, CollisionPhase.Stay)
        }

        override fun end(collision: ContactCollisionData<Body>, contact: Contact) {
            handleSensorContact(collision, CollisionPhase.Exit)
        }
    }
}

private data class SensorPairKey(
    val minId: Int,
    val maxId: Int,
    val ownerA: Instance,
    val ownerB: Instance,
) {
    fun includes(owner: Instance): Boolean = ownerA == owner || ownerB == owner

    fun includes(sensor: SensorCollider): Boolean = minId == sensor.id || maxId == sensor.id

    companion object {
        fun from(a: SensorCollider, b: SensorCollider): SensorPairKey {
            return if (a.id <= b.id) {
                SensorPairKey(a.id, b.id, a.owner, b.owner)
            } else {
                SensorPairKey(b.id, a.id, b.owner, a.owner)
            }
        }
    }
}
