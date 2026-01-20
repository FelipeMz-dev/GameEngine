package com.mc.gameengine.engine.input

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.mc.gameengine.engine.math.round
import com.mc.gameengine.engine.time.SmoothValue

class DeviceRotationAdapter(
    context: Context,
    private val input: InputManager
) : SensorEventListener {

    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    fun start() {
        rotationSensor?.let {
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

    private val rotationMatrix = FloatArray(9)
    private val orientation = FloatArray(3)

    private val pitchFilter = SmoothValue()
    private val rollFilter = SmoothValue()
    private val yawFilter = SmoothValue()

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ROTATION_VECTOR) return

        SensorManager.getRotationMatrixFromVector(
            rotationMatrix,
            event.values
        )

        SensorManager.getOrientation(
            rotationMatrix,
            orientation
        )

        val rawYaw = Math.toDegrees(orientation[0].toDouble()).toFloat()
        val rawPitch = Math.toDegrees(orientation[1].toDouble()).toFloat()
        val rawRoll = Math.toDegrees(orientation[2].toDouble()).toFloat()

        val pitch = pitchFilter.update(rawPitch).round(1)
        val roll = rollFilter.update(rawRoll).round(1)
        val yaw = yawFilter.update(rawYaw).round(1)

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}