package com.mc.gameengine.engine.compose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.style.TextOverflow
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.div
import com.mc.gameengine.engine.math.minus
import com.mc.gameengine.engine.math.plus
import com.mc.gameengine.engine.math.times
import com.mc.gameengine.engine.math.toOffset
import com.mc.gameengine.engine.math.toSize
import com.mc.gameengine.engine.math.toVec2
import com.mc.gameengine.engine.assets.SpriteManager
import com.mc.gameengine.engine.assets.Sprite
import com.mc.gameengine.engine.core.SpriteId
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.math.rem
import com.mc.gameengine.engine.math.resolve
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer

class RendererImpl(
    private val drawScope: DrawScope,
    private val spriteManager: SpriteManager,
    private val textMeasurer: TextMeasurer,
    private var camera: Camera2D
) : Renderer {

    private val commands = mutableListOf<RenderCommand>()

    override fun flush() {
        val cameraScale = camera.zoom.value
        val cameraScaleFrom = camera.zoom.from.toOffset()
        val cameraRotationFrom = camera.rotation.from.toOffset()

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

    override fun clear(color: Color) {
        commands += RenderCommand(RenderDepth.BACKGROUND) {
            val size = this.size
            drawRect(
                topLeft = Offset(-size.width, -size.height),
                color = color
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
        color: Color
    ) {
        val sizeX = points.minOf { it.x } - points.minOf { it.x }
        val sizeY = points.minOf { it.y } - points.maxOf { it.y }
        val size = Vec2(sizeX, sizeY)
        val pivotOffset = state.pivot.resolve(size)
        val topLeft = (state.position - pivotOffset)
        commands += RenderCommand(RenderDepth.DEBUG) {
            withTransform(
                {
                    scale(state.scale.x, state.scale.y, pivotOffset.toOffset())
                    rotate(state.angle, pivotOffset.toOffset())
                    translate(topLeft.x, topLeft.y)
                }
            ) {
                this.drawPoints(
                    points = points.map { it.toOffset() },
                    pointMode = PointMode.Points,
                    color = color,
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
        color: Color
    ) {
        val sizeX = minOf(start.x, end.x)
        val sizeY = minOf(start.y, end.y)
        val size = Vec2(sizeX, sizeY)
        val pivotOffset = state.pivot.resolve(size)
        val topLeft = (state.position - pivotOffset)
        commands += RenderCommand(RenderDepth.DEBUG) {
            withTransform(
                {
                    scale(state.scale.x, state.scale.y, pivotOffset.toOffset())
                    rotate(state.angle, pivotOffset.toOffset())
                    translate(topLeft.x, topLeft.y)
                }
            ) {
                drawLine(
                    color = color,
                    start = start.toOffset(),
                    end = end.toOffset(),
                    strokeWidth = strokeWidth
                )
            }
        }
    }

    override fun drawRect(
        size: Vec2,
        state: TransformState,
        style: DrawStyle,
        deep: Int,
        color: Color
    ) {
        val pivotOffset = state.pivot.resolve(size)
        val topLeft = (state.position - pivotOffset)
        val scaledSize = (size * state.scale).toSize()
        commands += RenderCommand(deep) {
            withTransform(
                {
                    rotate(state.angle, state.position.toOffset())
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
        radius: Float,
        state: TransformState,
        deep: Int,
        style: DrawStyle,
        color: Color
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
        style: DrawStyle,
        deep: Int,
        color: Color
    ) {
        val pivotOffset = state.pivot.resolve(size)
        val topLeft = (state.position - pivotOffset)
        val scaledSize = (size * state.scale).toSize()
        commands += RenderCommand(deep) {
            withTransform(
                {
                    rotate(state.angle, state.position.toOffset())
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
        points: List<Vec2>,
        state: TransformState,
        style: DrawStyle,
        deep: Int,
        color: Color
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
                    scale(state.scale.x, state.scale.y, pivotOffset.toOffset())
                    rotate(state.angle, pivotOffset.toOffset())
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
        color: Color,
        blendMode: BlendMode
    ) {
        val img = spriteManager.get(spriteId).frameAt(frame)

        img?.also {
            commands += RenderCommand(deep) {
                drawSpriteInternal(
                    image = img,
                    state = state,
                    colorFilter = ColorFilter.tint(color, blendMode)
                )
            }
        }
    }

    override fun Sprite.draw(
        deep: Int,
        color: Color,
        blendMode: BlendMode
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
        frame: Int,
        state: TransformState,
        contentScale: ContentScale
    ) {
        val spriteBaseSize = spriteManager.getSize(sprite)

        val contentScale = contentScale.computeScaleFactor(
            srcSize = spriteBaseSize.toSize(),
            dstSize = camera.viewportSize.toSize()
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
        contentScale: ContentScale
    ) {
        val spriteBaseSize = spriteManager.getSize(sprite)

        val contentScale = contentScale.computeScaleFactor(
            srcSize = spriteBaseSize.toSize(),
            dstSize = camera.viewportSize.toSize()
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
        contentScale: ContentScale,
        deep: Int
    ) {
        val spriteSize = spriteManager.getSize(spriteId)
        if (spriteSize == Vec2.Zero) return

        val contentScale = contentScale.computeScaleFactor(
            srcSize = spriteSize.toSize(),
            dstSize = camera.viewportSize.toSize()
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
