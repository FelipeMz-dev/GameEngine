package com.mc.gameengine.game.sprite

import com.mc.gameengine.engine.assets.Sprite
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.game.assets.SpritesDinoPlayer
import com.mc.gameengine.game.assets.SpritesMain

class SpriteDinoWalk() : Sprite(
    sprite = SpritesDinoPlayer.walk,
    state = TransformState(pivot = Pivot.Center)
)

class SpriteDinoBat() : Sprite(
    sprite = SpritesDinoPlayer.bat,
    state = TransformState(pivot = Pivot.Center),
    frameDuration = 0.04f
)

class SpriteDinoHit() : Sprite(
    sprite = SpritesDinoPlayer.hit,
    state = TransformState(pivot = Pivot.Center)
)

class SpriteHelmet() : Sprite(
    sprite = SpritesMain.helmet,
    state = TransformState(pivot = Pivot.Center, scale = Vec2.from(1.2f)),
)