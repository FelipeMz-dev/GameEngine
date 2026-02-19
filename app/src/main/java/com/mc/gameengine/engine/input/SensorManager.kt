package com.mc.gameengine.engine.input

interface SensorListener {
    fun onSensorEvent(event: SensorEvent)
}

class SensorManager {

    private val listeners = mutableSetOf<SensorListener>()

    fun register(listener: SensorListener) {
        listeners.add(listener)
    }

    fun unregister(listener: SensorListener) {
        listeners.remove(listener)
    }

    fun dispatch(sensor: SensorEvent) {
        listeners.forEach { it.onSensorEvent(sensor) }
    }
}