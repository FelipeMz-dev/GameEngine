package com.mc.engine.assets

import com.mc.engine.core.SpriteId
import com.mc.engine.math.Vec2

sealed interface SpriteDefinition {
    val spriteId: SpriteId
    val totalFrames: Int
    val srcOffset: Vec2?
    val srcSize: Vec2?
    val hasAlpha: Boolean
}

data class AtlasSpriteDef(
    override val spriteId: SpriteId,
    val resId: Int,
    val columns: Int,
    val rows: Int,
    val spriteWidth: Int? = null,
    val spriteHeight: Int? = null,
    val offsetX: Int = 0,
    val offsetY: Int = 0,
    val spacingX: Int = 0,
    val spacingY: Int = 0,
    override val hasAlpha: Boolean = true,
    override val srcOffset: Vec2? = null,
    override val srcSize: Vec2? = null,
    val srcSpacing: Vec2? = null,
) : SpriteDefinition {
    override val totalFrames = columns * rows

    val resolvedOffset: Vec2
        get() = srcOffset ?: Vec2(offsetX, offsetY)

    val resolvedFrameSize: Vec2
        get() = srcSize ?: Vec2(spriteWidth ?: 0, spriteHeight ?: 0)

    val resolvedSpacing: Vec2
        get() = srcSpacing ?: Vec2(spacingX, spacingY)
}

data class FrameListSpriteDef(
    override val spriteId: SpriteId,
    val resIds: List<Int>,
    override val hasAlpha: Boolean = true,
    override val srcOffset: Vec2? = null,
    override val srcSize: Vec2? = null
) : SpriteDefinition {
    override val totalFrames: Int = resIds.size
}

data class SingleImageSpriteDef(
    override val spriteId: SpriteId,
    val resId: Int,
    override val hasAlpha: Boolean = true,
    override val srcOffset: Vec2? = null,
    override val srcSize: Vec2? = null
) : SpriteDefinition {
    override val totalFrames: Int = 1
}
