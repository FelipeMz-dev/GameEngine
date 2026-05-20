package com.mc.engine.core

import com.mc.engine.audio.AudioPlayer
import com.mc.engine.math.Vec2
import com.mc.engine.physics.PhysicsManager

interface WorldContext {
    fun audioPlayer(): AudioPlayer
    fun physicsManager(): PhysicsManager
    fun camera2D(): Camera2D?
    fun viewport(): Viewport
    fun spriteSize(id: SpriteId): Vec2
    fun deleteInstance(instance: Instance)
    fun addInstance(instance: Instance)
    fun calculateFromViewport(position: Vec2): Vec2
    fun screenToWorld(position: Vec2): Vec2
    fun worldToScreen(position: Vec2): Vec2
}