package com.mc.gameengine.game.assets

import com.mc.gameengine.engine.assets.AssetsManager

fun AssetsManager.registerMainSprites() = this.apply {
    load(SpritesMain.entries)
    load(SpritesDinoPlayer.bat)
    load(SpritesDinoPlayer.walk)
}