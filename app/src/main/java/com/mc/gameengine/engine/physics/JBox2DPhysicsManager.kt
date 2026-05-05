package com.mc.gameengine.engine.physics

import com.mc.gameengine.engine.collision.CollisionEvent
import com.mc.gameengine.engine.collision.CollisionListener
import com.mc.gameengine.engine.collision.CollisionPhase
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.render.Pivot
import org.jbox2d.callbacks.ContactImpulse
import org.jbox2d.callbacks.ContactListener
import org.jbox2d.collision.Manifold
import org.jbox2d.collision.shapes.CircleShape
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.common.Vec2 as JVec2
import org.jbox2d.dynamics.*
import org.jbox2d.dynamics.contacts.Contact

/**
 * Adaptador para integrar JBox2D en el motor de juego.
 * Maneja la simulación física y colisiones automáticamente.
 */
class JBox2DPhysicsManager(
    gravity: Vec2 = Vec2(0f, 980f),
    private val velocityIterations: Int = 6,
    private val positionIterations: Int = 2
) {
    private val world: World = World(JVec2(gravity.x, gravity.y))

    /**
     * Avanza la simulación física por un paso de tiempo.
     */
    fun step(dt: Float) {
        world.step(dt, velocityIterations, positionIterations)
    }

    /**
     * Crea un cuerpo rígido dinámico en JBox2D.
     */
    fun createDynamicBody(
        position: Vec2,
        angle: Float = 0f,
    ): Body {
        val bodyDef = BodyDef()
        bodyDef.type = BodyType.DYNAMIC
        bodyDef.position.set(position.x, position.y)
        bodyDef.angle = angle
        return world.createBody(bodyDef)
    }

    /**
     * Crea un cuerpo estático en JBox2D.
     */
    fun createStaticBody(position: Vec2, angle: Float = 0f): Body {
        val bodyDef = BodyDef()
        bodyDef.type = BodyType.STATIC
        bodyDef.position.set(position.x, position.y)
        bodyDef.angle = angle
        return world.createBody(bodyDef)
    }

    /**
     * Crea un cuerpo cinetico en JBox2D.
     */
    fun createKinematicBody(position: Vec2, angle: Float = 0f): Body {
        val bodyDef = BodyDef()
        bodyDef.type = BodyType.KINEMATIC
        bodyDef.position.set(position.x, position.y)
        bodyDef.angle = angle
        return world.createBody(bodyDef)
    }

    /**
     * Crea una fixture circular para un cuerpo.
     */
    fun createCircleFixture(
        body: Body,
        radius: Float,
        density: Float = 1f,
        friction: Float = 0.3f,
        restitution: Float = 0.1f
    ): Fixture {
        val shape = CircleShape()
        shape.radius = radius

        val fixtureDef = FixtureDef()
        fixtureDef.shape = shape
        fixtureDef.density = density
        fixtureDef.friction = friction
        fixtureDef.restitution = restitution

        return body.createFixture(fixtureDef)
    }

    /**
     * Crea una fixture rectangular para un cuerpo.
     */
    fun createBoxFixture(
        body: Body,
        width: Float,
        height: Float,
        density: Float = 1f,
        friction: Float = 0.3f,
        restitution: Float = 0.1f
    ): Fixture {
        val shape = PolygonShape()
        shape.setAsBox(width / 2f, height / 2f)

        val fixtureDef = FixtureDef()
        fixtureDef.shape = shape
        fixtureDef.density = density
        fixtureDef.friction = friction
        fixtureDef.restitution = restitution

        return body.createFixture(fixtureDef)
    }

    /**
     * Crea una fixture rectangular para un cuerpo.
     */
    fun createPolygonFixture(
        body: Body,
        vertices: Array<Vec2>,
        density: Float = 1f,
        friction: Float = 0.3f,
        restitution: Float = 0.1f
    ): Fixture {
        val shape = PolygonShape()
        shape.set(vertices.map { JVec2(it.x, it.y) }.toTypedArray(), vertices.size)

        val fixtureDef = FixtureDef()
        fixtureDef.shape = shape
        fixtureDef.density = density
        fixtureDef.friction = friction
        fixtureDef.restitution = restitution

        return body.createFixture(fixtureDef)
    }

    /**
     * Destruye un cuerpo del mundo.
     */
    fun destroyBody(body: Body) {
        world.destroyBody(body)
    }

    /**
     * Limpia todos los cuerpos del mundo.
     */
    fun clear() {
        var body = world.bodyList
        while (body != null) {
            val next = body.next
            world.destroyBody(body)
            body = next
        }
    }
}
