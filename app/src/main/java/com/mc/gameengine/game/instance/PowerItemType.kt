package com.mc.gameengine.game.instance

import com.mc.gameengine.engine.assets.SpriteDefinition
import com.mc.gameengine.game.assets.SpritesMain

enum class PowerItemType(val typeName: String, val sprite: SpriteDefinition) {
    MAGNET("Magnet", SpritesMain.magnet),
    HELMET("Helmet", SpritesMain.helmet),
    WARNING("Warning", SpritesMain.warning),
    BALL_MULTIPLIER("Ball Multiplier", SpritesMain.multiplier),
    PORTAL("Portal", SpritesMain.portalIcon),
    SLOW_TIME("Slow Time", SpritesMain.clock);
}