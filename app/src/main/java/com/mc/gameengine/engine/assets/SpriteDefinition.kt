package com.mc.gameengine.engine.assets

import com.mc.gameengine.engine.core.SpriteId

sealed interface SpriteDefinition {
    val spriteId: SpriteId
    val totalFrames: Int
}

data class AtlasSpriteDef(
    override val spriteId: SpriteId,
    val resId: Int,
    val columns: Int,
    val rows: Int,
    val offsetX: Int = 0,
    val offsetY: Int = 0,
    val spriteWidth: Int,
    val spriteHeight: Int,
    val spacingX: Int = 0,
    val spacingY: Int = 0
) : SpriteDefinition {
    override val totalFrames = columns * rows - 1
}

data class FrameListSpriteDef(
    override val spriteId: SpriteId,
    val resIds: List<Int>,
    val offsetX: Int = 0,
    val offsetY: Int = 0
) : SpriteDefinition {
    override val totalFrames: Int = resIds.size
}

data class SingleImageSpriteDef(
    override val spriteId: SpriteId,
    val resId: Int,
    val offsetX: Int = 0,
    val offsetY: Int = 0
) : SpriteDefinition {
    override val totalFrames: Int = 0
}