package com.mc.engine.core

/**
 * Interfaz agnóstica para una escena de juego.
 */
interface GameSceneInterface {
    fun update(deltaTime: Float)
    fun fixedUpdate(deltaTime: Float)
}
