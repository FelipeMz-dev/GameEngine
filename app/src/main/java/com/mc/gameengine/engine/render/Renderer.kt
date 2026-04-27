package com.mc.gameengine.engine.render

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.mc.gameengine.engine.assets.Sprite
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.compose.RenderDepth
import com.mc.gameengine.engine.core.SpriteId
import com.mc.gameengine.engine.core.TransformState

interface Renderer {

    fun flush()

    fun clear(color: Color)

    fun drawPoints(
        points: List<Vec2>,
        state: TransformState,
        strokeWidth: Float = 1f,
        deep: Int = 0,
        color: Color = Color.Gray
    )

    fun drawRect(
        size: Vec2,
        state: TransformState,
        style: DrawStyle = Fill,
        deep: Int = 0,
        color: Color = Color.Gray
    )

    fun drawCircle(
        radius: Float,
        state: TransformState,
        deep: Int = 0,
        style: DrawStyle = Fill,
        color: Color = Color.Gray
    )

    fun drawOval(
        size: Vec2,
        state: TransformState,
        style: DrawStyle = Fill,
        deep: Int = 0,
        color: Color = Color.Gray
    )

    fun drawImage(
        spriteId: SpriteId,
        position: Vec2,
        size: Size,
        deep: Int = 0
    )

    fun drawLine(
        start: Vec2,
        end: Vec2,
        state: TransformState,
        strokeWidth: Float,
        deep: Int = 0,
        color: Color
    )

    fun drawPolygon(
        points: List<Vec2>,
        state: TransformState,
        style: DrawStyle = Fill,
        deep: Int = 0,
        color: Color = Color.Gray
    )

    fun drawSprite(
        spriteId: SpriteId,
        state: TransformState,
        frame: Int = 1,
        deep: Int = 0,
        color: Color = Color.White,
        blendMode: BlendMode = BlendMode.Modulate
    )

    fun Sprite.draw(
        deep: Int = 0,
        color: Color = Color.White,
        blendMode: BlendMode = BlendMode.Modulate
    )

    fun drawText(
        text: String,
        position: Vec2,
        rotation: Float = 0f,
        pivot: Pivot = Pivot.TopLeft,
        overflow: TextOverflow = TextOverflow.Clip,
        softWrap: Boolean = true,
        maxLines: Int = Int.MAX_VALUE,
        size: Size = Size.Unspecified,
        style: TextStyle = TextStyle(
            fontSize = TextUnit(16f, TextUnitType.Sp),
            color = Color.Black
        ),
        deep: Int = 0
    )

    fun drawBackground(
        sprite: SpriteId,
        frame: Int = 1,
        state: TransformState,
        contentScale: ContentScale = ContentScale.Fit,
    )

    fun drawForeground(
        sprite: SpriteId,
        frame: Int = 1,
        state: TransformState,
        contentScale: ContentScale = ContentScale.Fit,
    )

    fun drawInfiniteImage(
        spriteId: SpriteId,
        parallaxFactor: Float = 1f,
        contentScale: ContentScale = ContentScale.Fit,
        deep: Int = RenderDepth.BACKGROUND
    )

    fun drawAxis(
        position: Vec2,
        deep: Int = 0
    )
}