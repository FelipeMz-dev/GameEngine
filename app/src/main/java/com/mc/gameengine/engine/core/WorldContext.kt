package com.mc.gameengine.engine.core

import com.mc.gameengine.engine.collision.Collider
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.audio.AudioPlayer
import com.mc.gameengine.engine.compose.Camera2D
import com.mc.gameengine.engine.physics.PhysicsManager

internal interface WorldContext {
    fun audioPlayer(): AudioPlayer
    fun physicsManager(): PhysicsManager
    fun camera2D(): Camera2D?
    fun viewport(): Viewport
    fun spriteSize(id: SpriteId): Vec2
    fun removeGameObject(gameObject: GameObject)
    fun spawnGameObject(gameObject: GameObject)
    fun addCollider(collider: Collider)
    fun removeCollider(collider: Collider)
    fun clearInstanceColliders(instance: GameObject)
    fun calculateFromViewport(position: Vec2): Vec2
    fun screenToWorld(position: Vec2): Vec2
    fun worldToScreen(position: Vec2): Vec2
}

internal inline fun <reified T : GameObject> WorldContext.spawn(factory: () -> T): T {
    return InstanceContextProvider.runWithContext(this) {
        val instance = factory()
        spawnGameObject(instance)
        instance
    }
}