package com.mc.gameengine.engine.core

import com.mc.gameengine.core.math.Vec2

interface WorldContext {
    fun viewportSize(): Vec2
    fun spriteSize(id: SpriteId): Vec2
    fun deleteInstance(instance: Instance)
    fun addInstance(instance: Instance)
}