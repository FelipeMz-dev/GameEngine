package com.mc.engine.compose

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.IntSize
import com.mc.engine.core.TransformState
import com.mc.engine.graphics.GpuImage
import com.mc.engine.graphics.compose.toImageBitmap
import com.mc.engine.graphics.compose.toCompose
import com.mc.engine.math.Vec2
import com.mc.engine.math.minus
import com.mc.engine.math.resolve
import com.mc.engine.math.times
import com.mc.engine.math.toGpuOffset

internal fun DrawScope.drawSpriteInternal(
    image: GpuImage,
    state: TransformState = TransformState(),
    colorFilter: ColorFilter? = null
) {
    val imageBitmap = image.toImageBitmap()
    val size = Vec2(image.width, image.height)
    val scaledSize = (size * state.scale)
    val pivotOffset = state.pivot.resolve(scaledSize, state.flipX, state.flipY)

    val flipScaleX = if (state.flipX) -1f else 1f
    val flipScaleY = if (state.flipY) -1f else 1f

    val topLeft = (state.position - pivotOffset)

    val dstSize = IntSize(width = scaledSize.x.toInt(), height = scaledSize.y.toInt())

    withTransform(
        transformBlock = {
            translate(topLeft.x, topLeft.y)
            rotate(state.angle, pivotOffset.toGpuOffset().toCompose())
            scale(flipScaleX, flipScaleY, pivotOffset.toGpuOffset().toCompose())
        }
    ) {
        drawImage(
            image = imageBitmap,
            dstSize = dstSize,
            colorFilter = colorFilter
        )
    }
}

