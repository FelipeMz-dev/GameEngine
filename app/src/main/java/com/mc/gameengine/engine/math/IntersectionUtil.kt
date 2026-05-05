package com.mc.gameengine.engine.math

import com.mc.gameengine.engine.collision.BoxCollider
import com.mc.gameengine.engine.collision.CircleCollider
import com.mc.gameengine.engine.collision.EllipseCollider
import com.mc.gameengine.engine.collision.MaskCollider
import com.mc.gameengine.engine.collision.PolygonalCollider
import kotlin.collections.plus

internal object IntersectionUtil {

    fun circleCircle(a: CircleCollider, b: CircleCollider): Boolean {
        val dist = (a.getCenter() - b.getCenter()).length()
        return dist <= a.radius + b.radius
    }

    fun circleBox(circle: CircleCollider, box: BoxCollider): Boolean {
        val circleCenter = circle.getCenter()
        val boxCenter = box.getCenter()

        var localCircle = (circleCenter - boxCenter).rotate(-box.state.angle)
        localCircle = Vec2(localCircle.x / box.state.scale.x, localCircle.y / box.state.scale.y)

        val hw = box.width / 2f
        val hh = box.height / 2f

        val closestLocal = Vec2(
            localCircle.x.coerceIn(-hw, hw),
            localCircle.y.coerceIn(-hh, hh)
        )

        val closestRotated = closestLocal.rotate(box.state.angle)
        val closestWorld = boxCenter + Vec2(
            closestRotated.x * box.state.scale.x,
            closestRotated.y * box.state.scale.y
        )

        return (closestWorld - circleCenter).length() <= circle.radius
    }

    fun circleOval(circle: CircleCollider, oval: EllipseCollider): Boolean {
        val centerDiff = oval.getCenter() - circle.getCenter()
        val local = centerDiff.rotate(-oval.state.angle)

        val rx = oval.radius.x * oval.state.scale.x
        val ry = oval.radius.y * oval.state.scale.y

        return (local.x * local.x) / (rx * rx) + (local.y * local.y) / (ry * ry) <= 1f
    }

    fun circlePolygon(circle: CircleCollider, polygon: PolygonalCollider): Boolean {
        val circleCenter = circle.getCenter()

        val transformed = polygon.getVertices().map { v ->
            val p = (v - circleCenter).rotate(-polygon.state.angle)
            Vec2(
                x = p.x / (polygon.state.scale.x * circle.radius),
                y = p.y / (polygon.state.scale.y * circle.radius)
            )
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

    fun circleMask(circle: CircleCollider, mask: MaskCollider): Boolean {
        val buffer = mask.getBuffer() ?: return false
        val circleCenter = circle.getCenter()

        buffer.forEach {
            if (it.distanceSquaredTo(circleCenter) <= circle.radius * circle.radius) return true
        }
        return false
    }

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
            val pb = project(boxVertices, axis)
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

    fun polygonMask(polygon: PolygonalCollider, mask: MaskCollider): Boolean {
        val buffer = mask.getBuffer() ?: return false
        val polygonVertices = polygon.getVertices().ifEmpty { return false }

        buffer.forEach {
            if (pointInPolygon(it, polygonVertices)) return true
        }
        return false
    }

    fun boxMask(box: BoxCollider, mask: MaskCollider): Boolean {
        val buffer = mask.getBuffer() ?: return false
        val boxVertices = box.getVertices().ifEmpty { return false }

        buffer.forEach {
            if (pointInPolygon(it, boxVertices)) return true
        }
        return false
    }

    fun ovalMask(oval: EllipseCollider, mask: MaskCollider): Boolean {
        val buffer = mask.getBuffer() ?: return false
        val ovalCenter = oval.getCenter()

        buffer.forEach {
            val p = (it - ovalCenter).rotate(-oval.state.angle)
            val dx = p.x / (oval.radius.x * oval.state.scale.x)
            val dy = p.y / (oval.radius.y * oval.state.scale.y)
            if ((dx * dx) + (dy * dy) <= 1f) return true
        }
        return false
    }

    fun maskMask(a: MaskCollider, b: MaskCollider): Boolean {
        val bufferA = a.getBuffer() ?: return false
        val bufferB = b.getBuffer() ?: return false

        bufferA.forEachIndexed { indexA, a ->
            if (indexA % 2 == 0) bufferB.forEachIndexed { indexB, b ->
                if (indexB % 2 == 0) if (a.distanceSquaredTo(b) <= 2f) return true
            }
        }
        return false
    }

    private fun pointInPolygon(point: Vec2, verts: List<Vec2>): Boolean {
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