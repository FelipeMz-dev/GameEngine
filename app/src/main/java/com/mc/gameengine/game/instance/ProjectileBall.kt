package com.mc.gameengine.game.instance

import com.mc.gameengine.engine.collision.CollisionBodyType
import com.mc.gameengine.engine.collision.PhysicsMaterial
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.physics.RigidBody
import com.mc.gameengine.engine.physics.Shape
import com.mc.gameengine.engine.render.Renderer
import com.mc.gameengine.game.instance.spec.BallSpec

class ProjectileBall(
    private val start: Vec2,
    private val target: Vec2,
    private val velocity: Float,
    private val spec: BallSpec,
) : Instance() {

    private var livedSeconds = 0f
    private lateinit var ball: RigidBody

    override fun onEnterScene() {
        ball = createRigidBody(
            shape = Shape.CircleShape(spec.radius),
            state = TransformState(position = start),
            type = CollisionBodyType.Dynamic,
            material = PhysicsMaterial(
                density = spec.density,
                friction = 0.4f,
                restitution = 0.5f
            )
        )
        ball.applyImpulseTowards(target, velocity)
    }

    override fun fixedUpdate(dt: Float) {
        if (!::ball.isInitialized) return
        if (livedSeconds >= 15f || isOutOfScreen()) {
            deleteInstance(this)
        }
    }

    override fun update(dt: Float) {
        livedSeconds += dt
    }

    private fun isOutOfScreen(): Boolean {
        if (!::ball.isInitialized) return false
        val pos = ball.transformState.position
        val margin = 180f
        val view = viewport().size
        return pos.x < -margin || pos.x > view.x + margin || pos.y < -margin || pos.y > view.y + margin
    }

    override fun Renderer.onRender(state: TransformState) {
        if (!::ball.isInitialized) return
        drawCircle(
            radius = spec.radius,
            state = ball.transformState,
            color = spec.color
        )
    }
}