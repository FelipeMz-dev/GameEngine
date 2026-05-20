package com.mc.engine.graphics

/**
 * Representación agnóstica de plataforma para desplazamientos 2D.
 */
data class GpuOffset(
    val x: Float,
    val y: Float
) {
    companion object {
        val Zero = GpuOffset(0f, 0f)
    }
}

