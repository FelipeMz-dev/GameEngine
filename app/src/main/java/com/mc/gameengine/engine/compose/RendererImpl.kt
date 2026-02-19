package com.mc.gameengine.engine.compose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntRect
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.div
import com.mc.gameengine.engine.math.minus
import com.mc.gameengine.engine.math.plus
import com.mc.gameengine.engine.math.times
import com.mc.gameengine.engine.math.toOffset
import com.mc.gameengine.engine.math.toSize
import com.mc.gameengine.engine.math.toVec2
import com.mc.gameengine.engine.assets.AssetsManager
import com.mc.gameengine.engine.assets.AtlasSprite
import com.mc.gameengine.engine.assets.FrameListSprite
import com.mc.gameengine.engine.assets.SingleImageSprite
import com.mc.gameengine.engine.core.SpriteId
import com.mc.gameengine.engine.math.resolve
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer

class RendererImpl(
    private val drawScope: DrawScope,
    private val spriteManager: AssetsManager,
    private val textMeasurer: TextMeasurer,
    private var camera: Camera2D
) : Renderer {

    private val commands = mutableListOf<RenderCommand>()

    override fun flush() {
        commands.sortBy { it.order }
        commands.forEach { cmd ->
            with(drawScope) {
                withTransform(
                    transformBlock = {
                        translate(camera.position.x, camera.position.y)
                        scale(camera.zoom, camera.zoom)
                        rotate(camera.rotation)
                        translate(-camera.position.x, -camera.position.y)
                    }
                ) { cmd.draw(this) }
            }
        }
        commands.clear()
    }

    override fun clear(
        color: Color,
        deep: Int
    ) {
        commands += RenderCommand(deep) {
            drawRect(color)
        }
    }

    override fun drawLine(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        strokeWidth: Float,
        color: Color,
        deep: Int
    ) {
        commands += RenderCommand(deep) {
            drawLine(
                color = color,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = strokeWidth
            )
        }
    }

    override fun drawRect(
        position: Vec2,
        size: Vec2,
        angle: Float,
        scale: Vec2,
        pivot: Pivot,
        color: Color,
        style: DrawStyle,
        deep: Int
    ) {
        val pivotOffset = pivot.resolve(size)
        val topLeft = (position - pivotOffset)
        val scaledSize = (size * scale).toSize()
        commands += RenderCommand(deep) {
            withTransform(
                {
                    rotate(angle, position.toOffset())
                    translate(topLeft.x, topLeft.y)
                }
            ) {
                drawRect(
                    color = color,
                    size = scaledSize,
                    style = style
                )
            }
        }
    }

    override fun drawCircle(
        position: Vec2,
        radius: Float,
        angle: Float,
        pivot: Pivot,
        color: Color,
        style: DrawStyle,
        deep: Int
    ) {
        val size = Size(radius * 2, radius * 2)
        val pivotOffset = pivot.resolve(size.toVec2())
        val offset = (position + radius) - pivotOffset
        commands += RenderCommand(deep) {
            withTransform(
                {
                    translate(position.x, position.y)
                    rotate(angle, pivotOffset.toOffset())
                    translate(-pivotOffset.x, -pivotOffset.y)
                }
            ) {
                drawCircle(
                    center = offset.toOffset(),
                    color = color,
                    radius = radius,
                    style = style
                )
            }
        }
    }

    override fun drawOval(
        position: Vec2,
        size: Vec2,
        angle: Float,
        scale: Vec2,
        pivot: Pivot,
        color: Color,
        style: DrawStyle,
        deep: Int
    ) {
        val pivotOffset = pivot.resolve(size)
        val topLeft = (position - pivotOffset)
        val scaledSize = (size * scale).toSize()
        commands += RenderCommand(deep) {
            withTransform(
                {
                    rotate(angle, position.toOffset())
                    translate(topLeft.x, topLeft.y)
                }
            ) {
                drawOval(
                    color = color,
                    size = scaledSize,
                    style = style
                )
            }
        }
    }

    override fun drawPolygon(
        position: Vec2,
        points: List<Vec2>,
        color: Color,
        angle: Float,
        pivot: Pivot,
        scale: Vec2,
        style: DrawStyle,
        deep: Int
    ) {
        val path = Path().apply {
            points.forEachIndexed { index, item ->
                if (index == 0) moveTo(item.x, item.y)
                else lineTo(item.x, item.y)
            }
            close()
        }
        val width = points.maxOf { it.x } - points.minOf { it.x }
        val height = points.maxOf { it.y } - points.minOf { it.y }
        val pivotOffset = pivot.resolve(Vec2(width, height))
        commands += RenderCommand(deep) {
            withTransform(
                {
                    translate(position.x, position.y)
                    translate(-pivotOffset.x, -pivotOffset.y)
                    scale(scale.x, scale.y, pivotOffset.toOffset())
                    rotate(angle, pivotOffset.toOffset())
                }
            ) {
                drawPath(
                    path = path,
                    color = color,
                    style = style
                )
            }
        }
    }

    override fun drawImage(
        spriteId: SpriteId,
        position: Vec2,
        size: Size,
        deep: Int
    ) {
        val spriteSize = size.toVec2() / spriteManager.getSize(spriteId)
        drawSprite(
            sprite = spriteId,
            frame = 1,
            position = position,
            rotation = 0f,
            scale = spriteSize,
            deep = deep
        )
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
        flipY: Boolean,
        deep: Int
    ) = when (val sprite = spriteManager.get(sprite)) {
        is AtlasSprite -> drawAtlasSprite(
            atlas = sprite,
            frame = frame,
            position = position,
            rotation = rotation,
            scale = scale,
            color = color,
            pivot = pivot,
            flipX = flipX,
            flipY = flipY,
            deep = deep
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
            flipY = flipY,
            deep = deep
        )

        is SingleImageSprite -> drawImageSprite(
            sprite = sprite,
            position = position,
            rotation = rotation,
            scale = scale,
            color = color,
            pivot = pivot,
            flipX = flipX,
            flipY = flipY,
            deep = deep
        )

        else -> Unit
    }

    private fun drawImageSprite(
        sprite: SingleImageSprite,
        position: Vec2,
        rotation: Float,
        scale: Vec2,
        color: Color,
        pivot: Pivot,
        flipX: Boolean,
        flipY: Boolean,
        deep: Int
    ) {
        commands += RenderCommand(deep) {
            drawSpriteInternal(
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
        flipY: Boolean,
        deep: Int
    ) {
        val col = frame % atlas.columns
        val row = frame / atlas.columns

        val src = IntRect(
            left = atlas.offsetX + col * (atlas.spriteWidth + atlas.spacingX),
            top = atlas.offsetY + row * (atlas.spriteHeight + atlas.spacingY),
            right = atlas.offsetX + col * (atlas.spriteWidth + atlas.spacingX) + atlas.spriteWidth,
            bottom = atlas.offsetY + row * (atlas.spriteHeight + atlas.spacingY) + atlas.spriteHeight
        )
        commands += RenderCommand(deep) {
            drawSpriteInternal(
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
        flipY: Boolean,
        deep: Int
    ) {
        val image = sprite.frames.getOrNull(frame % sprite.frames.size) ?: return

        commands += RenderCommand(deep) {
            drawSpriteInternal(
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
        style: TextStyle,
        deep: Int
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

        val finalSize = Vec2(
            x = if (size.width > 0f) size.width else textLayoutResult.size.width.toFloat(),
            y = if (size.height > 0f) size.height else textLayoutResult.size.height.toFloat()
        )

        val pivotOffset = pivot.resolve(finalSize)

        commands += RenderCommand(deep) {
            withTransform(
                transformBlock = {
                    translate(position.x, position.y)
                    rotate(rotation, pivotOffset.toOffset())
                    translate(-pivotOffset.x, -pivotOffset.y)
                }
            ) {
                drawText(textLayoutResult)
            }
        }
    }

    override fun drawAxis(
        position: Vec2,
        deep: Int
    ) {
        commands += RenderCommand(deep) {
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

    override fun drawBackground(
        sprite: SpriteId,
        position: Vec2,
        scale: Vec2,
        config: ParallaxConfig
    ) {
        drawParallaxLayer(
            sprite = sprite,
            basePosition = position,
            baseScale = scale,
            config = config,
            depth = RenderDepth.BACKGROUND
        )
    }

    override fun drawForeground(
        sprite: SpriteId,
        position: Vec2,
        scale: Vec2,
        config: ParallaxConfig
    ) {
        drawParallaxLayer(
            sprite = sprite,
            basePosition = position,
            baseScale = scale,
            config = config,
            depth = RenderDepth.FOREGROUND
        )
    }

    private fun drawParallaxLayer(
        sprite: SpriteId,
        basePosition: Vec2,
        baseScale: Vec2,
        config: ParallaxConfig,
        depth: Int
    ) = with(camera) {
        val spriteBaseSize = spriteManager.getSize(sprite)

        val contentScale = config.contentScale.computeScaleFactor(
            srcSize = spriteBaseSize.toSize(),
            dstSize = viewportSize.toSize()
        ).let { Vec2(it.scaleX, it.scaleY) }

        val finalScale = baseScale * contentScale
        val spriteSize = spriteBaseSize * finalScale

        val baseX = if (config.loopX) infiniteOffset(spriteSize.x, position.x, config.parallax)
        else position.x * (1f - config.parallax)

        val baseY = if (config.loopY) infiniteOffset(spriteSize.y, position.y, config.parallax)
        else basePosition.y - position.y * (1f - config.parallax)

        val tilesX = if (config.loopX) (viewportSize.x / spriteSize.x).toInt() + 3 else 1

        val tilesY = if (config.loopY) (viewportSize.y / spriteSize.y).toInt() + 3 else 1

        repeat(tilesX) { ix ->
            repeat(tilesY) { iy ->

                val finalPosition = Vec2(
                    baseX + ix * spriteSize.x,
                    baseY + iy * spriteSize.y
                )

                drawSprite(
                    sprite = sprite,
                    frame = 1,
                    position = finalPosition,
                    scale = finalScale,
                    deep = depth
                )
            }
        }
    }
}

data class ParallaxConfig(
    val parallax: Float = 1f,
    val loopX: Boolean = false,
    val loopY: Boolean = false,
    val contentScale: ContentScale = ContentScale.Fit
)

object RenderDepth {
    const val BACKGROUND = -10_000
    const val FOREGROUND = 10_000
    const val DEBUG = 10_002
    const val UI = 10_001
}

private fun infiniteOffset(
    spriteSize: Float,
    cameraPos: Float,
    parallax: Float
): Float {
    val effective = cameraPos * parallax
    return -(effective % spriteSize)
}

data class Camera2D(
    var position: Vec2 = Vec2.Zero,
    var zoom: Float = 1f,
    var rotation: Float = 0f,
    var viewportSize: Vec2 = Vec2.Zero
)