package com.mc.engine.graphics

import kotlin.math.roundToInt

/**
 * Representación agnóstica de plataforma para colores.
 * Los valores están en rango [0.0, 1.0] para compatibilidad universal.
 */
data class GpuColor(
    val red: Float,
    val green: Float,
    val blue: Float,
    val alpha: Float = 1f
) {
    init {
        require(red in 0f..1f) { "red debe estar entre 0 y 1" }
        require(green in 0f..1f) { "green debe estar entre 0 y 1" }
        require(blue in 0f..1f) { "blue debe estar entre 0 y 1" }
        require(alpha in 0f..1f) { "alpha debe estar entre 0 y 1" }
    }

    /**
     * Convierte a formato ARGB de 32 bits para Android.
     */
    fun toArgb(): Int {
        val a = (alpha * 255).roundToInt()
        val r = (red * 255).roundToInt()
        val g = (green * 255).roundToInt()
        val b = (blue * 255).roundToInt()
        return (a shl 24) or (r shl 16) or (g shl 8) or b
    }

    /**
     * Crea una copia con alfa modificado.
     */
    fun withAlpha(alpha: Float): GpuColor = copy(alpha = alpha)

    companion object {
        val White = GpuColor(1f, 1f, 1f, 1f)
        val Black = GpuColor(0f, 0f, 0f, 1f)
        val Red = GpuColor(1f, 0f, 0f, 1f)
        val Green = GpuColor(0f, 1f, 0f, 1f)
        val Blue = GpuColor(0f, 0f, 1f, 1f)
        val Gray = GpuColor(0.5f, 0.5f, 0.5f, 1f)
        val Transparent = GpuColor(0f, 0f, 0f, 0f)
        val Yellow = GpuColor(1f, 1f, 0f, 1f)
        val Cyan = GpuColor(0f, 1f, 1f, 1f)
        val Magenta = GpuColor(1f, 0f, 1f, 1f)

        /**
         * Crear color desde ARGB de 32 bits.
         */
        fun fromArgb(argb: Int): GpuColor {
            val a = ((argb shr 24) and 0xff) / 255f
            val r = ((argb shr 16) and 0xff) / 255f
            val g = ((argb shr 8) and 0xff) / 255f
            val b = (argb and 0xff) / 255f
            return GpuColor(r, g, b, a)
        }

        /**
         * Crear color desde RGB en rango [0-255].
         */
        fun fromRgb(r: Int, g: Int, b: Int, a: Int = 255): GpuColor {
            return GpuColor(r / 255f, g / 255f, b / 255f, a / 255f)
        }
    }
}


