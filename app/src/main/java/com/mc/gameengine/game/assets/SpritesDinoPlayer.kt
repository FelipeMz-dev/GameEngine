package com.mc.gameengine.game.assets

import com.mc.gameengine.R
import com.mc.gameengine.engine.assets.AtlasSpriteDef
import com.mc.gameengine.engine.core.SpriteId

object SpritesDinoPlayer {
    const val BAT: SpriteId = "player_bat"
    const val IDLE: SpriteId = "player_idle"
    const val WALK: SpriteId = "player_walk"
    const val RUN: SpriteId = "player_run"

    val bat = AtlasSpriteDef(
        spriteId = BAT,
        resId = R.drawable.sheet_dino_bat,
        columns = 5,
        rows = 1,
        spriteWidth = 82,
        spriteHeight = 100,
        offsetY = 0,
        offsetX = 38
    )

    val walk = AtlasSpriteDef(
        spriteId = WALK,
        resId = R.drawable.sheet_dino_walk,
        columns = 4,
        rows = 7,
        spriteWidth = 82,
        spriteHeight = 100,
        spacingX = 231,
        spacingY = 83,
        offsetY = 60,
        offsetX = 110
    )
}