package com.mc.engine.render

import com.mc.engine.graphics.GpuSize

/**
 * Resoluciones virtuales predefinidas para diferentes aspectos de pantalla.
 * Agnóstico de plataforma.
 */
sealed class VirtualResolution(val width: Float, val height: Float) {
    object Portrait : VirtualResolution(360f, 640f)
    object Landscape : VirtualResolution(480f, 270f)
    object LandscapeHD : VirtualResolution(720f, 480f)
    object LandscapeFHD : VirtualResolution(1080f, 720f)
    object Square : VirtualResolution(640f, 640f)
    object PixelArt : VirtualResolution(320f, 180f)
    object Undefined : VirtualResolution(0f, 0f)
    data class Custom(val size: GpuSize) : VirtualResolution(size.width, size.height)

    fun toGpuSize() = GpuSize(width, height)
    fun aspectRatio() = if (height != 0f) width / height else 0f
}
