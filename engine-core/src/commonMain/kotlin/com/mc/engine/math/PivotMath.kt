package com.mc.engine.math

import com.mc.engine.render.Pivot

fun Pivot.resolve(size: Vec2) = this.resolve(size, flipX = false, flipY = false)

fun Pivot.resolve(
    size: Vec2,
    flipX: Boolean,
    flipY: Boolean
): Vec2 {
    val width = size.x
    val height = size.y

    return when (this) {
        Pivot.Center -> Vec2(width / 2f, height / 2f)

        Pivot.Top -> Vec2(
            x = width / 2f,
            y = if (flipY) height else 0f
        )

        Pivot.Bottom -> Vec2(
            x = width / 2f,
            y = if (flipY) 0f else height
        )

        Pivot.Left -> Vec2(
            x = if (flipX) width else 0f,
            y = height / 2f
        )

        Pivot.Right -> Vec2(
            x = if (flipX) 0f else width,
            y = height / 2f
        )

        Pivot.TopLeft -> Vec2(
            x = if (flipX) width else 0f,
            y = if (flipY) height else 0f
        )

        Pivot.TopRight -> Vec2(
            x = if (flipX) 0f else width,
            y = if (flipY) height else 0f
        )

        Pivot.BottomLeft -> Vec2(
            x = if (flipX) width else 0f,
            y = if (flipY) 0f else height
        )

        Pivot.BottomRight -> Vec2(
            x = if (flipX) 0f else width,
            y = if (flipY) 0f else height
        )

        is Pivot.Custom -> Vec2(
            x = if (flipX) width - x else x,
            y = if (flipY) height - y else y
        )
    }
}