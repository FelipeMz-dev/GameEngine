package com.mc.gameengine.engine.collision

data class CollisionEvent(
    val self: Collider,
    val other: Collider
)