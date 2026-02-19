package com.mc.gameengine.engine.input

import com.mc.gameengine.engine.math.Vec3

sealed interface SensorEvent

data class AccelerometerEvent(
    val value: Vec3
) : SensorEvent

data class GyroscopeEvent(
    val value: Vec3
) : SensorEvent