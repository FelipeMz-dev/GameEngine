package com.mc.gameengine.engine.render

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.mc.gameengine.core.math.Vec2
import com.mc.gameengine.engine.core.SpriteId
import com.mc.gameengine.engine.math.AABB

interface Renderer {
    fun clear(color: Color)

    fun drawRect(
        position: Vec2,
        size: Vec2,
        rotation: Float = 0f,
        pivot: Pivot = Pivot.TopLeft,
        color: Color = Color.Gray,
        style: DrawStyle = Fill,
    )

    fun drawRect(
        rect: Rect,
        pivot: Pivot,
        color: Color
    )

    fun drawCircle(
        position: Vec2,
        radius: Float,
        pivot: Pivot = Pivot.TopLeft,
        color: Color = Color.Gray,
        style: DrawStyle = Fill,
    )

    fun TextMeasurer.drawText(
        text: String,
        x: Float,
        y: Float,
        fontSize: Float,
        color: Color
    )

    fun drawImage(
        spriteId: SpriteId,
        position: Vec2,
        size: Size
    )

    fun drawLine(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        strokeWidth: Float,
        color: Color
    )

    fun drawPolygon(
        points: List<Pair<Float, Float>>,
        fill: Boolean,
        color: Color
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
        flipY: Boolean = false
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
        )
    )

    fun drawAxis(position: Vec2)
    fun drawAABB(
        aabb: AABB,
        color: Color
    )
}