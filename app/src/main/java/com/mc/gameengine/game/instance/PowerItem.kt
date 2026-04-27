package com.mc.gameengine.game.instance

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

sealed class PowerItem(val type: PowerItemType) {

    companion object{
        val entries = setOf(
            Helmet,
            Warning,
            BallMultiplier,
            Magnet,
            SlowTime,
            Portal
        )
    }

    object Magnet : PowerItem(PowerItemType.MAGNET)
    object Helmet : PowerItem(PowerItemType.HELMET)
    object Warning : PowerItem(PowerItemType.WARNING)
    object BallMultiplier : PowerItem(PowerItemType.BALL_MULTIPLIER)
    object Portal : PowerItem(PowerItemType.PORTAL)
    object SlowTime : PowerItem(PowerItemType.SLOW_TIME)

    var level: Int by mutableIntStateOf(5)

    var value: Float by mutableFloatStateOf(0f)

    val price: Int by derivedStateOf { 80 * level }

    val isMaxLevel: Boolean by derivedStateOf { level == 5 }

    val isAvailable: Boolean by derivedStateOf { value > 0f }

    private val duration: Int by derivedStateOf { 10 / level }

    fun upLevel() {
        if (isMaxLevel) return
        level++
    }

    fun sync(dt: Float) {
        if (!isAvailable) return
        value = (value - (duration * dt)).coerceAtLeast(0f)
    }

    fun active() {
        value = 100f
    }

    fun unActive() {
        value = 0f
    }
}