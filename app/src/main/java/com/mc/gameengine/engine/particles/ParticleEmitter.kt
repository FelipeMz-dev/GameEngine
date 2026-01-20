package com.mc.gameengine.engine.particles

import com.mc.gameengine.core.math.Vec2
import com.mc.gameengine.core.math.normalized
import com.mc.gameengine.core.math.plus
import com.mc.gameengine.core.math.times
import com.mc.gameengine.engine.math.random
import com.mc.gameengine.engine.render.Renderer

abstract class ParticleEmitter(
    private val config: ParticleConfig,
    private val draw: ParticleDraw,
    private val maxParticles: Int = 256
) {

    private val particles = mutableListOf<Particle>()

    fun emit(position: Vec2, direction: Vec2, count: Int) {
        repeat(count) {
            if (particles.size >= maxParticles) return

            val speed = config.speed.random()
            val life = config.life.random()
            val size = config.scale.random()

            particles += Particle(
                position = position,
                velocity = direction.normalized() * speed,
                life = life,
                size = size,
                rotation = 0f
            )
        }
    }

    fun fixedUpdate(dt: Float) {
        val it = particles.iterator()
        while (it.hasNext()) {
            val p = it.next()

            p.velocity += config.gravity * dt
            p.velocity *= config.damping
            p.position += p.velocity * dt
            p.life -= dt

            if (p.life <= 0f) it.remove()
        }
    }

    fun render(renderer: Renderer) {
        particles.forEach { draw.draw(renderer, it) }
    }
}

fun interface ParticleDraw {
    fun draw(renderer: Renderer, particle: Particle)
}