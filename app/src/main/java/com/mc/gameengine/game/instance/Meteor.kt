package com.mc.gameengine.game.instance

import com.mc.gameengine.core.math.Vec2
import com.mc.gameengine.core.math.plus
import com.mc.gameengine.core.math.times
import com.mc.gameengine.engine.assets.Sprite
import com.mc.gameengine.engine.collision.BoxCollider
import com.mc.gameengine.engine.collision.Collider
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer
import com.mc.gameengine.game.assets.SpritesMain

class Meteor(private val x: Float = 0f): Instance() {

    companion object {
        const val COLLISION_METEOR = "meteor"
    }

    private var spriteMeteor = Sprite(SpritesMain.meteor)

    private lateinit var collision: Collider

    override fun onEnterScene() {
        current = current.copy(Vec2(x, 0f))
        physics = physics.copy(Vec2(0f, 100f))
        collision = BoxCollider(
            COLLISION_METEOR,
            this,
            Vec2.Zero,
            spriteMeteor.size().let {
                it.copy(it.x / 1.5f, it.y / 4f)
            },
            Pivot.Center
        )
        addCollider(collision)
    }

    override fun fixedUpdate(dt: Float) {
        current = current.copy(current.position + physics.velocity * dt)
        if (current.position.y > 370f) explosion()
    }

    override fun update(dt: Float) {
        spriteMeteor.update(dt)
        if (spriteMeteor.spriteIs(SpritesMain.METEOR_DESTRUCTION)){
            println(spriteMeteor.currentFrame)
            if (spriteMeteor.isLastFrame()) {
                deleteInstance(this)
            }
        }
    }

    override fun Renderer.onRender(state: TransformState) {

        /*drawAABB(
            aabb = collision.bounds(current.position),
            color = Color.LightGray
        )*/

        drawSprite(
            sprite = spriteMeteor.spriteId,
            frame = spriteMeteor.currentFrame,
            position = current.position,
            pivot = Pivot.Custom(
                x = spriteMeteor.size().x / 2f,
                y = spriteMeteor.size().y / 4f
            ),
        )
    }

    private fun explosion(){
        current = current.copy(current.position.copy(y = 370f))
        physics = physics.copy(Vec2.Zero)
        spriteMeteor = Sprite(SpritesMain.meteorDestruction, frameDuration = 0.01f)
    }
}

/*
* Camera2D:
* Backgrounds / Foregrounds
* Parallax + loop infinito
* Tile layers
* ContentScale
* */
