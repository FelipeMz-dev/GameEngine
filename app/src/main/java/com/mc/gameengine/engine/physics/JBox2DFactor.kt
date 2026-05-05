package com.mc.gameengine.engine.physics

import com.mc.gameengine.engine.collision.CollisionBodyType
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.div
import org.jbox2d.common.Transform
import org.jbox2d.dynamics.BodyType
import org.jbox2d.common.Vec2 as JVec2
import org.jbox2d.collision.shapes.Shape as JShape

internal class JBox2DFactor(private var pixelsPerMeter: Float) {

    fun pxToM(px: Float): Float = px / pixelsPerMeter
    fun mToPx(m: Float): Float = m * pixelsPerMeter

    fun toJBox2D(vec2: Vec2): JVec2 = JVec2(pxToM(vec2.x), pxToM(vec2.y))
    fun toEngine(jVec2: JVec2): Vec2 = Vec2(mToPx(jVec2.x), mToPx(jVec2.y))

    fun radToDeg(rad: Float): Float = rad * 180 / Math.PI.toFloat()
    fun degToRad(deg: Float): Float = deg * Math.PI.toFloat() / 180

    fun toJBox2D(bodyType: CollisionBodyType) = when (bodyType) {
        CollisionBodyType.Static -> BodyType.STATIC
        CollisionBodyType.Dynamic -> BodyType.DYNAMIC
        CollisionBodyType.Kinematic -> BodyType.KINEMATIC
    }

    fun toJBox2D(shape: Shape): JShape {
        return when (shape) {
            is Shape.BoxShape -> {
                val halfSize = shape.size / 2f
                val jSize = toJBox2D(halfSize)
                val polygonShape = org.jbox2d.collision.shapes.PolygonShape()
                polygonShape.setAsBox(jSize.x, jSize.y)
                polygonShape.m_centroid = toJBox2D(shape.transform.position)
                polygonShape
            }

            is Shape.CircleShape -> {
                val circleShape = org.jbox2d.collision.shapes.CircleShape()
                circleShape.m_radius = pxToM(circleShape.radius)
                circleShape
            }

            is Shape.PolygonShape -> {
                val jVertices = shape.vertices.map { toJBox2D(it) }.toTypedArray()
                val polygonShape = org.jbox2d.collision.shapes.PolygonShape()
                polygonShape.set(jVertices, jVertices.size)
                polygonShape
            }

            is Shape.ChainShape -> {
                val jVertices = shape.vertices.map { toJBox2D(it) }.toTypedArray()
                val chainShape = org.jbox2d.collision.shapes.ChainShape()
                chainShape.createLoop(jVertices, jVertices.size)
                chainShape
            }

            is Shape.EdgeShape -> {
                val start = toJBox2D(shape.start)
                val end = toJBox2D(shape.end)
                val edgeShape = org.jbox2d.collision.shapes.EdgeShape()
                edgeShape.set(start, end)
                edgeShape
            }
        }
    }
}