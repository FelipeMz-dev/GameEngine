package com.mc.gameengine.engine.physics

import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.math.Vec2

sealed class Shape {
    val transform = TransformState()
    data class BoxShape(val size: Vec2): Shape()
    data class CircleShape(val radius: Float): Shape()
    data class PolygonShape(val vertices: List<Vec2>): Shape()
    data class ChainShape(val vertices: List<Vec2>): Shape()
    data class EdgeShape(val start: Vec2, val end: Vec2): Shape()
}