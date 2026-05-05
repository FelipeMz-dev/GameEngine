package com.mc.gameengine.engine.collision

import androidx.compose.ui.graphics.Color
import com.mc.gameengine.engine.compose.RenderDepth
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.math.AABB
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.render.Renderer

class CircleCollider(
    override val owner: Instance,
    val radius: Float
) : Collider(owner), CollisionCenterDelegate by CollisionCenterDelegateImpl() {

    override fun Renderer.debugDraw() {
        val diameter = radius * 2
        drawOval(
            size = Vec2(diameter, diameter),
            state = state,
            color = Color.Gray.copy(alpha = 0.5f),
            deep = RenderDepth.DEBUG
        )
    }

    override fun onUpdateAABB(): AABB {
        val center = getCenter()
        val rx = radius * state.scale.x
        val ry = radius * state.scale.y

        return AABB(
            x = center.x - rx,
            y = center.y - ry,
            width = rx * 2,
            height = ry * 2
        )
    }

    override fun onUpdate() {
        clearCenter()
    }

    override fun getCenter() = computeCenter()

    override fun getSize(): Vec2 = Vec2(radius * 2, radius * 2)

}