package com.mc.gameengine.game.instance

import com.mc.gameengine.engine.collision.CollisionBodyType
import com.mc.gameengine.engine.collision.PhysicsMaterial
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.physics.Shape
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer
import com.mc.gameengine.game.instance.spec.BallSpec

class ProjectileBall(
    private val start: Vec2,
    private val target: Vec2,
    private val velocity: Float,
    private val spec: BallSpec,
) : Instance() {

    private var livedSeconds = 0f

    override fun onEnterScene() {
        current = current.copy(position = start, pivot = Pivot.TopLeft)
        // Crear cuerpo JBox2D dinámico
        val shape = Shape.CircleShape(spec.radius)
        createRigidBody(
            shape = shape,
            state = current,
            type = CollisionBodyType.Dynamic,
            material = PhysicsMaterial(
                density = spec.density,
                friction = 0.4f,
                restitution = 0.5f
            )
        )
        applyImpulseTowards(target, velocity)
    }

    override fun fixedUpdate(dt: Float) {
        if (livedSeconds >= 15f || isOutOfScreen()) {
            deleteInstance(this)
        }
    }

    override fun update(dt: Float) {
        livedSeconds += dt
    }

    private fun isOutOfScreen(): Boolean {
        val margin = 180f
        val view = viewportSize()
        return current.position.x < -margin ||
            current.position.x > view.x + margin ||
            current.position.y < -margin ||
            current.position.y > view.y + margin
    }

    override fun Renderer.onRender(state: TransformState) {
        drawCircle(
            radius = spec.radius,
            state = state,
            color = spec.color
        )
    }
}