package com.mc.gameengine.game.instance

import com.mc.gameengine.core.math.Vec2
import com.mc.gameengine.engine.assets.Sprite
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer
import com.mc.gameengine.game.assets.SpritesMain

class Explosion(private val position: Vec2) : Instance() {
    private val sprite = Sprite(SpritesMain.meteorDestruction)

    override fun update(dt: Float) {
        sprite.update(dt)
    }

    override fun Renderer.onRender(state: TransformState) {

        if (sprite.isLastFrame()) deleteInstance(this@Explosion)

        drawSprite(
            sprite = sprite.spriteId,
            frame = sprite.currentFrame,
            position = position,
            pivot = Pivot.Center
        )
    }
}