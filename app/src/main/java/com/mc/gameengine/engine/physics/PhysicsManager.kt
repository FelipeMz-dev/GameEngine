package com.mc.gameengine.engine.physics

import com.mc.gameengine.engine.collision.CollisionBodyType
import com.mc.gameengine.engine.collision.PhysicsMaterial
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.times
import com.mc.gameengine.engine.render.Pivot
import org.dyn4j.dynamics.Body
import org.dyn4j.geometry.Vector2
import org.dyn4j.world.World
import kotlin.math.sqrt

class PhysicsManager(
    gravity: Vec2 = Vec2(0f, 9.8),
    pixelsPerMeter: Float = 100f
) {

    private val factor = Dyn4jFactor(pixelsPerMeter)
    internal val world = World<Body>().apply {
        this@apply.gravity = Vector2(gravity.x.toDouble(),gravity.y.toDouble())
    }

    internal fun update(dt: Float) {
        world.update(dt.toDouble())
    }

    internal fun createRigidBody(
        owner: Instance,
        shape: Shape,
        state: TransformState,
        type: CollisionBodyType = CollisionBodyType.Dynamic,
        material: PhysicsMaterial? = null
    ): Body {
        val body = Body()
        val angleRadians = factor.degToRad(state.angle)
        val positionMeters = factor.toDyn4j(state.position)
        val massType = factor.toDyn4j(type)
        val scaledShape = scaleShape(shape, state.scale)
        val jShape = factor.toDyn4j(scaledShape)
        val fixture = body.addFixture(jShape)

        material?.apply {
            fixture.density = material.density.toDouble()
            fixture.friction = material.friction.toDouble()
            fixture.restitution = material.restitution.toDouble()
        }

        body.userData = owner
        body.setMass(massType)
        body.rotate(angleRadians)
        body.translate(positionMeters)

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
        val angleRadians = factor.degToRad(state.angle)
        val positionMeters = factor.toDyn4j(state.position)
        body.rotate(angleRadians)
        body.translate(positionMeters)
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
        val scaledShape = scaleShape(shape, transformState.scale)
        val jShape = factor.toDyn4j(scaledShape)
        body.removeAllFixtures()
        body.addFixture(jShape)
    }

    internal fun verifyOwnerRemoved(owner: Instance) {
        val bodies = world.bodies.filter { it.userData == owner }
        bodies.forEach { world.removeBody(it) }
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
}