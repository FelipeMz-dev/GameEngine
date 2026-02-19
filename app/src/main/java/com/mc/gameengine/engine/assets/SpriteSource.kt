package com.mc.gameengine.engine.assets

import androidx.compose.ui.graphics.ImageBitmap

interface SpriteSource {
    val frameWidth: Int
    val frameHeight: Int
}

data class AtlasSprite(
    val image: ImageBitmap,
    val columns: Int = 1,
    val rows: Int = 1,
    val offsetX: Int = 0,
    val offsetY: Int = 0,
    val spriteWidth: Int = image.width,
    val spriteHeight: Int = image.height,
    val spacingX: Int = 0,
    val spacingY: Int = 0
) : SpriteSource {
    override val frameWidth: Int = spriteWidth
    override val frameHeight: Int = spriteHeight
}

data class FrameListSprite(
    val frames: List<ImageBitmap>,
    val offsetX: Int = 0,
    val offsetY: Int = 0,
    val spriteWidth: Int = frames.firstOrNull()?.width ?: 0,
    val spriteHeight: Int = frames.firstOrNull()?.height ?: 0,
) : SpriteSource {
    override val frameWidth: Int = frames.firstOrNull()?.width ?: 0

    override val frameHeight: Int = frames.firstOrNull()?.height ?: 0
}

data class SingleImageSprite(
    val image: ImageBitmap,
    val offsetX: Int = 0,
    val offsetY: Int = 0,
    val spriteWidth: Int = image.width,
    val spriteHeight: Int = image.height,
) : SpriteSource {
    override val frameWidth = image.width
    override val frameHeight = image.height
}

