package com.mc.gameengine.game.sprite

import com.mc.gameengine.engine.assets.Sprite
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.game.assets.SpritesMain

class SpriteMeteor : Sprite(
    sprite = SpritesMain.meteor,
    state = TransformState()
)

class SpriteExplosion : Sprite(
    sprite = SpritesMain.meteorExplosion,
    state = TransformState(
        pivot = Pivot.Custom(
            x = (SpritesMain.meteorExplosion.srcSize?.x ?: 0f) / 2f,
            y = (SpritesMain.meteorExplosion.srcSize?.y ?: 0f) / 3.5f
        )
    )
)