package com.mc.gameengine.engine.core

import com.mc.gameengine.engine.collision.Collider
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.audio.AudioPlayer
import com.mc.gameengine.engine.physics.PhysicsWorld

internal interface WorldContext {
    val audioPlayer: AudioPlayer
    fun viewportSize(): Vec2
    fun viewportScale(): Vec2
    fun spriteSize(id: SpriteId): Vec2
    fun deleteInstance(instance: Instance)
    fun addInstance(instance: Instance)
    fun addCollider(collider: Collider)
    fun removeCollider(collider: Collider)
    fun clearInstanceColliders(instance: Instance)
    fun rotateCamera(angle: Float, from: Vec2)
    fun translateCamera(to: Vec2)
    fun zoomCamera(value: Float, from: Vec2)
    fun calculateFromViewport(position: Vec2): Vec2
    fun screenToWorld(position: Vec2): Vec2
    fun worldToScreen(position: Vec2): Vec2
    fun physicsWorld(): PhysicsWorld
}