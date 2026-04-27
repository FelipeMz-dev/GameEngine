package com.mc.gameengine.game.assets

import com.mc.gameengine.R
import com.mc.gameengine.engine.audio.AudioManager

object MainAudios {
    const val SND_ROCK_DESTROY = "rock_destroy"
    const val SND_EXPLOSION = "explosion"
    const val SND_PAUSE = "snd_pause"
    const val SND_HIT_BAT = "hit_bat"
    const val SND_BALL = "snd_ball"
    const val SND_MAGNET = "snd_magnet"
    const val SND_COLLECT = "snd_collect"
    const val SND_BOOST = "snd_boost"
    const val SND_EGG_CRACK = "snd_egg_crack"
    const val SND_ALERT = "snd_alert"
    const val SND_COMPUTER_BEEP = "snd_computer_beep"
    const val SND_WATER_DROP = "snd_water_drop"
    const val SND_TELEPORT = "snd_teleport"

    fun AudioManager.loadMainAudio() = this.also {
        loadSound(SND_EXPLOSION, resId = R.raw.snd_explosion)
        loadSound(SND_ROCK_DESTROY, resId = R.raw.snd_rock_destroy)
        loadSound(SND_PAUSE, resId = R.raw.snd_pause)
        loadSound(SND_HIT_BAT, resId = R.raw.snd_hit_bat)
        loadSound(SND_BALL, resId = R.raw.snd_ball)
        loadSound(SND_MAGNET, resId = R.raw.snd_magnetic_field)
        loadSound(SND_COLLECT, resId = R.raw.snd_collect)
        loadSound(SND_BOOST, resId = R.raw.snd_boost)
        loadSound(SND_EGG_CRACK, resId = R.raw.snd_egg_crack)
        loadSound(SND_ALERT, resId = R.raw.snd_alert)
        loadSound(SND_COMPUTER_BEEP, resId = R.raw.snd_computer_beep)
        loadSound(SND_WATER_DROP, resId = R.raw.snd_water_drop)
        loadSound(SND_TELEPORT, resId = R.raw.snd_teleport)
    }
}
