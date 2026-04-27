package com.mc.gameengine.engine.math

data class AABB(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
) {
    fun overlaps(other: AABB): Boolean {
        return x < other.x + other.width && x + width > other.x
                && y < other.y + other.height && y + height > other.y
    }
}