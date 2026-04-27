package com.mc.gameengine.app

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mc.gameengine.engine.audio.AudioPlayer
import com.mc.gameengine.game.assets.MainAudios
import com.mc.gameengine.game.instance.PowerItem
import com.mc.gameengine.game.instance.PowerItemType

class GameController(private val audioPlayer: AudioPlayer) {

    private val _powers = PowerItem.entries

    var isInit by mutableStateOf(true)
        private set

    var pause by mutableStateOf(true)
        private set

    var maxHoles by mutableIntStateOf(15)
        private set

    var holes by mutableIntStateOf(maxHoles)
        private set

    var score by mutableIntStateOf(0)
        private set

    val isDie by derivedStateOf { holes <= 0 }

    var lastScore by mutableIntStateOf(0)
        private set

    var leftBoost by mutableFloatStateOf(100f)
        private set

    var rightBoost by mutableFloatStateOf(100f)
        private set

    var balls by mutableIntStateOf(0)
        private set

    var lastBalls by mutableIntStateOf(0)
        private set

    var bestScore by mutableIntStateOf(10)
        private set

    var boostRemaining by mutableIntStateOf(10)
        private set

    val powers by derivedStateOf { _powers }

    val availablePowers by derivedStateOf { _powers.filter { it.isAvailable } }

    val isMultiplier by derivedStateOf { PowerItem.BallMultiplier in availablePowers }

    val isBoostingLeft by derivedStateOf { leftBoost == 0f }

    val isBoostingRight by derivedStateOf { rightBoost == 0f }

    fun syncPowers(dt: Float) {
        _powers.forEach {
            it.sync(dt)
        }
    }

    fun onPower(type: PowerItemType) {
        _powers.find { it.type == type }?.active()
    }

    fun onFinalized() {
        audioPlayer.playSound(MainAudios.SND_WATER_DROP)
    }

    fun clearPowers() {
        _powers.forEach { it.unActive() }
    }

    fun syncBoost(dt: Float) {
        if (leftBoost < 100) leftBoost += boostRemaining * dt
        if (rightBoost < 100) rightBoost += boostRemaining * dt
    }

    fun boostLeft() {
        if (leftBoost > 80) leftBoost = 0f
    }

    fun boostRight() {
        if (rightBoost > 80) rightBoost = 0f
    }

    fun incrementScore() {
        score++
        lastScore = score
    }

    fun togglePause() {
        isInit = false
        if (isDie) {
            holes = maxHoles
            score = 0
            if (!pause){
                lastScore = 0
                lastBalls = 0

            }
        }
        pause = !pause
        audioPlayer.playSound(MainAudios.SND_PAUSE)
    }

    fun hate() {
        holes--
        if (isDie) {
            if (bestScore < score) bestScore = score
            pause = true
            score = 0
        }
    }

    fun collectBall() {
        val value = if (isMultiplier) 2 else 1
        balls += value
        lastBalls += value
    }

    fun addBalls(value: Int) {
        balls += value
    }

    fun removeBalls(value: Int) {
        balls -= value
    }
}