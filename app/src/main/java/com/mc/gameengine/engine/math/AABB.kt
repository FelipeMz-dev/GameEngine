package com.mc.gameengine.engine.math

data class AABB(
    val position: Vec2,
    val size: Vec2
) {
    fun intersects(other: AABB) = position.x < other.position.x + other.size.x &&
            position.x + size.x > other.position.x &&
            position.y < other.position.y + other.size.y &&
            position.y + size.y > other.position.y
}