package com.mc.gameengine.game.assets

import com.mc.gameengine.R
import com.mc.gameengine.engine.assets.AtlasSpriteDef
import com.mc.gameengine.engine.assets.SingleImageSpriteDef
import com.mc.gameengine.engine.core.SpriteId
import com.mc.gameengine.engine.math.Vec2

object SpritesDinoPlayer {
    const val BAT: SpriteId = "player_bat"
    const val WALK: SpriteId = "player_walk"
    const val HIT: SpriteId = "player_hit"

    val bat = AtlasSpriteDef(
        spriteId = BAT,
        resId = R.drawable.sheet_dino_bat,
        columns = 5,
        rows = 1,
        srcOffset = Vec2(38, 0),
        srcSize = Vec2(82, 100),
    )

    val walk = AtlasSpriteDef(
        spriteId = WALK,
        resId = R.drawable.sheet_dino_walk,
        columns = 4,
        rows = 7,
        srcOffset = Vec2(110, 60),
        srcSize = Vec2(82, 100),
        srcSpacing = Vec2(231, 83),
    )

    val hit = AtlasSpriteDef(
        spriteId = HIT,
        resId = R.drawable.spr_dino_hit,
        columns = 4,
        rows = 1,
        srcOffset = Vec2(3, 2),
        srcSize = Vec2(68, 98),
    )

    val entries = listOf(
        bat,
        walk,
        hit
    )
}