package com.mc.engine.input.sensor

import com.mc.engine.math.Vec2

/**
 * Interfaz agnóstica de plataforma para entrada de sensores.
 */
interface SensorSystem {

    /**
     * Inicia la detección de sensores.
     */
    fun start()

    /**
     * Detiene la detección de sensores.
     */
    fun stop()

    /**
     * Obtiene la aceleración actual del dispositivo (en m/s²).
     */
    fun getAcceleration(): Vec2

    /**
     * Obtiene la rotación actual del dispositivo (en rad/s).
     */
    fun getRotation(): Vec2
}
