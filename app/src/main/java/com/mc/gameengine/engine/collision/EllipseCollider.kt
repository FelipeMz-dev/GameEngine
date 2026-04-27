package com.mc.gameengine.engine.collision

import androidx.compose.ui.graphics.Color
import com.mc.gameengine.engine.compose.RenderDepth
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.math.AABB
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.render.Renderer

open class EllipseCollider(
    override val owner: Instance,
    val width: Float,
    val height: Float,
): Collider(owner), CollisionCenterDelegate by CollisionCenterDelegateImpl() {

    val radius: Vec2 = Vec2(width / 2, height / 2)

    override fun Renderer.debugDraw() {
        drawOval(
            size = Vec2(width, height),
            state = state,
            color = Color.Gray.copy(alpha = 0.5f),
            deep = RenderDepth.DEBUG
        )
    }

    override fun onUpdateAABB(): AABB {
        val center = getCenter()
        val rx = radius.x * state.scale.x
        val ry = radius.y * state.scale.y

        val angleRad = Math.toRadians(state.angle.toDouble()).toFloat()
        val cos = kotlin.math.cos(angleRad)
        val sin = kotlin.math.sin(angleRad)
        val ex = kotlin.math.sqrt(rx * rx * cos * cos + ry * ry * sin * sin)
        val ey = kotlin.math.sqrt(rx * rx * sin * sin + ry * ry * cos * cos)
        val minX = center.x - ex
        val minY = center.y - ey
        val maxX = center.x + ex
        val maxY = center.y + ey

        return AABB(
            x = minX,
            y = minY,
            width = maxX - minX,
            height = maxY - minY
        )
    }

    override fun onUpdate() {
        clearCenter()
    }

    fun getCenter() = computeCenter()

}