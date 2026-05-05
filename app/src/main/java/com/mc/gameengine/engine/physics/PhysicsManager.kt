package com.mc.gameengine.engine.physics

import android.service.autofill.UserData
import com.mc.gameengine.engine.collision.CollisionBodyType
import com.mc.gameengine.engine.collision.PhysicsMaterial
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.times
import com.mc.gameengine.engine.render.Pivot
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.BodyType
import org.jbox2d.dynamics.FixtureDef
import org.jbox2d.dynamics.World
import kotlin.math.sqrt
import org.jbox2d.common.Vec2 as JVec2

/**
 * Administra la creación de cuerpos físicos convirtiendo unidades de píxeles a metros.
 * @param world El mundo de JBox2D donde se crearán los cuerpos.
 * @param pixelsPerMeter La escala de conversión (ej. 50f).
 */
class PhysicsManager(
    gravity: Vec2 = Vec2(0f, 10f),
    private val velocityIterations: Int = 8,
    private val positionIterations: Int = 3,
    private val pixelsPerMeter: Float = 50f
) {

    private val factor = JBox2DFactor(pixelsPerMeter)
    val world = World(JVec2(gravity.x, gravity.y))

    /**
     * Realiza una iteración de la simulación física.
     */
    internal fun step(dt: Float) {
        world.step(dt, velocityIterations, positionIterations)
    }

    /**
     * Crea un cuerpo físico estático en el mundo.
     *
     * @param shape La forma del cuerpo (CircleShape o PolygonShape).
     * @param state El estado del cuerpo (posición, ángulo, escala, etc.).
     * @param type El tipo de cuerpo (Static, Dynamic, Kinematic).
     * @param material Los materiales físicos del cuerpo
     * @return El cuerpo creado.
     */
    fun createRigidBody(
        shape: Shape,
        state: TransformState,
        type: CollisionBodyType = CollisionBodyType.Dynamic,
        material: PhysicsMaterial = PhysicsMaterial()
    ): Body {
        val angleRadians = factor.degToRad(state.angle)
        val positionMeters = factor.toJBox2D(state.position)

        val bodyDef = BodyDef().apply {
            this@apply.type = factor.toJBox2D(type)
            this@apply.position.set(positionMeters)
            this@apply.angle = angleRadians
        }

        val body = world.createBody(bodyDef)
        val scaledShape = scaleShape(shape, state.scale)
        val jShape = factor.toJBox2D(scaledShape)

        val fixtureDef = FixtureDef().apply {
            this.shape = jShape
            this.density = material.density
            this.friction = material.friction
            this.restitution = material.restitution
        }

        body.createFixture(fixtureDef)
        return body
    }

    fun removeFixtureByUserdata(body: Body, shape: Shape) {
        val fixture = body.fixtureList
        while (fixture != null) {
            val nextFixture = fixture.next
            if (fixture.shape == shape) {
                body.destroyFixture(fixture)
            }
        }
    }

    fun getTransformState(body: Body): TransformState {
        val position = factor.toEngine(body.position)
        val angleDegrees = Math.toDegrees(body.angle.toDouble()).toFloat()
        return TransformState(position, angleDegrees, pivot = Pivot.Center)
    }

    /**
     * Aplica un impulso a un cuerpo para lanzarlo hacia un punto específico.
     *
     * @param body El cuerpo de Box2D que queremos mover (la bola).
     * @param target El punto al que queremos lanzar la bola.
     * @param force La magnitud de la fuerza a aplicar.
     * @param point El punto en el que se aplica el impulso.
     */
    fun applyImpulseTowards(
        body: Body,
        target: Vec2,
        force: Float = 40f,
        point: Vec2? = null
    ) {
        val targetXM = factor.pxToM(target.x)
        val targetYM = factor.pxToM(target.y)

        val bodyPos = body.position

        val dirX = targetXM - bodyPos.x
        val dirY = targetYM - bodyPos.y

        val length = sqrt((dirX * dirX + dirY * dirY).toDouble()).toFloat()

        if (length > 0) {
            val normal = Vec2(dirX / length, dirY / length)
            val impulse = factor.toJBox2D(normal * force)
            val jPoint = point?.let { factor.toJBox2D(it) }
            body.applyLinearImpulse(impulse, jPoint)
        }
    }

    /**
     * Aplica un impulso angular a un cuerpo.
     *
     * @param body El cuerpo de Box2D que queremos mover (la bola).
     * @param impulse La magnitud del impulso angular.
     */
    fun applyAngularImpulse(body: Body, impulse: Float) {
        body.applyAngularImpulse(impulse)
    }

    /**
     * Aplica un impulso lineal a un cuerpo.
     * @param body El cuerpo de Box2D que queremos mover (la bola).
     * @param impulse La magnitud del impulso lineal.
     * @param point El punto en el que se aplica el impulso.
     */
    fun applyLinearImpulse(body: Body, impulse: Vec2, point: Vec2? = null) {
        val jImpulse = factor.toJBox2D(impulse)
        val jPoint = point?.let { factor.toJBox2D(it) }
        body.applyLinearImpulse(jImpulse, jPoint)
    }

    /**
     * Aplica una fuerza a un cuerpo.
     * @param body El cuerpo de Box2D que queremos mover (la bola).
     * @param force La magnitud de la fuerza.
     * @param point El punto en el que se aplica la fuerza.
     */
    fun applyForce(body: Body, force: Vec2, point: Vec2? = null) {
        val jForce = factor.toJBox2D(force)
        val jPoint = point?.let { factor.toJBox2D(it) }
        jPoint?.let { body.applyForce(jForce, it) } ?: body.applyForceToCenter(jForce)
    }

    /**
     * Aplica un torque a un cuerpo.
     * @param body El cuerpo de Box2D que queremos mover (la bola).
     * @param torque La magnitud del torque.
     */
    fun applyTorque(body: Body, torque: Float) {
        body.applyTorque(torque)
    }

    /**
     * Detiene el movimiento de un cuerpo.
     * @param body El cuerpo que se desea detener.
     */
    fun stopBody(body: Body) {
        body.linearVelocity = JVec2(0f, 0f)
    }

    /**
     * Elimina un cuerpo específico del mundo físico.
     * @param body El cuerpo que se desea destruir.
     */
    fun destroyBody(body: Body) {
        world.destroyBody(body)
    }

    /**
     * Elimina todos los cuerpos físicos del mundo de Box2D.
     */
    internal fun clearAllObjects() {
        // Obtenemos el primer cuerpo de la lista enlazada
        var currentBody = world.bodyList

        while (currentBody != null) {
            // Guardamos el siguiente antes de destruir el actual
            val nextBody = currentBody.next

            // Le decimos a Box2D que lo elimine de la simulación
            world.destroyBody(currentBody)

            // Avanzamos al siguiente
            currentBody = nextBody
        }
    }

    /**
     * Escala un shape por un factor dado.
     * @param shape El shape original.
     * @param scale El factor de escala.
     * @return Un nuevo shape escalado.
     */
    fun scaleShape(shape: Shape, scale: Vec2): Shape {
        return when (shape) {
            is Shape.BoxShape -> Shape.BoxShape(shape.size * scale)
            is Shape.CircleShape -> {
                // Para círculos, usamos el promedio de la escala para mantenerlo circular
                val avgScale = (scale.x + scale.y) / 2f
                Shape.CircleShape(shape.radius * avgScale)
            }

            is Shape.PolygonShape -> Shape.PolygonShape(shape.vertices.map { it * scale })
            is Shape.ChainShape -> Shape.ChainShape(shape.vertices.map { it * scale })
            is Shape.EdgeShape -> Shape.EdgeShape(shape.start * scale, shape.end * scale)
        }
    }

    /**
     * Actualiza el fixture de un body con un nuevo shape escalado.
     * Esto destruye el fixture existente y crea uno nuevo.
     * @param body El body a actualizar.
     * @param newShape El nuevo shape escalado.
     * @param material Los materiales físicos del nuevo fixture.
     */
    fun updateBodyShape(body: Body, block: (body: Body) -> Body) {
        val body = block(body)
        val fixture = body.fixtureList
        if (fixture != null) {
            body.destroyFixture(fixture)
        }

    }

    fun updateBodyShape(
        body: Body,
        newShape: Shape,
        material: PhysicsMaterial = PhysicsMaterial()
    ) {
        // Destruir todos los fixtures existentes
        var fixture = body.fixtureList
        while (fixture != null) {
            val nextFixture = fixture.next
            body.destroyFixture(fixture)
            fixture = nextFixture
        }

        // Crear el nuevo fixture con el shape escalado
        val jShape = factor.toJBox2D(newShape)
        val fixtureDef = FixtureDef().apply {
            this.shape = jShape
            if (body.type == BodyType.DYNAMIC) {
                this.density = material.density
                this.friction = material.friction
                this.restitution = material.restitution
            }
        }

        body.createFixture(fixtureDef)
    }
}