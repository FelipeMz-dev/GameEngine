package com.mc.gameengine.game.instance

import com.mc.engine.core.Instance
import com.mc.engine.core.TransformState
import com.mc.engine.math.Vec2
import com.mc.engine.physics.CollisionEvent
import com.mc.engine.physics.CollisionListener
import com.mc.engine.physics.CollisionPhase
import com.mc.engine.physics.PhysicsMaterial
import com.mc.engine.physics.RigidBody
import com.mc.engine.physics.Shape
import com.mc.engine.render.Renderer
import com.mc.gameengine.game.instance.spec.BallSpec

class ProjectileBall(
    private val start: Vec2,
    private val target: Vec2,
    private val velocity: Float,
    private val spec: BallSpec,
) : Instance(), CollisionListener {

    private var livedSeconds = 0f
    private lateinit var ball: RigidBody

    override fun onEnterScene() {
        ball = createRigidBody(
            shape = Shape.CircleShape(spec.radius),
            state = TransformState(position = start),
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

    override fun onCollision(event: CollisionEvent) {
        if (event.phase == CollisionPhase.Enter) println(event.details)
    }
}