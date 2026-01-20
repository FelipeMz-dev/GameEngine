package com.mc.gameengine.engine.math

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.mc.gameengine.engine.render.Pivot

fun Pivot.resolve(
    size: Size,
    flipX: Boolean = false,
    flipY: Boolean = false
): Offset {
    val width = size.width
    val height = size.height

    return when (this) {
        Pivot.Center -> Offset(width / 2f, height / 2f)

        Pivot.Top -> {
            val x = width / 2f
            val y = if (flipY) height else 0f
            Offset(x, y)
        }

        Pivot.Bottom -> {
            val x = width / 2f
            val y = if (flipY) 0f else height
            Offset(x, y)
        }

        Pivot.Left -> {
            val x = if (flipX) width else 0f
            val y = height / 2f
            Offset(x, y)
        }

        Pivot.Right -> {
            val x = if (flipX) 0f else width
            val y = height / 2f
            Offset(x, y)
        }

        Pivot.TopLeft -> {
            val x = if (flipX) width else 0f
            val y = if (flipY) height else 0f
            Offset(x, y)
        }

        Pivot.TopRight -> {
            val x = if (flipX) 0f else width
            val y = if (flipY) height else 0f
            Offset(x, y)
        }

        Pivot.BottomLeft -> {
            val x = if (flipX) width else 0f
            val y = if (flipY) 0f else height
            Offset(x, y)
        }

        Pivot.BottomRight -> {
            val x = if (flipX) 0f else width
            val y = if (flipY) 0f else height
            Offset(x, y)
        }

        is Pivot.Custom -> {
            val finalX = if (flipX) (1f - x) else x
            val finalY = if (flipY) (1f - y) else y
            Offset(width - finalX, height - finalY)
        }
    }
}