package com.mc.gameengine.engine.physics

import com.mc.gameengine.engine.collision.CollisionBodyType
import com.mc.gameengine.engine.math.Vec2
import org.dyn4j.geometry.Capsule
import org.dyn4j.geometry.Circle
import org.dyn4j.geometry.Convex
import org.dyn4j.geometry.Ellipse
import org.dyn4j.geometry.MassType
import org.dyn4j.geometry.Polygon
import org.dyn4j.geometry.Rectangle
import org.dyn4j.geometry.Segment
import org.dyn4j.geometry.Slice
import org.dyn4j.geometry.Vector2

internal class Dyn4jFactor(private var pixelsPerMeter: Float) {

    fun pxToM(px: Float): Double = (px / pixelsPerMeter).toDouble()
    fun mToPx(m: Double): Float = (m * pixelsPerMeter).toFloat()

    fun toDyn4j(vec2: Vec2): Vector2 = Vector2(pxToM(vec2.x), pxToM(vec2.y))
    fun toEngine(vec2: Vector2): Vec2 = Vec2(mToPx(vec2.x), mToPx(vec2.y))

    fun radToDeg(rad: Double): Float = (rad * 180 / Math.PI).toFloat()
    fun degToRad(deg: Float): Double = deg * Math.PI / 180

    fun toDyn4j(bodyType: CollisionBodyType) = when (bodyType) {
        CollisionBodyType.Static -> MassType.INFINITE
        CollisionBodyType.Dynamic -> MassType.NORMAL
        CollisionBodyType.DinamicAngle -> MassType.FIXED_LINEAR_VELOCITY
        CollisionBodyType.DinamicPosition -> MassType.FIXED_ANGULAR_VELOCITY
    }

    fun toEngine(bodyType: MassType) = when (bodyType) {
        MassType.INFINITE -> CollisionBodyType.Static
        MassType.NORMAL -> CollisionBodyType.Dynamic
        MassType.FIXED_LINEAR_VELOCITY -> CollisionBodyType.DinamicAngle
        MassType.FIXED_ANGULAR_VELOCITY -> CollisionBodyType.DinamicPosition
    }

    fun toDyn4j(shape: Shape): Convex {
        return when (shape) {
            is Shape.BoxShape -> {
                val jSize = toDyn4j(shape.size)
                val rectangle = Rectangle(jSize.x, jSize.y)
                rectangle
            }

            is Shape.CircleShape -> {
                val jRadius = pxToM(shape.radius)
                val circleShape = Circle(jRadius)
                circleShape
            }

            is Shape.EllipseShape -> {
                val jRadius = toDyn4j(shape.radius)
                val ellipseShape = Ellipse(jRadius.x, jRadius.y)
                ellipseShape
            }

            is Shape.PolygonShape -> {
                val vertices = shape.vertices.map { toDyn4j(it) }.toTypedArray()
                val polygonShape = Polygon(*vertices)
                polygonShape
            }

            is Shape.SegmentShape -> {
                val start = toDyn4j(shape.start)
                val end = toDyn4j(shape.end)
                val edgeShape = Segment(start, end)
                edgeShape
            }

            is Shape.CapsuleShape -> {
                val size = toDyn4j(shape.size)
                val capsuleShape = Capsule(size.x, size.y)
                capsuleShape
            }

            is Shape.SliceShape -> {
                val radius = pxToM(shape.radius)
                val theta = degToRad(shape.theta)
                val sliceShape = Slice(radius, theta)
                sliceShape
            }
        }
    }

    fun toEngine(shape: Convex): Shape {
        return when (shape) {
            is Rectangle -> Shape.BoxShape(Vec2(mToPx(shape.width), mToPx(shape.height)))
            is Circle -> Shape.CircleShape(mToPx(shape.radius))
            is Ellipse -> Shape.EllipseShape(Vec2(mToPx(shape.width), mToPx(shape.height)))
            is Polygon -> Shape.PolygonShape(shape.vertices.map { toEngine(it) })
            is Segment -> Shape.SegmentShape(toEngine(shape.point1), toEngine(shape.point2))
            is Capsule -> {
                val width = mToPx(shape.length)
                val height = mToPx(shape.capRadius * 2)
                Shape.CapsuleShape(Vec2(width, height))
            }
            is Slice -> Shape.SliceShape(mToPx(shape.radius), radToDeg(shape.theta))
            else -> throw IllegalArgumentException("Unsupported shape type: ${shape.javaClass}")
        }
    }
}