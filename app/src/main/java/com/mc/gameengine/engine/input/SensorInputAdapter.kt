package com.mc.gameengine.engine.input

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.mc.gameengine.engine.math.Vec3
import com.mc.gameengine.engine.math.round
import com.mc.gameengine.engine.time.SmoothValue

class SensorInputAdapter(
    context: Context,
    private val inputManager: InputManager
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    fun start() {
        accelerometer?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    private val orientation = Array(3) { SmoothValue() }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {

            Sensor.TYPE_ACCELEROMETER -> {

                orientation.forEachIndexed { i, item ->
                    item.update(event.values[i])
                }

                val axis = Vec3(
                    x = orientation[0].value.round(1),
                    y = orientation[1].value.round(1),
                    z = orientation[2].value.round(1)
                )

                inputManager.dispatch(AccelerometerEvent(axis))
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

}