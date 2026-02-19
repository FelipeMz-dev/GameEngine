package com.mc.gameengine.engine.render

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.compose.ParallaxConfig
import com.mc.gameengine.engine.core.SpriteId

interface Renderer {

    fun flush()

    fun clear(
        color: Color,
        deep: Int = 0
    )

    fun drawRect(
        position: Vec2,
        size: Vec2,
        angle: Float = 0f,
        scale: Vec2 = Vec2.from(1f),
        pivot: Pivot = Pivot.TopLeft,
        color: Color = Color.Gray,
        style: DrawStyle = Fill,
        deep: Int = 0
    )

    fun drawCircle(
        position: Vec2,
        radius: Float,
        angle: Float = 0f,
        pivot: Pivot = Pivot.TopLeft,
        color: Color = Color.Gray,
        style: DrawStyle = Fill,
        deep: Int = 0
    )

    fun drawOval(
        position: Vec2,
        size: Vec2,
        angle: Float = 0f,
        scale: Vec2 = Vec2.from(1f),
        pivot: Pivot = Pivot.TopLeft,
        color: Color = Color.Gray,
        style: DrawStyle = Fill,
        deep: Int = 0
    )

    fun drawImage(
        spriteId: SpriteId,
        position: Vec2,
        size: Size,
        deep: Int = 0
    )

    fun drawLine(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        strokeWidth: Float,
        color: Color,
        deep: Int = 0
    )

    fun drawPolygon(
        position: Vec2,
        points: List<Vec2>,
        color: Color,
        angle: Float = 0f,
        pivot: Pivot = Pivot.TopLeft,
        scale: Vec2 = Vec2.from(1f),
        style: DrawStyle = Fill,
        deep: Int = 0
    )

    fun drawSprite(
        sprite: SpriteId,
        frame: Int,
        position: Vec2,
        rotation: Float = 0f,
        scale: Vec2 = Vec2(1f, 1f),
        color: Color = Color.White,
        pivot: Pivot = Pivot.TopLeft,
        flipX: Boolean = false,
        flipY: Boolean = false,
        deep: Int = 0
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
        position: Vec2 = Vec2.Zero,
        scale: Vec2 = Vec2.from(1f),
        config: ParallaxConfig = ParallaxConfig()
    )

    fun drawForeground(
        sprite: SpriteId,
        position: Vec2 = Vec2.Zero,
        scale: Vec2 = Vec2.from(1f),
        config: ParallaxConfig = ParallaxConfig()
    )

    fun drawAxis(
        position: Vec2,
        deep: Int = 0
    )
}