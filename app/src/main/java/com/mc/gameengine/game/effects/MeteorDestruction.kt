package com.mc.gameengine.game.effects

import androidx.compose.ui.graphics.Color
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.particles.Particle
import com.mc.gameengine.engine.particles.ParticleConfig
import com.mc.gameengine.engine.particles.ParticleSystem
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer
import com.mc.gameengine.game.assets.SpritesDinoPlayer

class MeteorDestruction : ParticleSystem(
    ParticleConfig(
        life = 0.7f..1.2f,
        speed = 50f..120f,
        scale = 2f..4f,
        gravity = Vec2(0f, 300f),
        color = listOf(
            Color(red = 255, green = 235, blue = 59, alpha = 150),
            Color(red = 200, green = 87, blue = 0, alpha = 200),
            Color(red = 222, green = 55, blue = 33, alpha = 255),
            Color(red = 96, green = 57, blue = 41, alpha = 255),
            Color(red = 66, green = 33, blue = 18, alpha = 255),
        )
    )
) {
    override fun onUpdate(particle: Particle): Particle {
        val y = if (particle.position.y > 400) particle.velocity.y / -2 else particle.velocity.y
        return particle.copy(velocity = particle.velocity.copy(y = y))
    }

    override fun Renderer.onDraw(particle: Particle) {
        drawCircle(
            position = particle.position,
            radius = particle.scale,
            pivot = Pivot.Center,
            color = particle.color
        )
    }
}

class BlurFX: ParticleSystem(
    ParticleConfig(
        life = 0.2f..0.2f,
        speed = 0f..0f,
        color = listOf(Color.White)
    )
) {

    private var flipX = false

    override fun onUpdate(particle: Particle): Particle {
        return particle.copy(color = particle.color.copy(alpha = particle.life * 3f))
    }

    override fun Renderer.onDraw(particle: Particle) {
        drawSprite(
            sprite = SpritesDinoPlayer.WALK,
            frame = 1,
            position = particle.position,
            flipX = flipX,
            color = particle.color
        )
    }

    fun emit(position: Vec2, flipX: Boolean) {
        this.flipX = flipX
        emit(position = position, count = 1)
    }
}