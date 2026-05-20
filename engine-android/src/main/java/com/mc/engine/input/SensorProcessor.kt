package com.mc.engine.input

import android.hardware.Sensor
import com.mc.engine.math.Vec3
import com.mc.engine.math.round
import com.mc.engine.time.SmoothValue
import android.hardware.SensorEvent as HardwareSensorEvent

class SensorProcessor(
    val sensorManager: SensorManager
) {

    private val smoothAccelerometer = Array(3) { SmoothValue() }

    private val smoothGyroscope = Array(3) { SmoothValue() }

    fun onSensorChanged(event: HardwareSensorEvent) {
        when (event.sensor.type) {

            Sensor.TYPE_ACCELEROMETER -> {

                smoothAccelerometer.forEachIndexed { i, item ->
                    item.update(event.values[i])
                }

                val axis = Vec3(
                    x = smoothAccelerometer[0].value.round(1),
                    y = smoothAccelerometer[1].value.round(1),
                    z = smoothAccelerometer[2].value.round(1)
                )

                sensorManager.dispatch(SensorEvent.AccelerometerEvent(axis))
            }

            Sensor.TYPE_GYROSCOPE -> {
                smoothGyroscope.forEachIndexed { i, item ->
                    item.update(event.values[i])
                }

                val axis = Vec3(
                    x = smoothGyroscope[0].value.round(1),
                    y = smoothGyroscope[1].value.round(1),
                    z = smoothGyroscope[2].value.round(1)
                )

                sensorManager.dispatch(SensorEvent.GyroscopeEvent(axis))
            }
        }
    }
}