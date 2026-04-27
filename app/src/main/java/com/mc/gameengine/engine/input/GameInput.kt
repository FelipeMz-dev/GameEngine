package com.mc.gameengine.engine.input

import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.input.keyboard.KeyboardListener
import com.mc.gameengine.engine.input.keyboard.KeyboardManager
import com.mc.gameengine.engine.input.keyboard.KeyboardProcessor
import com.mc.gameengine.engine.input.mouse.MouseListener
import com.mc.gameengine.engine.input.mouse.MouseManager
import com.mc.gameengine.engine.input.mouse.MouseProcessor
import com.mc.gameengine.engine.input.sensor.SensorListener
import com.mc.gameengine.engine.input.sensor.SensorManager
import com.mc.gameengine.engine.input.sensor.SensorProcessor
import com.mc.gameengine.engine.input.touch.TouchListener
import com.mc.gameengine.engine.input.touch.TouchManager
import com.mc.gameengine.engine.input.touch.TouchProcessor

class GameInput private constructor(
    val touchProcessor: TouchProcessor?,
    val sensorProcessor: SensorProcessor?,
    val keyboardProcessor: KeyboardProcessor?,
    val mouseProcessor: MouseProcessor?
) {
    class Builder {
        private var touchProcessor: TouchProcessor? = null
        private var sensorProcessor: SensorProcessor? = null
        private var keyboardProcessor: KeyboardProcessor? = null
        private var mouseProcessor: MouseProcessor? = null

        fun withTouch(manager: TouchManager) = apply {
            this.touchProcessor = TouchProcessor(manager)
        }

        fun withSensor(manager: SensorManager) = apply {
            this.sensorProcessor = SensorProcessor(manager)
        }

        fun withKeyboard(manager: KeyboardManager) = apply {
            this.keyboardProcessor = KeyboardProcessor(manager)
        }

        fun withMouse(manager: MouseManager) = apply {
            this.mouseProcessor = MouseProcessor(manager)
        }

        fun build() = GameInput(
            touchProcessor,
            sensorProcessor,
            keyboardProcessor,
            mouseProcessor
        )
    }

    fun register(instance: Instance) {
        if (instance is TouchListener) {
            touchProcessor?.touchManager?.register(instance)
        }
        if (instance is SensorListener) {
            sensorProcessor?.sensorManager?.register(instance)
        }
        if (instance is KeyboardListener) {
            keyboardProcessor?.keyboardManager?.register(instance)
        }
        if (instance is MouseListener) {
            mouseProcessor?.mouseManager?.register(instance)
        }
    }

    fun unregister(instance: Instance) {
        if (instance is TouchListener) {
            touchProcessor?.touchManager?.unregister(instance)
        }
        if (instance is SensorListener) {
            sensorProcessor?.sensorManager?.unregister(instance)
        }
        if (instance is KeyboardListener) {
            keyboardProcessor?.keyboardManager?.unregister(instance)
        }
        if (instance is MouseListener) {
            mouseProcessor?.mouseManager?.unregister(instance)
        }
    }
}