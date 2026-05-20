package com.mc.engine.core

import android.os.Build
import androidx.annotation.RequiresApi
import com.mc.engine.core.particles.Particle
import com.mc.engine.core.particles.ParticleConfig
import com.mc.engine.math.Vec2
import com.mc.engine.math.plus
import com.mc.engine.math.random
import com.mc.engine.math.times
import com.mc.engine.render.Renderer
import kotlin.random.Random

abstract class ParticleSystem(
    private val config: ParticleConfig,
    private val maxParticles: Int = 256,
) {

    private val particles = mutableListOf<Particle>()

    val hasParticle get() = particles.isNotEmpty()

    fun emit(position: Vec2, count: Int) {
        repeat(count) {
            if (particles.size >= maxParticles) return

            val speed = config.speed.random()
            val life = config.life.random()
            val rotation = config.rotation.random()
            val color = config.color.random()
            val angle = config.angle.random()
            val direction = config.direction.random()
            val scale = Vec2.from(config.scale.random())
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

    @RequiresApi(Build.VERSION_CODES.N)
    open fun update(dt: Float) {
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
