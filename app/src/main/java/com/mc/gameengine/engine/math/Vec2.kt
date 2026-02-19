package com.mc.gameengine.engine.math

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sqrt

data class Vec2(val x: Float, val y: Float) {

    companion object {
        val Zero = Vec2(0f, 0f)

        fun fromAngle(angleRadians: Float) = Vec2(
            x = cos(angleRadians),
            y = sin(angleRadians)
        )

        fun from(value: Float) = Vec2(value, value)

        fun lerp(start: Float, end: Float, alpha: Float) = Vec2(
            x = start + (end - start) * alpha,
            y = start + (end - start) * alpha
        )
    }

    fun lerp(to: Vec2, alpha: Float): Vec2 = Vec2(
        x = x + (to.x - x) * alpha,
        y = y + (to.y - y) * alpha
    )

}

operator fun Vec2.minus(other: Vec2): Vec2 = Vec2(this.x - other.x, this.y - other.y)

operator fun Vec2.minus(scalar: Float): Vec2 = Vec2(this.x - scalar, this.y - scalar)

operator fun Vec2.plus(other: Vec2): Vec2 = Vec2(this.x + other.x, this.y + other.y)

operator fun Vec2.plus(scalar: Float): Vec2 = Vec2(this.x + scalar, this.y + scalar)

operator fun Vec2.times(other: Vec2): Vec2 = Vec2(this.x * other.x, this.y * other.y)

operator fun Vec2.times(scalar: Float): Vec2 = Vec2(this.x * scalar, this.y * scalar)

operator fun Vec2.div(other: Vec2): Vec2 = Vec2(this.x / other.x, this.y / other.y)

operator fun Vec2.div(scalar: Float): Vec2 = Vec2(this.x / scalar, this.y / scalar)

fun Vec2.distanceTo(other: Vec2): Float = (this - other).length()

fun Vec2.distanceSquaredTo(other: Vec2): Float {
    val dx = this.x - other.x
    val dy = this.y - other.y
    return dx * dx + dy * dy
}

fun Vec2.perp(): Vec2 = Vec2(-y, x)

fun Vec2.normalize(): Vec2 {
    val len = sqrt(x * x + y * y)
    return if (len == 0f) this else Vec2(x / len, y / len)
}

fun Vec2.scale(scalarX: Float, scalarY: Float): Vec2 = Vec2(this.x * scalarX, this.y * scalarY)

fun Vec2.rotate(angleDegrees: Float): Vec2 {
    val rad = Math.toRadians(angleDegrees.toDouble())
    val cosTheta = cos(rad)
    val sinTheta = sin(rad)
    return Vec2(
        x = (this.x.toDouble() * cosTheta - this.y.toDouble() * sinTheta).toFloat(),
        y = (this.x.toDouble() * sinTheta + this.y.toDouble() * cosTheta).toFloat()
    )
}

fun Vec2.reflect(normal: Vec2): Vec2 {
    val dotProduct = this.dot(normal)
    return this - normal * (2f * dotProduct)
}

fun Vec2.projectOnto(other: Vec2): Vec2 {
    val otherLengthSquared = other.x * other.x + other.y * other.y
    return if (otherLengthSquared != 0f) {
        val dotProduct = this.dot(other)
        other * (dotProduct / otherLengthSquared)
    } else {
        Vec2(0f, 0f)
    }
}

fun Vec2.perpendicular(): Vec2 = Vec2(-this.y, this.x)

fun Vec2.clamp(min: Vec2, max: Vec2): Vec2 = Vec2(
    x = max(min.x, min(this.x, max.x)),
    y = max(min.y, min(this.y, max.y))
)

fun Vec2.length(): Float = sqrt(x * x + y * y)

fun Vec2.normalized(): Vec2 {
    val len = length()
    return if (len != 0f) {
        Vec2(x / len, y / len)
    } else {
        Vec2(0f, 0f)
    }
}

fun Vec2.clamp(radius: Float): Vec2 = if (this.length() > radius) {
    this.normalized() * radius
} else this

fun Vec2.abs(): Vec2 = Vec2(abs(this.x), abs(this.y))
fun Vec2.floor(): Vec2 = Vec2(floor(this.x), floor(this.y))
fun Vec2.ceil(): Vec2 = Vec2(ceil(this.x), ceil(this.y))
fun Vec2.round(): Vec2 = Vec2(round(this.x), round(this.y))

fun Vec2.dot(other: Vec2): Float = this.x * other.x + this.y * other.y
fun Vec2.cross(other: Vec2): Float = this.x * other.y - this.y * other.x
fun Vec2.angleBetween(other: Vec2): Float {
    val dotProduct = this.dot(other)
    val lengthsProduct = this.length() * other.length()
    return if (lengthsProduct != 0f) {
        acos(dotProduct / lengthsProduct)
    } else {
        0f
    }
}

fun Vec2.isInArea(min: Vec2, max: Vec2): Boolean =
    x >= min.x && x <= max.x && y >= min.y && y <= max.y

fun Vec2.isInArea(rect: Rect): Boolean {
    return x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom
}

fun Vec2.toOffset(): Offset = Offset(this.x, this.y)
fun Offset.toVec2(): Vec2 = Vec2(this.x, this.y)
fun Vec2.toSize(): Size = Size(this.x, this.y)
fun Size.toVec2(): Vec2 = Vec2(this.width, this.height)
