package com.mc.gameengine.engine.collision

interface CollisionListener {
    fun onCollision(event: CollisionEvent)
}