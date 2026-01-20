package com.mc.gameengine.engine.render

sealed class Pivot {
    object Center : Pivot()
    object Top : Pivot()
    object Bottom : Pivot()
    object Left : Pivot()
    object Right : Pivot()
    object TopLeft : Pivot()
    object TopRight : Pivot()
    object BottomLeft : Pivot()
    object BottomRight : Pivot()
    data class Custom(val x: Float, val y: Float) : Pivot()
}