package com.mc.gameengine.engine.render

import androidx.compose.ui.geometry.Size

sealed class VirtualResolution(val width: Float, val height: Float) {
    object Portrait : VirtualResolution(360f, 640f)
    object Landscape : VirtualResolution(480f, 270f)
    object LandscapeHD : VirtualResolution(720f, 480f)
    object LandscapeFHD : VirtualResolution(1080f, 720f)
    object Square : VirtualResolution(640f, 640f)
    object PixelArt : VirtualResolution(320f, 180f)
    object Undefined : VirtualResolution(0f, 0f)
    data class Custom(val size: Size) : VirtualResolution(size.width, size.height)
    fun toSize() = Size(width, height)
    fun aspectRatio() = width / height
}