package com.mc.engine.physics

interface CollisionListener {
    fun onCollision(event: CollisionEvent)
}