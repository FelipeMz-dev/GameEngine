package com.mc.gameengine.game.mask

import com.mc.gameengine.engine.collision.BoxCollider
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.game.assets.SpritesMain

class CollisionMeteor(owner: Instance): BoxCollider(
    owner = owner,
    width = (SpritesMain.meteor.srcSize?.x ?: 0f) / 1.6f,
    height = (SpritesMain.meteor.srcSize?.y ?: 0f) / 4f
)

class CollisionExplosion(owner: Instance): BoxCollider(
    owner = owner,
    width = (SpritesMain.meteorExplosion.srcSize?.x ?: 0f) / 3f,
    height = (SpritesMain.meteorExplosion.srcSize?.y ?: 0f) / 3f
)