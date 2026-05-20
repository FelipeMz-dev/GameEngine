package com.mc.engine.render

/**
 * Interfaz agnóstica para el sistema de renderizado.
 */
interface RendererInterface {
    fun clear()
    fun flush()
}