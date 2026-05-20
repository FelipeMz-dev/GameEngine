package com.mc.engine.input

import com.mc.engine.core.Instance

class GameInput private constructor(
    val touchProcessor: TouchProcessor?,
    val sensorProcessor: SensorProcessor?,
    val keyboardProcessor: KeyboardProcessor?,
    val mouseProcessor: MouseProcessor?,
    private val bindings: List<InstanceBinding>
) {
    class Builder {
        private var touchProcessor: TouchProcessor? = null
        private var sensorProcessor: SensorProcessor? = null
        private var keyboardProcessor: KeyboardProcessor? = null
        private var mouseProcessor: MouseProcessor? = null

        private val bindingsByType = linkedMapOf<Class<*>, InstanceBinding>()

        fun withTouch(processor: TouchProcessor) = apply {
            this.touchProcessor = processor
        }

        fun withSensor(processor: SensorProcessor) = apply {
            this.sensorProcessor = processor
        }

        fun withKeyboard(processor: KeyboardProcessor) = apply {
            this.keyboardProcessor = processor
        }

        fun withMouse(processor: MouseProcessor) = apply {
            this.mouseProcessor = processor
        }

        fun build() = GameInput(
            touchProcessor = touchProcessor,
            sensorProcessor = sensorProcessor,
            keyboardProcessor = keyboardProcessor,
            mouseProcessor = mouseProcessor,
            bindings = bindingsByType.values.toList()
        )

        private fun <T : Any> bind(type: Class<T>, binding: InstanceBinding) {
            bindingsByType[type] = binding
        }
    }

    fun register(instance: Instance) {
        bindings.forEach { it.register(instance) }
    }

    fun unregister(instance: Instance) {
        bindings.forEach { it.unregister(instance) }
    }
}
