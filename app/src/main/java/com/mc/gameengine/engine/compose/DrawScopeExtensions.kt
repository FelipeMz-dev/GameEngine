package com.mc.gameengine.engine.compose

import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.IntRect
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.toOffset
import com.mc.gameengine.engine.math.resolve
import com.mc.gameengine.engine.render.Pivot

fun DrawScope.drawSpriteInternal(
    image: ImageBitmap,
    src: IntRect,
    position: Vec2,
    rotation: Float,
    scale: Vec2,
    color: Color,
    pivot: Pivot,
    flipX: Boolean,
    flipY: Boolean
) {
    val size = Vec2(src.width.toFloat(), src.height.toFloat())
    val pivotOffset = pivot.resolve(size, flipX, flipY)

    val finalScaleX = scale.x * if (flipX) -1f else 1f
    val finalScaleY = scale.y * if (flipY) -1f else 1f

    withTransform(
        transformBlock = {
            translate(position.x, position.y)
            translate(-pivotOffset.x, -pivotOffset.y)
            scale(finalScaleX, finalScaleY, pivotOffset.toOffset())
            rotate(rotation, pivotOffset.toOffset())
        }
    ) {
        drawImage(
            image = image,
            srcOffset = src.topLeft,
            srcSize = src.size,
            colorFilter = ColorFilter.tint(color, BlendMode.Modulate)
        )
    }
}