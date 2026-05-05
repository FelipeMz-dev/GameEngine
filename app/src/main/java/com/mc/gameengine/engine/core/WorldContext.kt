package com.mc.gameengine.engine.core

import com.mc.gameengine.engine.collision.Collider
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.audio.AudioPlayer
import com.mc.gameengine.engine.compose.Camera2D
import com.mc.gameengine.engine.physics.PhysicsManager

internal interface WorldContext {
    fun audioPlayer(): AudioPlayer
    fun physicsWorld(): PhysicsManager
    fun camera2D(): Camera2D?
    fun viewportSize(): Vec2
    fun viewportScale(): Vec2
    fun spriteSize(id: SpriteId): Vec2
    fun deleteInstance(instance: Instance)
    fun addInstance(instance: Instance)
    fun addCollider(collider: Collider)
    fun removeCollider(collider: Collider)
    fun clearInstanceColliders(instance: Instance)
    fun calculateFromViewport(position: Vec2): Vec2
    fun screenToWorld(position: Vec2): Vec2
    fun worldToScreen(position: Vec2): Vec2
}