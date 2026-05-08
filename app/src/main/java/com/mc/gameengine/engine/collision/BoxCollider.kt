package com.mc.gameengine.engine.collision

import androidx.compose.ui.graphics.Color
import com.mc.gameengine.engine.compose.RenderDepth
import com.mc.gameengine.engine.core.GameObject
import com.mc.gameengine.engine.math.AABB
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.render.Renderer

open class BoxCollider(
    override val owner: GameObject,
    val width: Float,
    val height: Float,
) : Collider(owner),
    CollisionCenterDelegate by CollisionCenterDelegateImpl(),
    CollisionVerticesDelegate by CollisionVerticesDelegateImpl() {

    override fun Renderer.debugDraw() {
        drawPolygon(
            points = getVertices(),
            color = Color.Gray.copy(alpha = 0.5f),
            state = state,
            deep = RenderDepth.DEBUG
        )
    }

    override fun onUpdateAABB(): AABB {
        val vertices = getVertices()
        val minX = vertices.minOf { it.x }
        val minY = vertices.minOf { it.y }
        val maxX = vertices.maxOf { it.x }
        val maxY = vertices.maxOf { it.y }
        return AABB(
            x = minX,
            y = minY,
            width = maxX - minX,
            height = maxY - minY
        )
    }

    override fun onUpdate() {
        clearVertices()
        clearCenter()
    }

    fun getVertices(): List<Vec2> = computeVertices()

    fun getAxes(): List<Vec2> = computeAxes()

    override fun getCenter() = computeCenter()

    override fun getSize(): Vec2 = Vec2(width, height)

}