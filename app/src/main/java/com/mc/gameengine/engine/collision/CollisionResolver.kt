package com.mc.gameengine.engine.collision

import com.mc.gameengine.engine.math.IntersectionUtil

object CollisionResolver {

    fun test(a: Collider, b: Collider): Boolean = when (a) {
        is EllipseCollider -> when (b) {
            is EllipseCollider -> IntersectionUtil.ovalOval(a, b)
            is BoxCollider -> IntersectionUtil.boxOval(b, a)
            is PolygonalCollider -> IntersectionUtil.polygonOval(a, b)
            is MaskCollider -> IntersectionUtil.ovalMask(a, b)
            else -> false
        }

        is BoxCollider -> when (b) {
            is EllipseCollider -> IntersectionUtil.boxOval(a, b)
            is BoxCollider -> IntersectionUtil.boxBox(a, b)
            is PolygonalCollider -> IntersectionUtil.polygonBox(a, b)
            is MaskCollider -> IntersectionUtil.boxMask(a, b)
            else -> false
        }

        is PolygonalCollider -> when (b) {
            is EllipseCollider -> IntersectionUtil.polygonOval(b, a)
            is BoxCollider -> IntersectionUtil.polygonBox(b, a)
            is PolygonalCollider -> IntersectionUtil.polygonPolygon(a, b)
            is MaskCollider -> IntersectionUtil.polygonMask(a, b)
            else -> false
        }

        is MaskCollider -> when (b) {
            is PolygonalCollider -> IntersectionUtil.polygonMask(b, a)
            is BoxCollider -> IntersectionUtil.boxMask(b, a)
            is EllipseCollider -> IntersectionUtil.ovalMask(b, a)
            is MaskCollider -> IntersectionUtil.maskMask(a, b)
            else -> false
        }

        else -> false
    }
}