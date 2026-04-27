package com.mc.gameengine.engine.audio

import com.mc.gameengine.engine.core.AudioId
import com.mc.gameengine.engine.math.Vec2

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
        maxDistance: Float = 600f,
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