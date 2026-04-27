package com.mc.gameengine.engine.math

import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Pivot.Bottom
import com.mc.gameengine.engine.render.Pivot.BottomLeft
import com.mc.gameengine.engine.render.Pivot.BottomRight
import com.mc.gameengine.engine.render.Pivot.Center
import com.mc.gameengine.engine.render.Pivot.Custom
import com.mc.gameengine.engine.render.Pivot.Left
import com.mc.gameengine.engine.render.Pivot.Right
import com.mc.gameengine.engine.render.Pivot.Top
import com.mc.gameengine.engine.render.Pivot.TopLeft
import com.mc.gameengine.engine.render.Pivot.TopRight

fun Pivot.resolve(size: Vec2) = this.resolve(size, flipX = false, flipY = false)

fun Pivot.resolve(
    size: Vec2,
    flipX: Boolean,
    flipY: Boolean
): Vec2 {
    val width = size.x
    val height = size.y

    return when (this) {
        Center -> Vec2(width / 2f, height / 2f)

        Top -> Vec2(
            x = width / 2f,
            y = if (flipY) height else 0f
        )

        Bottom -> Vec2(
            x = width / 2f,
            y = if (flipY) 0f else height
        )

        Left -> Vec2(
            x = if (flipX) width else 0f,
            y = height / 2f
        )

        Right -> Vec2(
            x = if (flipX) 0f else width,
            y = height / 2f
        )

        TopLeft -> Vec2(
            x = if (flipX) width else 0f,
            y = if (flipY) height else 0f
        )

        TopRight -> Vec2(
            x = if (flipX) 0f else width,
            y = if (flipY) height else 0f
        )

        BottomLeft -> Vec2(
            x = if (flipX) width else 0f,
            y = if (flipY) 0f else height
        )

        BottomRight -> Vec2(
            x = if (flipX) 0f else width,
            y = if (flipY) 0f else height
        )

        is Custom -> Vec2(
            x = if (flipX) width - x else x,
            y = if (flipY) height - y else y
        )
    }
}