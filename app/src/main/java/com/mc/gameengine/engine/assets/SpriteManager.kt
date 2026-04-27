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
        when (def) {
            is AtlasSpriteDef -> loadAtlas(def)
            is FrameListSpriteDef -> loadFrameList(def)
            is SingleImageSpriteDef -> loadSingleImage(def)
        }
    }

    fun get(id: SpriteId): SpriteSource = sprites[id] ?: error("Sprite '$id' not found")

    private fun loadAtlas(def: AtlasSpriteDef) {
        val images = mutableListOf<ImageBitmap>()

        repeat(def.totalFrames) { frame ->
            val col = frame % def.columns
            val row = frame / def.columns
            val offsetX = def.srcOffset?.x ?: 0f
            val offsetY = def.srcOffset?.y ?: 0f
            val width = def.srcSize?.x ?: 0f
            val height = def.srcSize?.y ?: 0f
            val spacingX = def.srcSpacing?.x ?: 0f
            val spacingY = def.srcSpacing?.y ?: 0f
            val startX = offsetX + col * (width + spacingX)
            val startY = offsetY + row * (height + spacingY)
            val image = imageLoader.loadRes(
                resId = def.resId,
                hasAlpha = def.hasAlpha,
                srcOffset = Vec2(startX, startY),
                srcSize = Vec2(width, height)
            )
            images.add(image)
        }

        sprites[def.spriteId] = FrameListSprite(images)
    }

    private fun loadFrameList(def: FrameListSpriteDef) {
        val images = def.resIds.map {
            imageLoader.loadRes(
                resId = it,
                hasAlpha = def.hasAlpha,
                srcOffset = def.srcOffset,
                srcSize = def.srcSize
            )
        }

        sprites[def.spriteId] = FrameListSprite(images)
    }

    private fun loadSingleImage(def: SingleImageSpriteDef) {
        val image = imageLoader.loadRes(
            resId = def.resId,
            hasAlpha = def.hasAlpha,
            srcOffset = def.srcOffset,
            srcSize = def.srcSize
        )
        sprites[def.spriteId] = FrameListSprite(listOf(image))
    }

    fun getSize(id: SpriteId): Vec2 {
        val sprite = get(id)
        return Vec2(
            sprite.frameWidth.toFloat(),
            sprite.frameHeight.toFloat()
        )
    }
}