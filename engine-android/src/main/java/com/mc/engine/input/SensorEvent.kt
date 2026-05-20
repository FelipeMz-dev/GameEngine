package com.mc.engine.input

import com.mc.engine.math.Vec3

sealed interface SensorEvent {

    data class AccelerometerEvent(
        val value: Vec3
    ) : SensorEvent

    data class GyroscopeEvent(
        val value: Vec3
    ) : SensorEvent
}