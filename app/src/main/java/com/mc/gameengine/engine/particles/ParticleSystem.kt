package com.mc.gameengine.engine.particles

import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.plus
import com.mc.gameengine.engine.math.times
import com.mc.gameengine.engine.math.random
import com.mc.gameengine.engine.render.Renderer
import kotlin.random.Random

abstract class ParticleSystem(
    private val config: ParticleConfig,
    private val maxParticles: Int = 256,
) {

    private val particles = mutableListOf<Particle>()

    fun emit(position: Vec2, count: Int) {
        repeat(count) {
            if (particles.size >= maxParticles) return

            val speed = config.speed.random()
            val life = config.life.random()
            val scale = config.scale.random()
            val rotation = config.rotation.random()
            val color = config.color.random()
            val angle = config.angle.random()
            val direction = config.direction.random()
            val velocity = Vec2.fromAngle(Random.nextFloat() * direction) * speed

            val particle = Particle(
                position = position,
                velocity = velocity,
                life = life,
                scale = scale,
                rotation = rotation,
                color = color,
                angle = angle
            )

            particles += particle
            onCreate(particle)
        }
    }

    fun update(dt: Float) {
        val it = particles.iterator()
        while (it.hasNext()) {
            val p = it.next()

            p.velocity += config.gravity * dt
            p.velocity *= config.damping
            p.position += p.velocity * dt
            p.angle += p.rotation * dt
            p.life -= dt

            if (p.life <= 0f) {
                it.remove()
                onDelete(p)
            }
        }
        particles.replaceAll { onUpdate(it) }
    }

    fun draw(renderer: Renderer) {
        particles.map { renderer.onDraw(it) }
    }

    protected open fun onCreate(particle: Particle) = Unit

    protected open fun onDelete(particle: Particle) = Unit

    protected open fun onUpdate(particle: Particle) = particle

    protected abstract fun Renderer.onDraw(particle: Particle)
}
