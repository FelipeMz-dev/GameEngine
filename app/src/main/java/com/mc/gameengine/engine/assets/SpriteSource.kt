package com.mc.gameengine.engine.assets

import androidx.compose.ui.graphics.ImageBitmap
import com.mc.gameengine.engine.math.Vec2

interface SpriteSource {
    val frameWidth: Int
    val frameHeight: Int
}

data class AtlasSprite(
    val image: ImageBitmap,
    override val frameWidth: Int = image.width,
    override val frameHeight: Int = image.height,
) : SpriteSource

data class FrameListSprite(
    val images: List<ImageBitmap>,
    override val frameWidth: Int = images.firstOrNull()?.width ?: 1,
    override val frameHeight: Int = images.firstOrNull()?.height ?: 1,
) : SpriteSource

data class SingleImageSprite(
    val image: ImageBitmap,
    override val frameWidth: Int = image.width,
    override val frameHeight: Int = image.height,
) : SpriteSource

