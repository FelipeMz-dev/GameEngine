package com.mc.engine.compose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.Constraints
import com.mc.engine.assets.Sprite
import com.mc.engine.assets.SpriteManager
import com.mc.engine.core.Camera2D
import com.mc.engine.core.SpriteId
import com.mc.engine.core.TransformState
import com.mc.engine.graphics.GpuColor
import com.mc.engine.graphics.compose.toCompose
import com.mc.engine.math.Vec2
import com.mc.engine.math.div
import com.mc.engine.math.minus
import com.mc.engine.math.plus
import com.mc.engine.math.rem
import com.mc.engine.math.resolve
import com.mc.engine.math.times
import com.mc.engine.math.toGpuOffset
import com.mc.engine.math.toGpuSize
import com.mc.engine.render.ContentScaleMode
import com.mc.engine.render.Pivot
import com.mc.engine.render.RenderBlendMode
import com.mc.engine.render.RenderStyle
import com.mc.engine.render.Renderer
import com.mc.engine.render.TextRenderStyle

class RendererImpl(
    private val drawScope: DrawScope,
    private val spriteManager: SpriteManager,
    private val textMeasurer: TextMeasurer,
    private var camera: Camera2D
) : Renderer {

    private val commands = mutableListOf<RenderCommand>()

    override fun flush() {
        val cameraScale = camera.zoom.value
        val cameraScaleFrom = camera.zoom.from.toGpuOffset().toCompose()
        val cameraRotationFrom = camera.rotation.point.toGpuOffset().toCompose()

        commands.sortBy { it.order }
        with(drawScope) {
            withTransform(
                transformBlock = {
                    scale(cameraScale, cameraScale, cameraScaleFrom)
                    rotate(camera.rotation.angle, cameraRotationFrom)
                    translate(camera.position.x, camera.position.y)
                }
            ) { commands.forEach { it.draw(this) } }
        }
        commands.clear()
    }

    override fun clear(color: GpuColor) {
        commands += RenderCommand(RenderDepth.BACKGROUND) {
            val size = this.size
            drawRect(
                topLeft = Offset(-size.width, -size.height),
                color = color.toCompose()
            )
        }
    }

    private fun DrawScope.drawGird() {
        for (i in 1 until drawScope.size.width.toInt()) {
            val pos = i * 32f
            drawLine(
                start = Offset(pos, 0f),
                end = Offset(pos, drawScope.size.height),
                strokeWidth = 1f,
                color = Color.DarkGray
            )
        }
        for (i in 1 until drawScope.size.height.toInt()) {
            val pos = i * 32f
            drawLine(
                start = Offset(0f, pos),
                end = Offset(drawScope.size.width, pos),
                strokeWidth = 1f,
                color = Color.DarkGray
            )
        }
    }

    override fun drawPoints(
        points: List<Vec2>,
        state: TransformState,
        strokeWidth: Float,
        deep: Int,
        color: GpuColor
    ) {
        val sizeX = points.minOf { it.x } - points.minOf { it.x }
        val sizeY = points.minOf { it.y } - points.maxOf { it.y }
        val size = Vec2(sizeX, sizeY)
        val pivotOffset = state.pivot.resolve(size)
        val topLeft = (state.position - pivotOffset)
        commands += RenderCommand(RenderDepth.DEBUG) {
            withTransform(
                {
                    scale(state.scale.x, state.scale.y, pivotOffset.toGpuOffset().toCompose())
                    rotate(state.angle, pivotOffset.toGpuOffset().toCompose())
                    translate(topLeft.x, topLeft.y)
                }
            ) {
                this.drawPoints(
                    points = points.map { it.toGpuOffset().toCompose() },
                    pointMode = PointMode.Points,
                    color = color.toCompose(),
                    strokeWidth = strokeWidth
                )
            }
        }
    }

    override fun drawLine(
        start: Vec2,
        end: Vec2,
        state: TransformState,
        strokeWidth: Float,
        deep: Int,
        color: GpuColor
    ) {
        val sizeX = minOf(start.x, end.x)
        val sizeY = minOf(start.y, end.y)
        val size = Vec2(sizeX, sizeY)
        val pivotOffset = state.pivot.resolve(size)
        val topLeft = (state.position - pivotOffset)
        commands += RenderCommand(RenderDepth.DEBUG) {
            withTransform(
                {
                    scale(state.scale.x, state.scale.y, pivotOffset.toGpuOffset().toCompose())
                    rotate(state.angle, pivotOffset.toGpuOffset().toCompose())
                    translate(topLeft.x, topLeft.y)
                }
            ) {
                drawLine(
                    color = color.toCompose(),
                    start = start.toGpuOffset().toCompose(),
                    end = end.toGpuOffset().toCompose(),
                    strokeWidth = strokeWidth
                )
            }
        }
    }

    override fun drawRect(
        size: Vec2,
        state: TransformState,
        style: RenderStyle,
        deep: Int,
        color: GpuColor
    ) {
        val pivotOffset = state.pivot.resolve(size)
        val topLeft = (state.position - pivotOffset)
        val scaledSize = (size * state.scale).toGpuSize().toCompose()
        commands += RenderCommand(deep) {
            withTransform(
                {
                    rotate(state.angle, state.position.toGpuOffset().toCompose())
                    translate(topLeft.x, topLeft.y)
                }
            ) {
                drawRect(
                    color = color.toCompose(),
                    size = scaledSize,
                    style = style.toCompose()
                )
            }
        }
    }

    override fun drawCircle(
        radius: Float,
        state: TransformState,
        deep: Int,
        style: RenderStyle,
        color: GpuColor
    ) {
        val size = Vec2(radius, radius) * 2f
        drawOval(
            size = size,
            state = state,
            style = style,
            deep = deep,
            color = color
        )
    }

    override fun drawOval(
        size: Vec2,
        state: TransformState,
        style: RenderStyle,
        deep: Int,
        color: GpuColor
    ) {
        val pivotOffset = state.pivot.resolve(size)
        val topLeft = (state.position - pivotOffset)
        val scaledSize = (size * state.scale).toGpuSize().toCompose()
        commands += RenderCommand(deep) {
            withTransform(
                {
                    rotate(state.angle, state.position.toGpuOffset().toCompose())
                    translate(topLeft.x, topLeft.y)
                }
            ) {
                drawOval(
                    color = color.toCompose(),
                    size = scaledSize,
                    style = style.toCompose()
                )
            }
        }
    }

    override fun drawPolygon(
        points: List<Vec2>,
        state: TransformState,
        style: RenderStyle,
        deep: Int,
        color: GpuColor
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
        val pivotOffset = state.pivot.resolve(Vec2(width, height))
        commands += RenderCommand(deep) {
            withTransform(
                {
                    translate(state.position.x, state.position.y)
                    translate(-pivotOffset.x, -pivotOffset.y)
                    scale(state.scale.x, state.scale.y, pivotOffset.toGpuOffset().toCompose())
                    rotate(state.angle, pivotOffset.toGpuOffset().toCompose())
                }
            ) {
                drawPath(
                    path = path,
                    color = color.toCompose(),
                    style = style.toCompose()
                )
            }
        }
    }

    override fun drawImage(
        spriteId: SpriteId,
        position: Vec2,
        size: Vec2,
        deep: Int
    ) {
        val spriteSize = size / spriteManager.getSize(spriteId)
        drawSprite(
            spriteId = spriteId,
            frame = 0,
            state = TransformState(
                position = position,
                angle = 0f,
                scale = spriteSize
            ),
            deep = deep
        )
    }

    override fun drawSprite(
        spriteId: SpriteId,
        state: TransformState,
        frame: Int,
        deep: Int,
        color: GpuColor,
        blendMode: RenderBlendMode
    ) {
        val img = spriteManager.get(spriteId).frameAt(frame)

        img?.also {
            commands += RenderCommand(deep) {
                drawSpriteInternal(
                    image = img,
                    state = state,
                    colorFilter = ColorFilter.tint(color.toCompose(), blendMode.toCompose())
                )
            }
        }
    }

    override fun Sprite.draw(
        deep: Int,
        color: GpuColor,
        blendMode: RenderBlendMode
    ) {
        drawSprite(
            spriteId = spriteId,
            frame = currentFrame,
            state = state,
            deep = deep,
            color = color,
            blendMode = blendMode
        )
    }

    override fun drawText(
        text: String,
        position: Vec2,
        rotation: Float,
        pivot: Pivot,
        overflow: com.mc.engine.render.TextOverflow,
        softWrap: Boolean,
        maxLines: Int,
        size: Vec2,
        style: TextRenderStyle,
        deep: Int
    ) {
        val textLayoutResult = textMeasurer.measure(
            text = text,
            style = style.toCompose(),
            overflow = overflow.toCompose(),
            softWrap = softWrap,
            maxLines = maxLines,
            constraints = Constraints(
                maxWidth = if (size.x > 0f) size.x.toInt() else Int.MAX_VALUE,
                maxHeight = if (size.y > 0f) size.y.toInt() else Int.MAX_VALUE
            )
        )

        val finalSize = Vec2(
            x = if (size.x > 0f) size.x else textLayoutResult.size.width.toFloat(),
            y = if (size.y > 0f) size.y else textLayoutResult.size.height.toFloat()
        )

        val pivotOffset = pivot.resolve(finalSize)

        commands += RenderCommand(deep) {
            withTransform(
                transformBlock = {
                    translate(position.x, position.y)
                    rotate(rotation, pivotOffset.toGpuOffset().toCompose())
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
                start = position.copy(x = position.x - 50f).toGpuOffset().toCompose(),
                end = position.copy(x = position.x + 50f).toGpuOffset().toCompose(),
                strokeWidth = 2f,
                color = Color.DarkGray
            )
            drawLine(
                start = position.copy(y = position.y - 50f).toGpuOffset().toCompose(),
                end = position.copy(y = position.y + 50f).toGpuOffset().toCompose(),
                strokeWidth = 2f,
                color = Color.DarkGray
            )
        }
    }

    override fun drawBackground(
        sprite: SpriteId,
        frame: Int,
        state: TransformState,
        contentScale: ContentScaleMode
    ) {
        val spriteBaseSize = spriteManager.getSize(sprite)

        val contentScale = contentScale.toCompose().computeScaleFactor(
            srcSize = spriteBaseSize.toGpuSize().toCompose(),
            dstSize = camera.viewportSize.toGpuSize().toCompose()
        ).let { Vec2(it.scaleX, it.scaleY) }

        drawSprite(
            spriteId = sprite,
            frame = frame,
            state = state.copy(scale = state.scale * contentScale),
            deep = RenderDepth.BACKGROUND
        )
    }

    override fun drawForeground(
        sprite: SpriteId,
        frame: Int,
        state: TransformState,
        contentScale: ContentScaleMode
    ) {
        val spriteBaseSize = spriteManager.getSize(sprite)

        val contentScale = contentScale.toCompose().computeScaleFactor(
            srcSize = spriteBaseSize.toGpuSize().toCompose(),
            dstSize = camera.viewportSize.toGpuSize().toCompose()
        ).let { Vec2(it.scaleX, it.scaleY) }

        drawSprite(
            spriteId = sprite,
            frame = frame,
            state = state.copy(scale = state.scale * contentScale),
            deep = RenderDepth.FOREGROUND
        )
    }

    override fun drawInfiniteImage(
        spriteId: SpriteId,
        parallaxFactor: Float,
        contentScale: ContentScaleMode,
        deep: Int
    ) {
        val spriteSize = spriteManager.getSize(spriteId)
        if (spriteSize == Vec2.Zero) return

        val contentScale = contentScale.toCompose().computeScaleFactor(
            srcSize = spriteSize.toGpuSize().toCompose(),
            dstSize = camera.viewportSize.toGpuSize().toCompose()
        ).let { Vec2(it.scaleX, it.scaleY) }

        val scaledSize = spriteSize * contentScale
        val movement = camera.position * (0f - parallaxFactor)
        val offset = ((movement % scaledSize) + scaledSize) % scaledSize
        val startDraw = Vec2.Zero - camera.position - offset

        val tilesX = (camera.viewportSize.x / scaledSize.x).toInt() + 2
        val tilesY = (camera.viewportSize.y / scaledSize.y).toInt() + 2

        for (i in -1..tilesX) {
            for (j in -1..tilesY) {
                val posX = startDraw.x + (i * scaledSize.x)
                val posY = startDraw.y + (j * scaledSize.y)

                drawSprite(
                    spriteId = spriteId,
                    frame = 1,
                    state = TransformState(
                        position = Vec2(posX, posY),
                        angle = 0f,
                        scale = contentScale
                    ),
                    deep = deep
                )
            }
        }
    }
}
