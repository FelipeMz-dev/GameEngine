package com.mc.gameengine.engine.collision

import androidx.compose.ui.graphics.Color
import com.mc.gameengine.engine.compose.RenderDepth
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.render.Renderer

open class EllipseCollider(
    override val owner: Instance,
    val width: Float,
    val height: Float,
): Collider(owner), CollisionCenterDelegate by CollisionCenterDelegateImpl() {

    val radius: Vec2 = Vec2(width / 2, height / 2)

    override var state: ColliderState = ColliderState()

    override fun Renderer.debugDraw() {
        drawOval(
            position = state.position,
            size = Vec2(width, height),
            angle = state.angle,
            scale = state.scale,
            pivot = state.pivot,
            color = Color.Gray.copy(alpha = 0.5f),
            deep = RenderDepth.DEBUG
        )
    }

    override fun onUpdate() {
        clearCenter()
    }

    fun getCenter() = computeCenter()

}