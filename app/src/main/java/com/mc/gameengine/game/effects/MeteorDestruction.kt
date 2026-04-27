package com.mc.gameengine.game.effects

import androidx.compose.ui.graphics.Color
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.times
import com.mc.gameengine.engine.particles.Particle
import com.mc.gameengine.engine.particles.ParticleConfig
import com.mc.gameengine.engine.particles.ParticleSystem
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer

private val colorBrown = Color(red = 66, green = 33, blue = 18, alpha = 255)
private val colorYellow = Color(red = 255, green = 235, blue = 59, alpha = 255)
private val colorOrange = Color(red = 217, green = 97, blue = 6, alpha = 255)
private val colorRed = Color(red = 232, green = 36, blue = 36, alpha = 255)
private val colorBrown2 = Color(red = 112, green = 62, blue = 42, alpha = 255)

class MeteorDestruction : ParticleSystem(
    ParticleConfig(
        life = 0.7f..1.2f,
        speed = 50f..120f,
        scale = 2f..4f,
        angle = 0f..90f,
        gravity = Vec2(0f, 300f),
        color = listOf(
            colorBrown,
            colorYellow,
            colorOrange,
            colorRed,
            colorBrown2
        )
    )
) {
    override fun onUpdate(particle: Particle): Particle {
        val y = if (particle.position.y > 390) particle.velocity.y / -2 else particle.velocity.y
        return particle.copy(velocity = particle.velocity.copy(y = y))
    }

    override fun Renderer.onDraw(particle: Particle) {
        if (particle.color == colorBrown || particle.color == colorBrown2) drawRect(
            size = particle.scale * 2.4f,
            state = TransformState(
                position = particle.position,
                angle = particle.angle,
                pivot = Pivot.Center
            ),
            color = particle.color,
        ) else drawOval(
            size = particle.scale * 1.8f,
            state = TransformState(
                position = particle.position,
                angle = particle.angle,
                pivot = Pivot.Center
            ),
            color = particle.color,
        )
    }
}