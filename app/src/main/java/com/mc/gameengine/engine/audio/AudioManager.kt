package com.mc.gameengine.engine.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.SoundPool
import com.mc.gameengine.engine.core.AudioId
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.length
import com.mc.gameengine.engine.math.minus

class AudioManager(context: Context): AudioPlayer {

    private var listeners = mutableListOf<AudioListener>()

    private val soundPool = SoundPool.Builder()
        .setMaxStreams(16)
        .build()

    private val sounds = mutableMapOf<AudioId, Int>()
    private val musics = mutableMapOf<AudioId, Int>()
    private val musicPlayers = mutableMapOf<AudioId, MediaPlayer>()

    private val appContext = context.applicationContext

    fun loadSound(id: AudioId, resId: Int) {
        val soundId = soundPool.load(appContext, resId, 1)

        sounds[id] = soundId
    }

    fun loadMusic(id: AudioId, resId: Int) {
        musics[id] = resId
    }

    internal fun registerListener(instance: Instance) {
        (instance as? AudioListener)?.let { listeners += it }
    }

    internal fun unregisterListener(instance: Instance) {
        (instance as? AudioListener)?.let { listeners -= it }
    }

    override fun playSound(
        id: AudioId,
        volume: Float,
        rate: Float,
        loop: Boolean
    ) {
        val soundId = sounds[id] ?: return

        soundPool.play(
            soundId,
            volume,
            volume,
            1,
            if (loop) -1 else 0,
            rate
        )
    }

    override fun playSoundAt(
        id: AudioId,
        position: Vec2,
        maxDistance: Float,
        volume: Float,
        loop: Boolean
    ) {
        listeners.ifEmpty { return }
        val soundId = sounds[id] ?: return
        val dist = listeners.minOf { (position - it.onRequireListenPosition()).length() }
        val volume = (volume - (dist / maxDistance)).coerceIn(0f, 1f)

        soundPool.play(
            soundId,
            volume,
            volume,
            1,
            if (loop) -1 else 0,
            1f
        )
    }

    override fun stopSound(id: AudioId) {
        val soundId = sounds[id] ?: return
        soundPool.stop(soundId)
    }

    override fun playMusic(
        id: AudioId,
        volume: Float,
        loop: Boolean
    ) {
        musicPlayers[id]?.run { return }

        val resId = musics[id] ?: return
        val player = MediaPlayer.create(appContext, resId)

        player.isLooping = loop
        player.setVolume(volume, volume)
        player.start()

        musicPlayers[id] = player
    }

    override fun stopMusic(id: AudioId) {
        val player = musicPlayers[id] ?: return
        player.stop()
        player.release()
        musicPlayers.remove(id)
    }

    override fun pauseMusic(id: AudioId) {
        val player = musicPlayers[id] ?: return
        player.pause()
    }

    override fun resumeMusic(id: AudioId) {
        val player = musicPlayers[id] ?: return
        player.start()
    }

    override fun setMusicVolume(id: AudioId, volume: Float) {
        val player = musicPlayers[id] ?: return
        player.setVolume(volume, volume)
    }

    override fun setMusicLooping(id: AudioId, looping: Boolean) {
        val player = musicPlayers[id] ?: return
        player.isLooping = looping
    }

    override fun isMusicPlaying(id: AudioId): Boolean {
        val player = musicPlayers[id] ?: return false
        return player.isPlaying
    }

    override fun stopMusics() {
        musicPlayers.values.forEach {
            it.stop()
            it.release()
        }
        musicPlayers.clear()
    }
}