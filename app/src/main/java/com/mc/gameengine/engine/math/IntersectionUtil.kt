package com.mc.gameengine.engine.math

import com.mc.gameengine.engine.collision.BoxCollider
import com.mc.gameengine.engine.collision.EllipseCollider
import com.mc.gameengine.engine.collision.PolygonalCollider
import kotlin.collections.plus

internal class IntersectionUtil {

    fun ovalOval(a: EllipseCollider, b: EllipseCollider): Boolean {
        val centerDiff = b.getCenter() - a.getCenter()

        var local = centerDiff.rotate(-a.state.angle)
        local = Vec2(local.x / a.state.scale.x, local.y / a.state.scale.y)

        val rx = a.radius.x + b.radius.x * (b.state.scale.x / a.state.scale.x)
        val ry = a.radius.y + b.radius.y * (b.state.scale.y / a.state.scale.y)

        return (local.x * local.x) / (rx * rx) + (local.y * local.y) / (ry * ry) <= 1f
    }

    fun boxOval(box: BoxCollider, oval: EllipseCollider): Boolean {
        val boxCenter = box.getCenter()
        val ovalCenter = oval.getCenter()

        var localCenter = (ovalCenter - boxCenter).rotate(-box.state.angle)
        localCenter = Vec2(localCenter.x / box.state.scale.x, localCenter.y / box.state.scale.y)

        val hw = box.width / 2f
        val hh = box.height / 2f

        val closestLocal = Vec2(
            localCenter.x.coerceIn(-hw, hw),
            localCenter.y.coerceIn(-hh, hh)
        )

        val closestRotated = closestLocal.rotate(box.state.angle)
        val closestWorld = boxCenter + Vec2(
            closestRotated.x * box.state.scale.x,
            closestRotated.y * box.state.scale.y
        )

        val v = (closestWorld - ovalCenter).rotate(-oval.state.angle)
        val dx = v.x
        val dy = v.y

        val ovalScaled = oval.radius * oval.state.scale

        return (dx * dx) / (ovalScaled.x * ovalScaled.x) + (dy * dy) / (ovalScaled.y * ovalScaled.y) <= 1f
    }

    fun boxBox(a: BoxCollider, b: BoxCollider): Boolean {
        val axes = a.getAxes() + b.getAxes()

        val va = a.getVertices()
        val vb = b.getVertices()

        for (axis in axes) {
            val pa = project(va, axis)
            val pb = project(vb, axis)
            if (!overlap(pa, pb)) return false
        }
        return true
    }

    fun polygonPolygon(a: PolygonalCollider, b: PolygonalCollider): Boolean {
        val axes = a.getAxes() + b.getAxes()

        val verticesA = a.getVertices()
        val verticesB = b.getVertices()
        if (verticesA.isEmpty() || verticesB.isEmpty()) return false

        for (axis in axes) {
            val pa = project(verticesA, axis)
            val pb = project(verticesB, axis)
            if (!overlap(pa, pb)) return false
        }
        return true
    }

    fun polygonBox(box: BoxCollider, polygon: PolygonalCollider): Boolean {
        val axes = box.getAxes() + polygon.getAxes()

        val boxVertices = box.getVertices()
        val polygonVertices = polygon.getVertices()
        if (polygonVertices.isEmpty() || boxVertices.isEmpty()) return false

        for (axis in axes) {
            val pa = project(polygonVertices, axis)
            val pb = project(box.getVertices(), axis)
            if (!overlap(pa, pb)) return false
        }
        return true
    }

    fun polygonOval(oval: EllipseCollider, polygon: PolygonalCollider): Boolean {
        val polygonVertices = polygon.getVertices()
        if (polygonVertices.isEmpty()) return false

        val ovalCenter = oval.getCenter()

        val transformed = polygonVertices.map { v ->
            val p = (v - ovalCenter).rotate(-oval.state.angle)
            Vec2(
                x = p.x / (oval.radius.x * oval.state.scale.x),
                y = p.y / (oval.radius.y * oval.state.scale.y)
            )
        }

        fun pointInPolygon(point: Vec2, verts: List<Vec2>): Boolean {
            var inside = false
            var j = verts.size - 1
            for (i in verts.indices) {
                val vi = verts[i]
                val vj = verts[j]
                if (((vi.y > point.y) != (vj.y > point.y)) &&
                    (point.x < (vj.x - vi.x) * (point.y - vi.y) / (vj.y - vi.y) + vi.x)
                ) {
                    inside = !inside
                }
                j = i
            }
            return inside
        }

        if (pointInPolygon(Vec2.Zero, transformed)) return true

        var minDistSq = Float.MAX_VALUE
        for (i in transformed.indices) {
            val a = transformed[i]
            val b = transformed[(i + 1) % transformed.size]
            val ab = b - a
            val abLenSq = ab.x * ab.x + ab.y * ab.y
            val t = if (abLenSq == 0f) 0f else ((-a.dot(ab)) / abLenSq).coerceIn(0f, 1f)
            val closest = Vec2(a.x + ab.x * t, a.y + ab.y * t)
            val distSq = closest.x * closest.x + closest.y * closest.y
            if (distSq < minDistSq) minDistSq = distSq
        }

        return minDistSq <= 1f
    }

    private fun overlap(a: Projection, b: Projection): Boolean = a.max >= b.min && b.max >= a.min

    private fun project(points: List<Vec2>, axis: Vec2): Projection {
        var min = points[0].dot(axis)
        var max = min
        for (i in 1 until points.size) {
            val p = points[i].dot(axis)
            min = kotlin.math.min(min, p)
            max = kotlin.math.max(max, p)
        }
        return Projection(min, max)
    }
}