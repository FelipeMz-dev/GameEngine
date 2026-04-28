package com.mc.gameengine.game.instance

import androidx.compose.ui.graphics.Color
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.input.sensor.SensorEvent
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.clamp
import com.mc.gameengine.engine.math.div
import com.mc.gameengine.engine.math.minus
import com.mc.gameengine.engine.math.plus
import com.mc.gameengine.engine.math.times
import com.mc.gameengine.engine.input.sensor.SensorListener
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer

class MovingSphere : Instance(), SensorListener {

    private var sensorState = "State:"

    private val speed = 500f

    override fun onEnterScene() {
        current = current.copy(viewportSize() / 2f)
    }

    override fun fixedUpdate(dt: Float) {
        val minMoving = Vec2.Zero + 50f
        val maxMoving = viewportSize() - 50f
        val moving = (current.position + physics.velocity * dt)
        current = current.copy(moving.clamp(minMoving, maxMoving))
    }

    override fun onSensorEvent(event: SensorEvent) {
        when (event) {
            is SensorEvent.AccelerometerEvent -> {
                sensorState = "State: " +
                        "\npitch: ${event.value.x}" +
                        "\nroll: ${event.value.y}" +
                        "\nyaw: ${event.value.z}" +
                        "\nsize: ${viewportSize()}"
                physics = physics.copy(Vec2(event.value.y, event.value.x) * speed)
            }

            else -> Unit
        }
    }

    override fun update(dt: Float) {
        if (current.position == Vec2.Zero && viewportSize() != Vec2.Zero) {
            current.position = viewportSize() / 2f
        }
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