package com.mc.engine.input

class SensorManager {

    private val listeners = ListenerRegistry<SensorListener>()

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
