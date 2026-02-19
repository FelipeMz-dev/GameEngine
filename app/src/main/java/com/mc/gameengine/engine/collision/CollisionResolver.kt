package com.mc.gameengine.engine.collision

import com.mc.gameengine.engine.math.IntersectionUtil

object CollisionResolver {

    private val intersector = IntersectionUtil()

    fun test(a: Collider, b: Collider): Boolean = when (a) {
        is EllipseCollider -> when (b) {
            is EllipseCollider -> intersector.ovalOval(a, b)
            is BoxCollider -> intersector.boxOval(b, a)
            is PolygonalCollider -> intersector.polygonOval(a, b)
            else -> false
        }

        is BoxCollider -> when (b) {
            is EllipseCollider -> intersector.boxOval(a, b)
            is BoxCollider -> intersector.boxBox(a, b)
            is PolygonalCollider -> intersector.polygonBox(a, b)
            else -> false
        }

        is PolygonalCollider -> when (b) {
            is EllipseCollider -> intersector.polygonOval(b, a)
            is BoxCollider -> intersector.polygonBox(b, a)
            is PolygonalCollider -> intersector.polygonPolygon(a, b)
            else -> false
        }

        else -> false
    }
}