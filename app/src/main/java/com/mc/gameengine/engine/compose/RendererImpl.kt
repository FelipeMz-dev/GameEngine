package com.mc.gameengine.engine.compose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.mc.gameengine.core.math.Vec2
import com.mc.gameengine.core.math.div
import com.mc.gameengine.core.math.plus
import com.mc.gameengine.core.math.toOffset
import com.mc.gameengine.core.math.toSize
import com.mc.gameengine.core.math.toVec2
import com.mc.gameengine.engine.assets.AssetsManager
import com.mc.gameengine.engine.assets.AtlasSprite
import com.mc.gameengine.engine.assets.FrameListSprite
import com.mc.gameengine.engine.assets.SingleImageSprite
import com.mc.gameengine.engine.core.SpriteId
import com.mc.gameengine.engine.math.AABB
import com.mc.gameengine.engine.math.resolve
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer

class RendererImpl(
    private val drawScope: DrawScope,
    private val spriteManager: AssetsManager,
    private val textMeasurer: TextMeasurer
) : Renderer {

    override fun clear(color: Color) {
        with(drawScope) {
            drawRect(color)
        }
    }

    override fun drawRect(
        position: Vec2,
        size: Vec2,
        rotation: Float,
        pivot: Pivot,
        color: Color,
        style: DrawStyle,
    ) {
        val pivotOffset = pivot.resolve(size.toSize())
        with(drawScope) {
            withTransform(
                {
                    translate(position.x, position.y)
                    rotate(rotation, pivotOffset)
                    translate(-pivotOffset.x, -pivotOffset.y)
                }
            ) {
                drawRect(
                    color = color,
                    size = size.toSize()
                )
            }
        }
    }

    override fun drawRect(
        rect: Rect,
        pivot: Pivot,
        color: Color
    ) {
        val pivotOffset = pivot.resolve(rect.size)
        with(drawScope) {
            withTransform(
                {
                    translate(rect.topLeft.x, rect.topLeft.y)
                    translate(-pivotOffset.x, -pivotOffset.y)
                }
            ) {
                drawRect(
                    color = color,
                    size = rect.size
                )
            }
        }
    }

    override fun drawCircle(
        position: Vec2,
        radius: Float,
        pivot: Pivot,
        color: Color,
        style: DrawStyle,
    ) {
        val size = Size(radius * 2, radius * 2)
        val pivotOffset = pivot.resolve(size)
        val offset = (position + radius).toOffset() - pivotOffset
        with(drawScope) {
            drawCircle(
                center = offset,
                color = color,
                radius = radius
            )
        }
    }

    override fun TextMeasurer.drawText(
        text: String,
        x: Float,
        y: Float,
        fontSize: Float,
        color: Color
    ) {
        with(drawScope) {
            this.drawText(
                textMeasurer = this@drawText,
                text = text,
                topLeft = Offset(x, y),
                style = TextStyle(
                    fontSize = TextUnit(
                        value = fontSize,
                        type = TextUnitType.Companion.Sp
                    )
                )
            )
        }
    }

    override fun drawImage(
        spriteId: SpriteId,
        position: Vec2,
        size: Size
    ) {
        val spriteSize = size.toVec2() / spriteManager.getSize(spriteId)
        with(drawScope) {
            drawSprite(
                sprite = spriteId,
                frame = 1,
                position = position,
                rotation = 0f,
                scale = spriteSize
            )
        }
    }

    override fun drawLine(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        strokeWidth: Float,
        color: Color
    ) {
        with(drawScope) {
            drawLine(
                color = color,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = strokeWidth
            )
        }
    }

    override fun drawPolygon(
        points: List<Pair<Float, Float>>,
        fill: Boolean,
        color: Color
    ) {
        with(drawScope) {
            val path = Path().apply {
                if (points.isNotEmpty()) {
                    moveTo(points[0].first, points[0].second)
                    for (i in 1 until points.size) {
                        lineTo(points[i].first, points[i].second)
                    }
                    close()
                }
            }
            if (fill) {
                drawPath(
                    path = path,
                    color = color
                )
            } else {
                drawPath(
                    path = path,
                    color = color,
                    style = Stroke()
                )
            }
        }
    }

    override fun drawSprite(
        sprite: SpriteId,
        frame: Int,
        position: Vec2,
        rotation: Float,
        scale: Vec2,
        color: Color,
        pivot: Pivot,
        flipX: Boolean,
        flipY: Boolean
    ) {
        val sprite = spriteManager.get(sprite)

        return when (sprite) {
            is AtlasSprite -> drawAtlasSprite(
                atlas = sprite,
                frame = frame,
                position = position,
                rotation = rotation,
                scale = scale,
                color = color,
                pivot = pivot,
                flipX = flipX,
                flipY = flipY
            )

            is FrameListSprite -> drawFrameList(
                sprite = sprite,
                frame = frame,
                position = position,
                rotation = rotation,
                scale = scale,
                color = color,
                pivot = pivot,
                flipX = flipX,
                flipY = flipY

            )

            is SingleImageSprite -> drawImageSprite(
                sprite = sprite,
                position = position,
                rotation = rotation,
                scale = scale,
                color = color,
                pivot = pivot,
                flipX = flipX,
                flipY = flipY
            )

            else -> Unit
        }
    }

    private fun drawImageSprite(
        sprite: SingleImageSprite,
        position: Vec2,
        rotation: Float,
        scale: Vec2,
        color: Color,
        pivot: Pivot,
        flipX: Boolean,
        flipY: Boolean
    ) = with(drawScope) {
        drawScope.drawSpriteInternal(
            image = sprite.image,
            src = IntRect(
                left = sprite.offsetX,
                top = sprite.offsetY,
                right = sprite.offsetX + sprite.spriteWidth,
                bottom = sprite.offsetY + sprite.spriteHeight
            ),
            position = position,
            rotation = rotation,
            scale = scale,
            color = color,
            pivot = pivot,
            flipX = flipX,
            flipY = flipY
        )
    }

    private fun drawAtlasSprite(
        atlas: AtlasSprite,
        frame: Int,
        position: Vec2,
        rotation: Float,
        scale: Vec2,
        color: Color,
        pivot: Pivot,
        flipX: Boolean,
        flipY: Boolean
    ) = with(drawScope) {

        val col = frame % atlas.columns
        val row = frame / atlas.columns

        val src = IntRect(
            left = atlas.offsetX + col * (atlas.spriteWidth + atlas.spacingX),
            top = atlas.offsetY + row * (atlas.spriteHeight + atlas.spacingY),
            right = atlas.offsetX + col * (atlas.spriteWidth + atlas.spacingX) + atlas.spriteWidth,
            bottom = atlas.offsetY + row * (atlas.spriteHeight + atlas.spacingY) + atlas.spriteHeight
        )
        drawScope.drawSpriteInternal(
            image = atlas.image,
            src = src,
            position = position,
            rotation = rotation,
            scale = scale,
            color = color,
            pivot = pivot,
            flipX = flipX,
            flipY = flipY
        )
    }

    private fun drawFrameList(
        sprite: FrameListSprite,
        frame: Int,
        position: Vec2,
        rotation: Float,
        scale: Vec2,
        color: Color,
        pivot: Pivot,
        flipX: Boolean,
        flipY: Boolean
    ) = with(drawScope) {

        val image = sprite.frames.getOrNull(frame % sprite.frames.size) ?: return

        drawScope.drawSpriteInternal(
            image = image,
            src = IntRect(
                left = sprite.offsetX,
                top = sprite.offsetY,
                right = sprite.offsetX + sprite.spriteWidth,
                bottom = sprite.offsetY + sprite.spriteHeight
            ),
            position = position,
            rotation = rotation,
            scale = scale,
            color = color,
            pivot = pivot,
            flipX = flipX,
            flipY = flipY
        )
    }

    override fun drawText(
        text: String,
        position: Vec2,
        rotation: Float,
        pivot: Pivot,
        overflow: TextOverflow,
        softWrap: Boolean,
        maxLines: Int,
        size: Size,
        style: TextStyle
    ) {
        val textLayoutResult = textMeasurer.measure(
            text = text,
            style = style,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            constraints = androidx.compose.ui.unit.Constraints(
                maxWidth = if (size.width > 0f) size.width.toInt() else Int.MAX_VALUE,
                maxHeight = if (size.height > 0f) size.height.toInt() else Int.MAX_VALUE
            )
        )

        val finalSize = Size(
            width = if (size.width > 0f) size.width else textLayoutResult.size.width.toFloat(),
            height = if (size.height > 0f) size.height else textLayoutResult.size.height.toFloat()
        )

        val pivotOffset = pivot.resolve(finalSize)

        with(drawScope) {
            withTransform(
                transformBlock = {
                    translate(position.x, position.y)
                    rotate(rotation, pivotOffset)
                    translate(-pivotOffset.x, -pivotOffset.y)
                }
            ) {
                drawText(textLayoutResult)
            }
        }
    }

    override fun drawAxis(position: Vec2) {
        with(drawScope) {
            drawLine(
                start = position.copy(x = position.x - 50f).toOffset(),
                end = position.copy(x = position.x + 50f).toOffset(),
                strokeWidth = 2f,
                color = Color.DarkGray
            )
            drawLine(
                start = position.copy(y = position.y - 50f).toOffset(),
                end = position.copy(y = position.y + 50f).toOffset(),
                strokeWidth = 2f,
                color = Color.DarkGray
            )
        }
    }

    override fun drawAABB(
        aabb: AABB,
        color: Color
    ) {
        val rect = Rect(
            offset = aabb.position.toOffset(),
            size = aabb.size.toSize()
        )
        drawRect(
            rect = rect,
            pivot = Pivot.TopLeft,
            color = color
        )
    }
}