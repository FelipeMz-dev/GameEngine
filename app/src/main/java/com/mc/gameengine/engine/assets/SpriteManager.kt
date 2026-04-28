package com.mc.gameengine.engine.assets

import androidx.compose.ui.graphics.ImageBitmap
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.core.SpriteId
import com.mc.gameengine.engine.render.ImageLoader

class SpriteManager(private val imageLoader: ImageLoader) {

    private val sprites = mutableMapOf<SpriteId, SpriteSource>()

    fun load(defs: List<SpriteDefinition>) {
        defs.forEach { load(it) }
    }

    fun load(def: SpriteDefinition) {
        sprites[def.spriteId] = def.toSource()
    }

    fun get(id: SpriteId): SpriteSource = sprites[id] ?: error("Sprite '$id' not found")

    private fun loadAtlas(def: AtlasSpriteDef): SpriteSource {
        val images = mutableListOf<ImageBitmap>()
        val frameSize = def.resolvedFrameSize
        require(frameSize.x > 0f && frameSize.y > 0f) {
            "Atlas '${def.spriteId}' requires frame size > 0. " +
                "Use srcSize or spriteWidth/spriteHeight."
        }
        val offset = def.resolvedOffset
        val spacing = def.resolvedSpacing

        repeat(def.totalFrames) { frame ->
            val col = frame % def.columns
            val row = frame / def.columns
            val startX = offset.x + col * (frameSize.x + spacing.x)
            val startY = offset.y + row * (frameSize.y + spacing.y)
            val image = imageLoader.loadRes(
                resId = def.resId,
                hasAlpha = def.hasAlpha,
                srcOffset = Vec2(startX, startY),
                srcSize = frameSize
            )
            images.add(image)
        }

        return SpriteSource.FrameListSprite(images)
    }

    private fun loadFrameList(def: FrameListSpriteDef): SpriteSource {
        val images = def.resIds.map {
            imageLoader.loadRes(
                resId = it,
                hasAlpha = def.hasAlpha,
                srcOffset = def.srcOffset,
                srcSize = def.srcSize
            )
        }

        return SpriteSource.FrameListSprite(images)
    }

    private fun loadSingleImage(def: SingleImageSpriteDef): SpriteSource {
        val image = imageLoader.loadRes(
            resId = def.resId,
            hasAlpha = def.hasAlpha,
            srcOffset = def.srcOffset,
            srcSize = def.srcSize
        )
        return SpriteSource.SingleImageSprite(image)
    }

    fun getSize(id: SpriteId): Vec2 {
        val sprite = get(id)
        return Vec2(
            sprite.frameWidth.toFloat(),
            sprite.frameHeight.toFloat()
        )
    }

    private fun SpriteDefinition.toSource(): SpriteSource {
        return when (this) {
            is AtlasSpriteDef -> loadAtlas(this)
            is FrameListSpriteDef -> loadFrameList(this)
            is SingleImageSpriteDef -> loadSingleImage(this)
        }
    }
}
