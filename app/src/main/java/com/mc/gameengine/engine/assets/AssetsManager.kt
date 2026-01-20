package com.mc.gameengine.engine.assets

import com.mc.gameengine.core.math.Vec2
import com.mc.gameengine.core.rendering.AtlasSpriteDef
import com.mc.gameengine.core.rendering.FrameListSpriteDef
import com.mc.gameengine.core.rendering.SingleImageSpriteDef
import com.mc.gameengine.core.rendering.SpriteDefinition
import com.mc.gameengine.engine.core.SpriteId
import com.mc.gameengine.engine.render.ImageLoader

class AssetsManager(private val imageLoader: ImageLoader) {

    private val sprites = mutableMapOf<SpriteId, SpriteSource>()

    fun load(defs: List<SpriteDefinition>) {
        defs.forEach { load(it) }
    }

    fun load(def: SpriteDefinition) {
        when (def) {
            is AtlasSpriteDef -> loadAtlas(def)
            is FrameListSpriteDef -> loadFrameList(def)
            is SingleImageSpriteDef -> {
                val image = imageLoader.loadRes(def.resId)
                sprites[def.spriteId] = SingleImageSprite(image)
            }
        }
    }

    fun get(id: SpriteId): SpriteSource = sprites[id] ?: error("Sprite '$id' not found")

    private fun loadAtlas(def: AtlasSpriteDef) {
        val image = imageLoader.loadRes(def.resId)

        sprites[def.spriteId] = AtlasSprite(
            image = image,
            columns = def.columns,
            rows = def.rows,
            offsetX = def.offsetX,
            offsetY = def.offsetY,
            spriteWidth = def.spriteWidth,
            spriteHeight = def.spriteHeight,
            spacingX = def.spacingX,
            spacingY = def.spacingY
        )
    }

    private fun loadFrameList(def: FrameListSpriteDef) {
        val frames = def.resIds.map { imageLoader.loadRes(it) }

        sprites[def.spriteId] = FrameListSprite(
            frames = frames,
            offsetX = def.offsetX,
            offsetY = def.offsetY
        )
    }

    fun getSize(id: SpriteId): Vec2 {
        val sprite = get(id)
        return Vec2(
            sprite.frameWidth.toFloat(),
            sprite.frameHeight.toFloat()
        )
    }
}