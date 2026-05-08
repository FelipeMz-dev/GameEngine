package com.mc.gameengine.engine.collision

import androidx.compose.ui.graphics.Color
import com.mc.gameengine.engine.compose.RenderDepth
import com.mc.gameengine.engine.core.GameObject
import com.mc.gameengine.engine.math.AABB
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.render.Renderer

open class PolygonalCollider(
    override val owner: GameObject,
    val points: List<Vec2>,
) : Collider(owner),
    CollisionCenterDelegate by CollisionCenterDelegateImpl(),
    CollisionVerticesDelegate by CollisionVerticesDelegateImpl()
{

    private val minX = points.minOf { it.x }
    private val minY = points.minOf { it.y }
    private val maxX = points.maxOf { it.x }
    private val maxY = points.maxOf { it.y }

    val localOrigin = Vec2(minX, minY)
    val localSize = Vec2(maxX - minX, maxY - minY)

    override fun Renderer.debugDraw() {
        drawPolygon(
            points = getVertices(),
            state = state,
            color = Color.Gray.copy(alpha = 0.5f),
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

    override fun getSize(): Vec2 = localSize

}