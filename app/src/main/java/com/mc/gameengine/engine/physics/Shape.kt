package com.mc.gameengine.engine.physics

import com.mc.gameengine.engine.math.Vec2

sealed class Shape {
    data class BoxShape(val size: Vec2): Shape()
    data class CircleShape(val radius: Float): Shape()
    data class EllipseShape(val radius: Vec2): Shape()
    data class PolygonShape(val vertices: List<Vec2>): Shape()
    data class SegmentShape(val start: Vec2, val end: Vec2): Shape()
    data class CapsuleShape(val size: Vec2): Shape()
    data class SliceShape(val radius: Float, val theta: Float): Shape()
}