package com.mc.gameengine.game.mask

import com.mc.gameengine.engine.collision.BoxCollider
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.game.assets.SpritesDinoPlayer

class CollisionBatMeteor(owner: Instance) : BoxCollider(
    owner = owner,
    width = SpritesDinoPlayer.walk.spriteWidth / 5.5f,
    height = SpritesDinoPlayer.walk.spriteHeight / 1.5f
)