package com.mc.gameengine.game.instance

import androidx.compose.ui.graphics.Color
import com.mc.gameengine.engine.collision.CollisionBodyType
import com.mc.gameengine.engine.collision.PhysicsMaterial
import com.mc.gameengine.engine.core.GameObject
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.physics.RigidBody
import com.mc.gameengine.engine.physics.Shape
import com.mc.gameengine.engine.render.Renderer

class StackBlock(
    private val start: Vec2,
    private val size: Vec2,
    val density: Float,
    private val color: Color,
) : GameObject() {

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
            removeGameObject(this)
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
