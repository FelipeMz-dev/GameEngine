package com.mc.gameengine.game.assets

import com.mc.gameengine.engine.assets.SpriteManager

fun SpriteManager.registerMainSprites() = this.apply {
    load(SpritesMain.entries)
    load(SpritesDinoPlayer.entries)
}