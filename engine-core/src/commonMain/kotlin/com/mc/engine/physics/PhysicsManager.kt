package com.mc.engine.physics

import com.mc.engine.core.Instance
import com.mc.engine.core.TransformState
import com.mc.engine.math.Vec2
import com.mc.engine.math.times
import com.mc.engine.render.Pivot
import org.dyn4j.collision.CategoryFilter
import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.BodyFixture
import org.dyn4j.dynamics.contact.Contact
import org.dyn4j.dynamics.contact.SolvedContact
import org.dyn4j.dynamics.joint.DistanceJoint
import org.dyn4j.dynamics.joint.RevoluteJoint
import org.dyn4j.dynamics.joint.WeldJoint
import org.dyn4j.geometry.Transform
import org.dyn4j.geometry.Vector2
import org.dyn4j.world.ContactCollisionData
import org.dyn4j.world.World
import org.dyn4j.world.listener.ContactListenerAdapter
import kotlin.math.abs
import kotlin.math.sqrt

class PhysicsManager(
    gravity: Vec2 = Vec2(0f, 9.8),
    pixelsPerMeter: Float = 100f
) {

    private val factor = Dyn4jFactor(pixelsPerMeter)
    private val activeContactPairs = mutableMapOf<PhysicsContactPairKey, Int>()
    private var lastStepDt = 1f / 60f
    private val joints = mutableSetOf<PhysicsJoint>()
    internal val world = World<Body>().apply {
        this@apply.gravity = Vector2(gravity.x.toDouble(), gravity.y.toDouble())
        addContactListener(PhysicsContactListener())
    }

    fun update(dt: Float) {
        lastStepDt = dt.coerceAtLeast(MIN_STEP_DT)
        world.update(dt.toDouble())
    }

    internal fun createRigidBody(
        owner: Instance,
        config: RigidBodyConfig,
    ): RigidBody {
        val body = createBody(owner, config)
        val rigidBody = RigidBody(
            owner = owner,
            body = body,
            manager = this,
            layer = config.layer,
            mask = config.mask,
        )
        body.fixtures.forEach { fixture ->
            fixture.userData = rigidBody
            fixture.filter = CategoryFilter(config.layer.toLong(), config.mask.toLong())
        }
        return rigidBody
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
                type = CollisionBodyType.Static,
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

    internal fun createJoint(config: PhysicsJointConfig): PhysicsJoint {
        val bodyA = config.bodyA.body
        val bodyB = config.bodyB.body
        val dynJoint = when (config) {
            is PhysicsJointConfig.Distance -> DistanceJoint(
                bodyA,
                bodyB,
                factor.toDyn4j(config.anchorA),
                factor.toDyn4j(config.anchorB),
            )

            is PhysicsJointConfig.Revolute -> RevoluteJoint(
                bodyA,
                bodyB,
                factor.toDyn4j(config.anchor),
            )

            is PhysicsJointConfig.Weld -> WeldJoint(
                bodyA,
                bodyB,
                factor.toDyn4j(config.anchor),
            )
        }
        dynJoint.isCollisionAllowed = config.collisionAllowed
        world.addJoint(dynJoint)

        val joint = PhysicsJoint(
            joint = dynJoint,
            bodyA = bodyA,
            bodyB = bodyB,
            manager = this,
        )
        joints += joint
        return joint
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

        if (isSensor) fixture.isSensor = true
        else {
            fixture.density = config.material.density.toDouble()
            fixture.friction = config.material.friction.toDouble()
            fixture.restitution = config.material.restitution.toDouble()
        }

        body.userData = owner
        if (!fixture.isSensor) body.setMass(massType)
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
        activeContactPairs.keys.removeIf { key -> key.includes(owner) }
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
        val targets = body.fixtures.mapNotNull { it.userData.asContactTarget() }
        if (targets.isNotEmpty()) {
            activeContactPairs.keys.removeIf { key -> targets.any { target -> key.includes(target) } }
        }
        joints
            .filter { it.includes(body) }
            .forEach { removeJoint(it) }
        world.removeBody(body)
    }

    internal fun removeJoint(joint: PhysicsJoint) {
        world.removeJoint(joint.joint)
        joints -= joint
    }

    internal fun toEngine(vector: Vector2): Vec2 {
        return factor.toEngine(vector)
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

    internal fun isSensor(body: Body): Boolean {
        return body.fixtures.any { it.isSensor }
    }

    internal fun updateBodySensor(body: Body, isSensor: Boolean) {
        body.fixtures.forEach { fixture ->
            fixture.isSensor = isSensor
        }
        body.gravityScale = if (isSensor) 0.0 else 1.0
    }

    private fun handleContact(
        data: ContactCollisionData<Body>,
        contact: Contact,
        phase: CollisionPhase,
        solvedContact: SolvedContact? = null,
    ) {
        val targetA = data.fixture1.userData.asContactTarget() ?: return
        val targetB = data.fixture2.userData.asContactTarget() ?: return
        val isPhysicContact = data.fixture1.userData is RigidBody || data.fixture2.userData is RigidBody
        if (phase != CollisionPhase.Exit && !targetA.canNotify(targetB)) return

        val details = if (isPhysicContact) contact.toDetails(data, solvedContact) else null
        val key = PhysicsContactPairKey.from(targetA, targetB)
        when (phase) {
            CollisionPhase.Enter -> {
                val contacts = activeContactPairs[key] ?: 0
                activeContactPairs[key] = contacts + 1
                if (contacts == 0) dispatch(targetA, targetB, CollisionPhase.Enter, details)
            }

            CollisionPhase.Stay -> dispatch(targetA, targetB, CollisionPhase.Stay, details)

            CollisionPhase.Exit -> {
                val remainingContacts = ((activeContactPairs[key] ?: 1) - 1).coerceAtLeast(0)
                if (remainingContacts == 0) {
                    activeContactPairs.remove(key)
                    dispatch(targetA, targetB, CollisionPhase.Exit, details)
                } else {
                    activeContactPairs[key] = remainingContacts
                }
            }
        }
    }

    private fun dispatch(
        a: PhysicsContactTarget,
        b: PhysicsContactTarget,
        phase: CollisionPhase,
        details: CollisionDetails?,
    ) {
        (a.owner as? CollisionListener)?.onCollision(CollisionEvent(a.source, b.source, phase, details))
        (b.owner as? CollisionListener)?.onCollision(CollisionEvent(b.source, a.source, phase, details?.reversed()))
    }

    private fun Contact.toDetails(
        data: ContactCollisionData<Body>,
        solvedContact: SolvedContact? = null,
    ): CollisionDetails {
        val normal = data.contactConstraint.normal
        val relativeVelocity = Vector2(
            data.body2.linearVelocity.x - data.body1.linearVelocity.x,
            data.body2.linearVelocity.y - data.body1.linearVelocity.y,
        )
        val relativeSpeed = sqrt(
            relativeVelocity.x * relativeVelocity.x + relativeVelocity.y * relativeVelocity.y
        ).toFloat()
        val normalSpeed = (relativeVelocity.x * normal.x + relativeVelocity.y * normal.y).toFloat()
        val normalImpulse = solvedContact?.normalImpulse?.toFloat() ?: 0f
        val tangentImpulse = solvedContact?.tangentialImpulse?.toFloat() ?: 0f
        val force = if (normalImpulse > 0f) {
            normalImpulse / lastStepDt
        } else {
            abs(normalSpeed) / lastStepDt
        }

        return CollisionDetails(
            point = factor.toEngine(point),
            normal = Vec2(normal.x.toFloat(), normal.y.toFloat()),
            depth = depth.toFloat(),
            relativeVelocity = factor.toEngine(relativeVelocity),
            relativeSpeed = factor.mToPx(relativeSpeed.toDouble()),
            normalSpeed = factor.mToPx(normalSpeed.toDouble()),
            normalImpulse = normalImpulse,
            tangentImpulse = tangentImpulse,
            estimatedForce = force,
            isSensor = data.fixture1.isSensor || data.fixture2.isSensor,
        )
    }

    private fun CollisionDetails.reversed(): CollisionDetails {
        return copy(
            normal = Vec2(-normal.x, -normal.y),
            relativeVelocity = Vec2(-relativeVelocity.x, -relativeVelocity.y),
            normalSpeed = -normalSpeed,
        )
    }

    private inner class PhysicsContactListener : ContactListenerAdapter<Body>() {
        override fun begin(collision: ContactCollisionData<Body>, contact: Contact) {
            handleContact(collision, contact, CollisionPhase.Enter)
        }

        override fun persist(
            collision: ContactCollisionData<Body>,
            oldContact: Contact,
            newContact: Contact,
        ) {
            handleContact(collision, newContact, CollisionPhase.Stay)
        }

        override fun end(collision: ContactCollisionData<Body>, contact: Contact) {
            handleContact(collision, contact, CollisionPhase.Exit)
        }

        override fun postSolve(collision: ContactCollisionData<Body>, contact: SolvedContact) {
            handleContact(collision, contact, CollisionPhase.Stay, contact)
        }
    }

    private companion object {
        private const val MIN_STEP_DT = 0.0001f
    }
}

private fun Any?.asContactTarget(): PhysicsContactTarget? {
    return when (this) {
        is RigidBody -> PhysicsContactTarget(
            id = System.identityHashCode(this),
            owner = owner,
            source = this,
            layer = layer,
            mask = mask,
        )
        is SensorCollider -> PhysicsContactTarget(
            id = System.identityHashCode(this),
            owner = owner,
            source = this,
            layer = layer,
            mask = mask,
        )
        else -> null
    }
}
