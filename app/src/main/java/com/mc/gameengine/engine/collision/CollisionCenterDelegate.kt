package com.mc.gameengine.engine.collision

import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.minus
import com.mc.gameengine.engine.math.normalize
import com.mc.gameengine.engine.math.perp
import com.mc.gameengine.engine.math.plus
import com.mc.gameengine.engine.math.resolve
import com.mc.gameengine.engine.math.rotate
import com.mc.gameengine.engine.math.times
import com.mc.gameengine.engine.render.Pivot
import kotlin.collections.plusAssign

interface CollisionCenterDelegate {
    fun Collider.computeCenter(): Vec2
    fun clearCenter()
}

class CollisionCenterDelegateImpl() : CollisionCenterDelegate {
    private var cachedCenter: Vec2? = null

    override fun Collider.computeCenter(): Vec2 {
        if (state.pivot is Pivot.Center && this !is PolygonalCollider) return state.position
        if (cachedCenter == null) when (this) {
            is EllipseCollider -> updateCenter()
            is BoxCollider -> updateCenter()
            is PolygonalCollider -> updateCenter()
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

interface CollisionVerticesDelegate {
    fun Collider.computeVertices(): List<Vec2>
    fun Collider.computeAxes(): List<Vec2>
    fun clearVertices()
}

class CollisionVerticesDelegateImpl : CollisionVerticesDelegate {
    private var cachedVertices: List<Vec2>? = null
    private var cachedAxes: List<Vec2>? = null

    override fun Collider.computeVertices(): List<Vec2> {
        if (cachedVertices == null) when (this) {
            is BoxCollider -> updateVertices()
            is PolygonalCollider -> updateVertices()
        }
        return cachedVertices.orEmpty()
    }

    override fun Collider.computeAxes(): List<Vec2> {
        if (cachedAxes == null) when (this) {
            is BoxCollider -> updateAxes()
            is PolygonalCollider -> updateAxes()
        }
        return cachedAxes.orEmpty()
    }

    override fun clearVertices() {
        cachedVertices = null
        cachedAxes = null
    }

    private fun BoxCollider.updateVertices() {
        val size = Vec2(width, height) * state.scale
        val pivotOffset = state.pivot.resolve(size)

        val topLeft = Vec2(0f, 0f)
        val topRight = Vec2(size.x, 0f)
        val bottomLeft = Vec2(0f, size.y)
        val bottomRight = Vec2(size.x, size.y)

        cachedVertices = listOf(topLeft, topRight, bottomRight, bottomLeft).map { local ->
            val rotated = (local - pivotOffset).rotate(state.angle)
            rotated + state.position
        }
    }

    private fun PolygonalCollider.updateVertices() {
        if (points.isEmpty()) return
        val minX = points.minOf { it.x }
        val minY = points.minOf { it.y }
        val maxX = points.maxOf { it.x }
        val maxY = points.maxOf { it.y }

        val origin = Vec2(minX, minY)
        val size = Vec2(maxX - minX, maxY - minY) * state.scale
        val pivotOffset = state.pivot.resolve(size)

        cachedVertices = points.map { local ->
            val localRel = (local - origin) * state.scale
            val rotated = (localRel - pivotOffset).rotate(state.angle)
            rotated + state.position
        }
    }

    private fun BoxCollider.updateAxes() {
        val vertices = getVertices()
        cachedAxes = listOf(
            (vertices[1] - vertices[0]).perp().normalize(),
            (vertices[3] - vertices[0]).perp().normalize()
        )
    }

    private fun PolygonalCollider.updateAxes() {
        val axes = mutableListOf<Vec2>()
        val vertices = getVertices()
        for (i in vertices.indices) {
            val p1 = vertices[i]
            val p2 = vertices[(i + 1) % vertices.size]
            axes += (p2 - p1).perp().normalize()
        }
        cachedAxes = axes
    }
}