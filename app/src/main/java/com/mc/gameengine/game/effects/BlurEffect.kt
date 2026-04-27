package com.mc.gameengine.game.effects

import androidx.compose.ui.graphics.Color
import com.mc.gameengine.engine.core.SpriteId
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.particles.Particle
import com.mc.gameengine.engine.particles.ParticleConfig
import com.mc.gameengine.engine.particles.ParticleSystem
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer

class BlurEffect(private val spriteId: SpriteId) : ParticleSystem(
    ParticleConfig(
        life = 0.2f..0.2f,
        speed = 0f..0f,
        color = listOf(Color.White)
    )
) {

    private var flipX = false
    private var alpha = 1f

    override fun onUpdate(particle: Particle): Particle {
        return particle.copy(
            color = particle.color.copy(alpha = particle.life * 3f)
        )
    }

    override fun Renderer.onDraw(particle: Particle) {
        val minAlpha = minOf(particle.color.alpha, alpha)
        drawSprite(
            spriteId = spriteId,
            frame = 1,
            state = TransformState(
                position = particle.position,
                pivot = Pivot.Center,
                flipX = flipX,
            ),
            color = particle.color.copy(alpha = minAlpha)
        )
    }

    fun emit(
        position: Vec2,
        flipX: Boolean = false,
        alpha: Float = 1f
    ) {
        this.alpha = alpha
        this.flipX = flipX
        emit(position = position, count = 1)
    }
}