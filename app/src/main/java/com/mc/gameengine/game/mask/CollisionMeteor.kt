package com.mc.gameengine.game.mask

import com.mc.gameengine.engine.collision.BoxCollider
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.game.assets.SpritesMain

class CollisionMeteor(owner: Instance): BoxCollider(
    owner = owner,
    width = SpritesMain.meteor.spriteWidth / 1.5f,
    height = SpritesMain.meteor.spriteHeight / 4f
)