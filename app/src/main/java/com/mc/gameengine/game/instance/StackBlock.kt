package com.mc.gameengine.game.instance

import androidx.compose.ui.graphics.Color
import com.mc.gameengine.engine.collision.CollisionBodyType
import com.mc.gameengine.engine.collision.PhysicsMaterial
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.physics.Shape
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer

class StackBlock(
    private val start: Vec2,
    private val size: Vec2,
    val density: Float,
    private val color: Color,
) : Instance() {

    override fun onEnterScene() {
        current = current.copy(position = start, pivot = Pivot.Center)
        // Crear cuerpo JBox2D dinámico
        val shape = Shape.BoxShape(size)
        createRigidBody(
            shape = shape,
            state = current,
            type = CollisionBodyType.Dynamic,
            material = PhysicsMaterial(
                density = density,
                friction = 0.8f,
                restitution = 0.5f
            )
        )
    }

    override fun fixedUpdate(dt: Float) {
        if  (isOutOfScreen()) {
            deleteInstance(this)
        }
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
        drawRect(
            size = size,
            state = state,
            color = color
        )
    }
}
