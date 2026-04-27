package com.mc.gameengine.engine.input.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.mc.gameengine.engine.input.sensor.SensorProcessor

class SensorInputAdapter(
    context: Context,
    private val sensorProcessor: SensorProcessor
): SensorEventListener {
    private val manager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val gyroscope = manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    fun start() {
        accelerometer?.let {
            manager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
        gyroscope?.let {
            manager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    fun stop() {
        manager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        sensorProcessor.onSensorChanged(event)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}