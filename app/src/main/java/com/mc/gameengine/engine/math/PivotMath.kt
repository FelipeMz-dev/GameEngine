package com.mc.gameengine.engine.math

import com.mc.gameengine.engine.render.Pivot

fun Pivot.resolve(
    size: Vec2,
    flipX: Boolean = false,
    flipY: Boolean = false
): Vec2 {
    val width = size.x
    val height = size.y

    return when (this) {
        Pivot.Center -> Vec2(width / 2f, height / 2f)

        Pivot.Top -> {
            val x = width / 2f
            val y = if (flipY) height else 0f
            Vec2(x, y)
        }

        Pivot.Bottom -> {
            val x = width / 2f
            val y = if (flipY) 0f else height
            Vec2(x, y)
        }

        Pivot.Left -> {
            val x = if (flipX) width else 0f
            val y = height / 2f
            Vec2(x, y)
        }

        Pivot.Right -> {
            val x = if (flipX) 0f else width
            val y = height / 2f
            Vec2(x, y)
        }

        Pivot.TopLeft -> {
            val x = if (flipX) width else 0f
            val y = if (flipY) height else 0f
            Vec2(x, y)
        }

        Pivot.TopRight -> {
            val x = if (flipX) 0f else width
            val y = if (flipY) height else 0f
            Vec2(x, y)
        }

        Pivot.BottomLeft -> {
            val x = if (flipX) width else 0f
            val y = if (flipY) 0f else height
            Vec2(x, y)
        }

        Pivot.BottomRight -> {
            val x = if (flipX) 0f else width
            val y = if (flipY) 0f else height
            Vec2(x, y)
        }

        is Pivot.Custom -> {
            val finalX = if (flipX) (1f - x) else x
            val finalY = if (flipY) (1f - y) else y
            Vec2(finalX, finalY)
        }
    }
}