package com.mc.gameengine.game.assets

import com.mc.gameengine.R
import com.mc.gameengine.engine.assets.AtlasSpriteDef
import com.mc.gameengine.engine.assets.SingleImageSpriteDef

object SpritesMain {
    const val EXAMPLE = "example"
    const val VOLCANO = "volcano"
    const val METEOR = "meteor"
    const val METEOR_EXPLOSION = "meteorExplosion"
    const val METEOR_DESTRUCTION = "meteorDestruction"
    const val BACKGROUND = "backIsland"
    const val FOREGROUND = "foreIsland"

    val example = AtlasSpriteDef(
        spriteId = EXAMPLE,
        resId = R.drawable.example,
        spriteWidth = 88,
        spriteHeight = 134,
        columns = 6,
        rows = 1,
        offsetX = 26,
        spacingX = 2
    )

    val volcano = AtlasSpriteDef(
        spriteId = VOLCANO,
        resId = R.drawable.sheet_volcano,
        spriteWidth = 360,
        spriteHeight = 720,
        columns = 4,
        rows = 1,
        offsetX = 10,
        offsetY = 150,
        spacingX = 2
    )

    val meteor = AtlasSpriteDef(
        spriteId = METEOR,
        resId = R.drawable.sheet_meteor,
        spriteWidth = 50,
        spriteHeight = 110,
        columns = 4,
        rows = 1,
        offsetX = 0,
        offsetY = 0,
        spacingX = 8
    )

    val meteorExplosion = AtlasSpriteDef(
        spriteId = METEOR_EXPLOSION,
        resId = R.drawable.explosion_meteor,
        spriteWidth = 214,
        spriteHeight = 178,
        columns = 3,
        rows = 3,
    )

    val meteorDestruction = AtlasSpriteDef(
        spriteId = METEOR_DESTRUCTION,
        resId = R.drawable.sheet_meteor_explosion,
        spriteWidth = 100,
        spriteHeight = 100,
        columns = 10,
        rows = 5,
        offsetX = 0,
        offsetY = 0,
    )


    val background = SingleImageSpriteDef(
        spriteId = BACKGROUND,
        resId = R.drawable.background_island
    )

    val foreground = SingleImageSpriteDef(
        spriteId = FOREGROUND,
        resId = R.drawable.foreground_island
    )

    val entries = listOf(
        example,
        volcano,
        meteor,
        background,
        foreground,
        meteorExplosion,
        meteorDestruction
    )
}

