package com.mc.gameengine.engine.assets

import androidx.compose.ui.graphics.ImageBitmap

sealed interface SpriteSource {
    val frameWidth: Int
    val frameHeight: Int
    val totalFrames: Int

    fun frameAt(index: Int): ImageBitmap?
    fun firstFrame(): ImageBitmap? = frameAt(0)

    data class FrameListSprite(
        val images: List<ImageBitmap>,
        override val frameWidth: Int = images.firstOrNull()?.width ?: 1,
        override val frameHeight: Int = images.firstOrNull()?.height ?: 1,
    ) : SpriteSource {
        override val totalFrames: Int = images.size

        override fun frameAt(index: Int): ImageBitmap? {
            if (images.isEmpty()) return null
            val normalizedIndex = index.mod(images.size)
            return images.getOrNull(normalizedIndex)
        }
    }

    data class SingleImageSprite(
        val image: ImageBitmap,
        override val frameWidth: Int = image.width,
        override val frameHeight: Int = image.height,
    ) : SpriteSource {
        override val totalFrames: Int = 1

        override fun frameAt(index: Int): ImageBitmap = image
    }
}
