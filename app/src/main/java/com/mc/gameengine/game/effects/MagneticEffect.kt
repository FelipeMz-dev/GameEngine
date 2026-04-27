package com.mc.gameengine.game.effects

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.mc.gameengine.engine.audio.AudioPlayer
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.particles.Particle
import com.mc.gameengine.engine.particles.ParticleConfig
import com.mc.gameengine.engine.particles.ParticleSystem
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer
import com.mc.gameengine.game.assets.MainAudios

class MagneticEffect : ParticleSystem(
    ParticleConfig(
        life = 2f..2f,
        speed = 0f..0f,
        color = listOf(Color.White)
    )
) {

    private var timer = 0

    private var currentPosition: Vec2 = Vec2.Zero

    override fun onCreate(particle: Particle) {
        currentPosition = particle.position
    }

    override fun onUpdate(particle: Particle): Particle {
        val alpha = 0.5f / particle.life
        return particle.copy(
            position = currentPosition,
            color = particle.color.copy(alpha = alpha)
        )
    }

    fun updatePosition(position: Vec2) {
        currentPosition = position
        timer++
    }

    override fun Renderer.onDraw(particle: Particle) {
        drawOval(
            size = Vec2.from(200 * particle.life),
            state = TransformState(
                position = particle.position,
                pivot = Pivot.Center
            ),
            style = Stroke(width = 10f - 5 * particle.life),
            color = particle.color
        )
    }

    fun emit(position: Vec2, audioPlayer: AudioPlayer) {
        if (timer >= 30) timer = 0
        else return
        emit(position = position, count = 1)
        audioPlayer.playSound(MainAudios.SND_MAGNET)
    }
}