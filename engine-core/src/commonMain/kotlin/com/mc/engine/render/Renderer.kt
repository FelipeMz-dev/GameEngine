package com.mc.engine.render

import com.mc.engine.assets.Sprite
import com.mc.engine.core.SpriteId
import com.mc.engine.core.TransformState
import com.mc.engine.graphics.GpuColor
import com.mc.engine.math.Vec2

/**
 * Interfaz agnóstica de plataforma para renderizado.
 * No depende de Compose ni de Android, permitiendo múltiples implementaciones.
 */
interface Renderer {

    fun flush()

    fun clear(color: GpuColor)

    fun drawPoints(
        points: List<Vec2>,
        state: TransformState,
        strokeWidth: Float = 1f,
        deep: Int = 0,
        color: GpuColor = GpuColor.Gray
    )

    fun drawRect(
        size: Vec2,
        state: TransformState,
        style: RenderStyle = RenderStyle.Fill,
        deep: Int = 0,
        color: GpuColor = GpuColor.Gray
    )

    fun drawCircle(
        radius: Float,
        state: TransformState,
        deep: Int = 0,
        style: RenderStyle = RenderStyle.Fill,
        color: GpuColor = GpuColor.Gray
    )

    fun drawOval(
        size: Vec2,
        state: TransformState,
        style: RenderStyle = RenderStyle.Fill,
        deep: Int = 0,
        color: GpuColor = GpuColor.Gray
    )

    fun drawImage(
        spriteId: SpriteId,
        position: Vec2,
        size: Vec2,
        deep: Int = 0
    )

    fun drawLine(
        start: Vec2,
        end: Vec2,
        state: TransformState,
        strokeWidth: Float,
        deep: Int = 0,
        color: GpuColor
    )

    fun drawPolygon(
        points: List<Vec2>,
        state: TransformState,
        style: RenderStyle = RenderStyle.Fill,
        deep: Int = 0,
        color: GpuColor = GpuColor.Gray
    )

    fun drawSprite(
        spriteId: SpriteId,
        state: TransformState,
        frame: Int = 0,
        deep: Int = 0,
        color: GpuColor = GpuColor.White,
        blendMode: RenderBlendMode = RenderBlendMode.Modulate
    )

    fun Sprite.draw(
        deep: Int = 0,
        color: GpuColor = GpuColor.White,
        blendMode: RenderBlendMode = RenderBlendMode.Modulate
    )

    fun drawText(
        text: String,
        position: Vec2,
        rotation: Float = 0f,
        pivot: Pivot = Pivot.TopLeft,
        overflow: TextOverflow = TextOverflow.Clip,
        softWrap: Boolean = true,
        maxLines: Int = Int.MAX_VALUE,
        size: Vec2 = Vec2.Zero,
        style: TextRenderStyle = TextRenderStyle.default(),
        deep: Int = 0
    )

    fun drawBackground(
        sprite: SpriteId,
        frame: Int = 0,
        state: TransformState,
        contentScale: ContentScaleMode = ContentScaleMode.Fit,
    )

    fun drawForeground(
        sprite: SpriteId,
        frame: Int = 0,
        state: TransformState,
        contentScale: ContentScaleMode = ContentScaleMode.Fit,
    )

    fun drawInfiniteImage(
        spriteId: SpriteId,
        parallaxFactor: Float = 1f,
        contentScale: ContentScaleMode = ContentScaleMode.Fit,
        deep: Int = RenderDepth.BACKGROUND
    )

    fun drawAxis(
        position: Vec2,
        deep: Int = 0
    )
}

/**
 * Estilos de render agnósticos de plataforma.
 */
sealed class RenderStyle {
    object Fill : RenderStyle()
    data class Stroke(val width: Float) : RenderStyle()
}

/**
 * Modos de blending agnósticos de plataforma.
 */
enum class RenderBlendMode {
    Modulate,
    Screen,
    Multiply,
    Plus,
}

/**
 * Modos de escala de contenido agnósticos de plataforma.
 */
enum class ContentScaleMode {
    Fit,
    Crop,
    FillBounds,
    FillHeight,
    FillWidth,
    Inside,
    None,
}

/**
 * Over-flow de texto agnóstico de plataforma.
 */
enum class TextOverflow {
    Clip,
    Ellipsis,
    Visible,
}

/**
 * Estilo de texto agnóstico de plataforma.
 */
data class TextRenderStyle(
    val fontSize: Float = 16f,
    val fontSizeUnit: FontSizeUnit = FontSizeUnit.Sp,
    val color: GpuColor = GpuColor.Black,
) {
    companion object {
        fun default() = TextRenderStyle()
    }
}

enum class FontSizeUnit {
    Sp,
    Px,
    Em,
}
