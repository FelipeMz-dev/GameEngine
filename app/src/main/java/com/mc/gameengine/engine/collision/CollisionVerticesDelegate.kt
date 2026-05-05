package com.mc.gameengine.engine.collision

import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.minus
import com.mc.gameengine.engine.math.normalize
import com.mc.gameengine.engine.math.perp
import com.mc.gameengine.engine.math.plus
import com.mc.gameengine.engine.math.resolve
import com.mc.gameengine.engine.math.rotate
import com.mc.gameengine.engine.math.times

internal interface CollisionVerticesDelegate {
    fun Collider.computeVertices(): List<Vec2>
    fun Collider.computeAxes(): List<Vec2>
    fun clearVertices()
}

internal class CollisionVerticesDelegateImpl internal constructor(): CollisionVerticesDelegate {
    private var cachedVertices: List<Vec2>? = null
    private var cachedAxes: List<Vec2>? = null

    override fun Collider.computeVertices(): List<Vec2> {
        if (cachedVertices == null) when (this) {
            is BoxCollider -> updateVertices()
            is PolygonalCollider -> updateVertices()
            is MaskCollider -> updateVertices()
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
        val scaledSize = Vec2(width, height) * state.scale
        val pivotOffset = state.pivot.resolve(scaledSize)

        val topLeft = Vec2(0f, 0f)
        val topRight = Vec2(scaledSize.x, 0f)
        val bottomLeft = Vec2(0f, scaledSize.y)
        val bottomRight = Vec2(scaledSize.x, scaledSize.y)

        cachedVertices = listOf(topLeft, topRight, bottomRight, bottomLeft).map { local ->
            val rotated = (local - pivotOffset).rotate(state.angle)
            rotated + state.position
        }
    }

    private fun MaskCollider.updateVertices() {
        val scaledSize = getSize() * state.scale
        val pivotOffset = state.pivot.resolve(scaledSize)

        val topLeft = Vec2(0f, 0f)
        val topRight = Vec2(scaledSize.x, 0f)
        val bottomLeft = Vec2(0f, scaledSize.y)
        val bottomRight = Vec2(scaledSize.x, scaledSize.y)

        cachedVertices = listOf(topLeft, topRight, bottomRight, bottomLeft).map { local ->
            val rotated = (local - pivotOffset).rotate(state.angle)
            rotated + state.position
        }
    }

    private fun PolygonalCollider.updateVertices() {
        if (points.isEmpty()) return
        val scaledSize = localSize * state.scale
        val pivotOffset = state.pivot.resolve(scaledSize)

        cachedVertices = points.map { local ->
            val localRel = (local - localOrigin) * state.scale
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