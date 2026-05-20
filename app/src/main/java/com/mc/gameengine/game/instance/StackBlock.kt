package com.mc.gameengine.game.instance

import androidx.compose.ui.graphics.Color
import com.mc.engine.core.Instance
import com.mc.engine.core.TransformState
import com.mc.engine.math.Vec2
import com.mc.engine.physics.CollisionBodyType
import com.mc.engine.physics.PhysicsMaterial
import com.mc.engine.physics.RigidBody
import com.mc.engine.physics.Shape
import com.mc.engine.render.Renderer

class StackBlock(
    private val start: Vec2,
    private val size: Vec2,
    val density: Float,
    private val color: Color,
) : Instance() {

    private lateinit var block: RigidBody

    override fun onEnterScene() {
        block = createRigidBody(
            shape = Shape.BoxShape(size),
            state = TransformState(position = start),
            type = CollisionBodyType.Dynamic,
            material = PhysicsMaterial(
                density = density,
                friction = 0.8f,
                restitution = 0.5f
            )
        )
    }

    override fun fixedUpdate(dt: Float) {
        if (!::block.isInitialized) return
        current = block.transformState
        if (isOutOfScreen()) {
            deleteInstance(this)
        }
    }

    private fun isOutOfScreen(): Boolean {
        val margin = 180f
        val view = viewport().size
        return current.position.x < -margin ||
                current.position.x > view.x + margin ||
                current.position.y < -margin ||
                current.position.y > view.y + margin
    }

    override fun Renderer.onRender(state: TransformState) {
        if (!::block.isInitialized) return
        drawRect(
            size = size,
            state = current,
            color = color
        )
    }
}
