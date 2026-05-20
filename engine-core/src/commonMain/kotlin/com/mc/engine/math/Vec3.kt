package com.mc.engine.math

import kotlin.math.sqrt

class Vec3(
    val x: Float,
    val y: Float,
    val z: Float
)

operator fun Vec3.plus(other: Vec3) = Vec3(x + other.x, y + other.y, z + other.z)

operator fun Vec3.minus(other: Vec3) = Vec3(x - other.x, y - other.y, z - other.z)

operator fun Vec3.times(other: Vec3) = Vec3(x * other.x, y * other.y, z * other.z)

operator fun Vec3.div(other: Vec3) = Vec3(x / other.x, y / other.y, z / other.z)

operator fun Vec3.plus(scalar: Float) = Vec3(x + scalar, y + scalar, z + scalar)

operator fun Vec3.minus(scalar: Float) = Vec3(x - scalar, y - scalar, z - scalar)

operator fun Vec3.times(scalar: Float) = Vec3(x * scalar, y * scalar, z * scalar)

operator fun Vec3.div(scalar: Float) = Vec3(x / scalar, y / scalar, z / scalar)

fun Vec3.normalize() = this / length()

fun Vec3.length() = sqrt(x * x + y * y + z * z)

fun Vec3.normalizeRotation() = Vec3(
    x = normalize(x),
    y = normalize(y),
    z = normalize(z)
)

private fun normalize(angle: Float): Float =
    ((angle + 180f) % 360f + 360f) % 360f - 180f
