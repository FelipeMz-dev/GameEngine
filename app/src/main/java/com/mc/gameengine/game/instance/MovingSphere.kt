package com.mc.gameengine.game.instance

import androidx.compose.ui.graphics.Color
import com.mc.engine.core.Instance
import com.mc.engine.core.TransformState
import com.mc.engine.input.sensor.SensorEvent
import com.mc.engine.input.sensor.SensorListener
import com.mc.engine.math.Vec2
import com.mc.engine.math.clamp
import com.mc.engine.math.div
import com.mc.engine.math.minus
import com.mc.engine.math.plus
import com.mc.engine.math.times
import com.mc.engine.render.Pivot
import com.mc.engine.render.Renderer

class MovingSphere : Instance(), SensorListener {

    private var sensorState = "State:"

    private val speed = 500f

    override fun onEnterScene() {
        updatePosition { viewport().size / 2f }
    }

    override fun fixedUpdate(dt: Float) {

        val minMoving = Vec2.Zero + 50f
        val maxMoving = viewport().size - 50f
        val clamped = current.position.clamp(minMoving, maxMoving)

        if (clamped != current.position) {
            updatePosition { clamped }
        }
    }

    override fun onSensorEvent(event: SensorEvent) {
        when (event) {
            is SensorEvent.AccelerometerEvent -> moveByAccelerometer(event)
            else -> Unit
        }
    }

    fun moveByAccelerometer(event: SensorEvent.AccelerometerEvent) {
        sensorState = "State: " +
                "\npitch: ${event.value.x}" +
                "\nroll: ${event.value.y}" +
                "\nyaw: ${event.value.z}" +
                "\nsize: ${viewport()}"
        updatePosition { it + (Vec2(event.value.y, event.value.x) * speed) }
    }

    override fun Renderer.onRender(state: TransformState) {
        drawCircle(
            state = TransformState(
                position = state.position,
                pivot = Pivot.Center
            ),
            radius = 50f,
            color = Color.Red
        )

        drawText(
            text = sensorState,
            position = Vec2.Zero + 100f,
        )

        drawText(
            text = current.position.toString(),
            position = current.position - 100f,
        )
    }

}
