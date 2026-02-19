package com.mc.gameengine.engine.input

import com.mc.gameengine.engine.core.Instance

class GameInput private constructor(
    val touchProcessor: TouchProcessor?,
    val sensorProcessor: SensorProcessor?,
    val keyboardProcessor: KeyboardProcessor?
) {
    class Builder {
        private var touchProcessor: TouchProcessor? = null
        private var sensorProcessor: SensorProcessor? = null
        private var keyboardProcessor: KeyboardProcessor? = null

        fun withTouch(manager: TouchManager) = apply {
            this.touchProcessor = TouchProcessor(manager)
        }

        fun withSensor(manager: SensorManager) = apply {
            this.sensorProcessor = SensorProcessor(manager)
        }

        fun withKeyboard(manager: KeyboardManager) = apply {
            this.keyboardProcessor = KeyboardProcessor(manager)
        }

        fun build() = GameInput(
            touchProcessor,
            sensorProcessor,
            keyboardProcessor
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
    }
}