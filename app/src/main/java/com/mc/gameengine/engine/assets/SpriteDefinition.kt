package com.mc.gameengine.engine.assets

import com.mc.gameengine.engine.core.SpriteId
import com.mc.gameengine.engine.math.Vec2

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
    override val hasAlpha: Boolean = true,
    override val srcOffset: Vec2? = null,
    override val srcSize: Vec2? = null,
    val srcSpacing: Vec2? = null,
) : SpriteDefinition {
    override val totalFrames = columns * rows
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