package com.mc.gameengine.engine.core

internal object InstanceContextProvider {
    private var _current: WorldContext? = null

    val current: WorldContext
        get() = _current ?: error("Instance context not found. Did you instantiate this class manually? Use context.spawn { } instead.")

    /**
     * Ejecuta un bloque de código asegurando que el contexto esté disponible
     * para las instancias creadas dentro de él.
     */
    inline fun <T> runWithContext(context: WorldContext, block: () -> T): T {
        val previous = _current
        _current = context
        try {
            return block()
        } finally {
            _current = previous
        }
    }
}