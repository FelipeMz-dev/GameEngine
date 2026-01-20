package com.mc.gameengine.engine.particles

import com.mc.gameengine.engine.render.Renderer

class ParticleSystem {

    private val emitters = mutableListOf<ParticleEmitter>()

    fun add(emitter: ParticleEmitter) {
        emitters += emitter
    }

    fun fixedUpdate(dt: Float) {
        emitters.forEach { it.fixedUpdate(dt) }
    }

    fun render(renderer: Renderer) {
        emitters.forEach { it.render(renderer) }
    }
}