package com.mc.gameengine.game.instance

import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.plus
import com.mc.gameengine.engine.math.times
import com.mc.gameengine.engine.assets.Sprite
import com.mc.gameengine.engine.collision.BoxCollider
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer
import com.mc.gameengine.game.assets.SpritesMain
import com.mc.gameengine.game.mask.CollisionMeteor
import com.mc.gameengine.game.sprite.SpriteExplosion
import com.mc.gameengine.game.sprite.SpriteMeteor

class Meteor(private val x: Float = 0f) : Instance() {

    private var collision = CollisionMeteor(this)

    private var sprite: Sprite = SpriteMeteor()

    private var isExploding = false

    override fun onEnterScene() {
        current = current.copy(position = Vec2(x, 0f))
        physics = physics.copy(velocity = Vec2(0f, 200f))
        addCollider(collision)
    }

    override fun fixedUpdate(dt: Float) {
        collision.update { it.copy(position = current.position) }
        current = current.copy(position = current.position + physics.velocity * dt)
        sprite.update { it.copy(position = current.position) }
        if (current.position.y > 370f) explosion()
        if (isExploding && sprite.isLastFrame()) {
            sprite.stop()
            isExploding = false
        }
    }

    override fun update(dt: Float) {
        sprite.animate(dt)
    }

    override fun Renderer.onRender(state: TransformState) {
        drawSprite(
            sprite = sprite.spriteId,
            frame = sprite.currentFrame,
            position = sprite.metrics.position,
            scale = sprite.metrics.scale,
            pivot = sprite.metrics.pivot,
            deep = sprite.metrics.deep
        )
    }

    private fun explosion() {
        removeCollider(collision)
        current = current.copy(position = current.position.copy(y = 370f))
        physics = physics.copy(velocity = Vec2.Zero)
        sprite = SpriteExplosion()
        isExploding = true
    }
}

/*
* Camera2D:
* Backgrounds / Foregrounds
* Parallax + loop infinito
* Tile layers
* ContentScale
* */