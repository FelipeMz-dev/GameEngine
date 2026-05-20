package com.mc.engine.audio

import com.mc.engine.core.AudioId
import com.mc.engine.math.Vec2

/**
 * Interfaz de reproductor de audio agnóstica de plataforma.
 */
interface AudioPlayer {

    fun playSound(
        id: AudioId,
        volume: Float = 1f,
        rate: Float = 1f,
        loop: Boolean = false
    )

    fun playSoundAt(
        id: AudioId,
        position: Vec2,
        maxDistance: Float,
        volume: Float = 1f,
        loop: Boolean = false
    )

    fun stopSound(id: AudioId)

    fun playMusic(
        id: AudioId,
        volume: Float = 1f,
        loop: Boolean = false
    )

    fun stopMusic(id: AudioId)

    fun pauseMusic(id: AudioId)

    fun resumeMusic(id: AudioId)

    fun setMusicVolume(id: AudioId, volume: Float)

    fun setMusicLooping(id: AudioId, looping: Boolean)

    fun isMusicPlaying(id: AudioId): Boolean

    fun stopMusics()
}