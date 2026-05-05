package com.mc.gameengine.engine.collision

import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.minus
import com.mc.gameengine.engine.math.plus
import com.mc.gameengine.engine.math.resolve
import com.mc.gameengine.engine.math.rotate
import com.mc.gameengine.engine.math.times
import com.mc.gameengine.engine.render.Pivot

internal interface CollisionCenterDelegate {
    fun Collider.computeCenter(): Vec2
    fun clearCenter()
}

internal class CollisionCenterDelegateImpl : CollisionCenterDelegate {
    private var cachedCenter: Vec2? = null

    override fun Collider.computeCenter(): Vec2 {
        if (state.pivot is Pivot.Center && this !is PolygonalCollider) return state.position
        if (cachedCenter == null) when (this) {
            is EllipseCollider -> updateCenter()
            is BoxCollider -> updateCenter()
            is PolygonalCollider -> updateCenter()
            is CircleCollider -> updateCenter()
        }
        return cachedCenter ?: state.position
    }

    override fun clearCenter() {
        cachedCenter = null
    }

    private fun EllipseCollider.updateCenter() {
        val size = Vec2(width, height) * state.scale
        val localCenter = Vec2(size.x / 2f, size.y / 2f)
        val pivotOffset = state.pivot.resolve(size)
        cachedCenter = (localCenter - pivotOffset).rotate(state.angle) + state.position
    }

    private fun CircleCollider.updateCenter() {
        val size = state.scale * (radius * 2f)
        val localCenter = Vec2(size.x / 2f, size.y / 2f)
        val pivotOffset = state.pivot.resolve(size)
        cachedCenter = (localCenter - pivotOffset) + state.position
    }

    private fun BoxCollider.updateCenter() {
        val size = Vec2(width, height) * state.scale
        val localCenter = Vec2(size.x / 2f, size.y / 2f)
        val pivotOffset = state.pivot.resolve(size)
        cachedCenter = (localCenter - pivotOffset).rotate(state.angle) + state.position
    }

    private fun PolygonalCollider.updateCenter() {
        if (points.isEmpty()) return
        val minX = points.minOf { it.x }
        val minY = points.minOf { it.y }
        val maxX = points.maxOf { it.x }
        val maxY = points.maxOf { it.y }

        val size = Vec2(maxX - minX, maxY - minY) * state.scale
        val localCenter = Vec2(size.x / 2f, size.y / 2f)
        val pivotOffset = state.pivot.resolve(size)
        cachedCenter = (localCenter - pivotOffset).rotate(state.angle) + state.position
    }
}