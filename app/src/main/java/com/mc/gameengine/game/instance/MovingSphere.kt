package com.mc.gameengine.game.instance

import androidx.compose.ui.graphics.Color
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.core.math.Vec2
import com.mc.gameengine.core.math.clamp
import com.mc.gameengine.core.math.div
import com.mc.gameengine.core.math.minus
import com.mc.gameengine.core.math.plus
import com.mc.gameengine.core.math.times
import com.mc.gameengine.engine.input.AccelerometerEvent
import com.mc.gameengine.engine.input.InputEvent
import com.mc.gameengine.engine.input.InputListener
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer

class MovingSphere : Instance(), InputListener {

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

    override fun onInput(event: InputEvent) {
        when (event) {
            is AccelerometerEvent -> {
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
            position = state.position,
            radius = 50f,
            pivot = Pivot.Center,
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