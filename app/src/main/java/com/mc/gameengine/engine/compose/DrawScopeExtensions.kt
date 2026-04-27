package com.mc.gameengine.engine.compose

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toIntSize
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.minus
import com.mc.gameengine.engine.math.resolve
import com.mc.gameengine.engine.math.times
import com.mc.gameengine.engine.math.toOffset
import com.mc.gameengine.engine.math.toSize

internal fun DrawScope.drawSpriteInternal(
    image: ImageBitmap,
    state: TransformState = TransformState(),
    colorFilter: ColorFilter? = null
) {
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
            rotate(state.angle, pivotOffset.toOffset())
            scale(flipScaleX, flipScaleY, pivotOffset.toOffset())
        }
    ) {
        drawImage(
            image = image,
            dstSize = dstSize,
            colorFilter = colorFilter
        )
    }
}