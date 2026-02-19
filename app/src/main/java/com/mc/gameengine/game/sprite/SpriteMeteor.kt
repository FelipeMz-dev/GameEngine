package com.mc.gameengine.game.sprite

import com.mc.gameengine.engine.assets.Sprite
import com.mc.gameengine.engine.assets.SpriteMetrics
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.game.assets.SpritesMain

class SpriteMeteor : Sprite(
    sprite = SpritesMain.meteor,
    metrics = SpriteMetrics(
        pivot = Pivot.Custom(
            x = SpritesMain.meteor.spriteWidth / 2f,
            y = SpritesMain.meteor.spriteHeight / 4f
        ),
        deep = 1
    )
)

class SpriteExplosion : Sprite(
    sprite = SpritesMain.meteorExplosion,
    metrics = SpriteMetrics(
        pivot = Pivot.Custom(
            x = SpritesMain.meteorExplosion.spriteWidth / 2f,
            y = SpritesMain.meteorExplosion.spriteHeight / 3.5f
        ),
        deep = -1
    )
)