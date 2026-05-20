package com.mc.engine.input

import com.mc.engine.math.Vec2

/**
 * Interfaz agnóstica para procesamiento de entrada táctil.
 */
interface TouchProcessor {
    fun start()
    fun stop()
    fun getTouches(): List<TouchEvent>
}

/**
 * Interfaz agnóstica para procesamiento de sensores.
 */
interface SensorProcessor {
    fun start()
    fun stop()
    fun getAcceleration(): Vec2
    fun getRotation(): Vec2
}

/**
 * Interfaz agnóstica para procesamiento de teclado.
 */
interface KeyboardProcessor {
    fun start()
    fun stop()
    fun getPressedKeys(): Set<KeyEvent>
}

/**
 * Interfaz agnóstica para procesamiento de mouse.
 */
interface MouseProcessor {
    fun start()
    fun stop()
    fun getMousePosition(): Vec2
    fun getMouseButtons(): Set<MouseButton>
}
