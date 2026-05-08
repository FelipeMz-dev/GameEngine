package com.mc.gameengine.engine.input

import com.mc.gameengine.engine.core.GameObject
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
    val mouseProcessor: MouseProcessor?,
    private val bindings: List<InstanceBinding>
) {
    class Builder {
        private var touchProcessor: TouchProcessor? = null
        private var sensorProcessor: SensorProcessor? = null
        private var keyboardProcessor: KeyboardProcessor? = null
        private var mouseProcessor: MouseProcessor? = null

        private val bindingsByType = linkedMapOf<Class<*>, InstanceBinding>()

        fun withTouch(manager: TouchManager) = apply {
            this.touchProcessor = TouchProcessor(manager)
            bind(
                TouchListener::class.java,
                ListenerBinding(
                    listenerClass = TouchListener::class.java,
                    onRegister = manager::register,
                    onUnregister = manager::unregister
                )
            )
        }

        fun withSensor(manager: SensorManager) = apply {
            this.sensorProcessor = SensorProcessor(manager)
            bind(
                SensorListener::class.java,
                ListenerBinding(
                    listenerClass = SensorListener::class.java,
                    onRegister = manager::register,
                    onUnregister = manager::unregister
                )
            )
        }

        fun withKeyboard(manager: KeyboardManager) = apply {
            this.keyboardProcessor = KeyboardProcessor(manager)
            bind(
                KeyboardListener::class.java,
                ListenerBinding(
                    listenerClass = KeyboardListener::class.java,
                    onRegister = manager::register,
                    onUnregister = manager::unregister
                )
            )
        }

        fun withMouse(manager: MouseManager) = apply {
            this.mouseProcessor = MouseProcessor(manager)
            bind(
                MouseListener::class.java,
                ListenerBinding(
                    listenerClass = MouseListener::class.java,
                    onRegister = manager::register,
                    onUnregister = manager::unregister
                )
            )
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

    fun register(instance: GameObject) {
        bindings.forEach { it.register(instance) }
    }

    fun unregister(instance: GameObject) {
        bindings.forEach { it.unregister(instance) }
    }
}
