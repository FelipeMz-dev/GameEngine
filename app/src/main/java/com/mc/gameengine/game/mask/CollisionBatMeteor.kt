package com.mc.gameengine.game.mask

import com.mc.gameengine.engine.collision.BoxCollider
import com.mc.gameengine.engine.collision.EllipseCollider
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.game.assets.SpritesDinoPlayer

class CollisionBatMeteor(owner: Instance) : BoxCollider(
    owner = owner,
    width = (SpritesDinoPlayer.walk.srcSize?.x ?: 0f) / 1.5f,
    height = (SpritesDinoPlayer.walk.srcSize?.y ?: 0f) / 5.5f,
)

class MagnetCollider(owner: Instance) : EllipseCollider(
    owner = owner,
    width = 400f,
    height = 400f
)