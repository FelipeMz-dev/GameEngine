package com.mc.engine.graphics

/**
 * Representación agnóstica de plataforma para dimensiones.
 */
data class GpuSize(
    val width: Float,
    val height: Float
) {
    companion object {
        val Zero = GpuSize(0f, 0f)
        val Unspecified = GpuSize(Float.NaN, Float.NaN)
        
        fun square(side: Float) = GpuSize(side, side)
    }

    val isSpecified: Boolean
        get() = !width.isNaN() && !height.isNaN()

    val isUnspecified: Boolean
        get() = width.isNaN() || height.isNaN()
}

