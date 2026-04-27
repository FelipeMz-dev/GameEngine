package com.mc.gameengine.game.instance

import androidx.compose.ui.graphics.Color
import com.mc.gameengine.app.GameController
import com.mc.gameengine.engine.assets.Sprite
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.math.div
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer
import com.mc.gameengine.game.assets.MainAudios
import com.mc.gameengine.game.assets.SpritesMain
import com.mc.gameengine.game.effects.FadeEffect
import com.mc.gameengine.game.effects.ShakeEffect
import com.mc.gameengine.game.mask.CollisionExplosion
import com.mc.gameengine.game.mask.CollisionMeteor
import com.mc.gameengine.game.sprite.SpriteExplosion
import com.mc.gameengine.game.sprite.SpriteMeteor

class Meteor(
    initialHorizontalPosition: Float,
    private val controller: GameController
) : FallObject(
    initialHorizontalPosition,
    120f
) {

    private var sprite: Sprite = SpriteMeteor()

    override val initialVerticalPosition: Float = -32f

    private var collider = CollisionMeteor(this)

    private val explosionArea = CollisionExplosion(this)

    private val fadeAnimation = FadeEffect()

    private val shakeAnimation = ShakeEffect()

    private val warningTimer = WarningTimer(PowerItem.Warning in controller.availablePowers)

    private var isExploding = false

    private var isBack = false

    private var isDestroying = false

    override fun onEnterScene() {
        super.onEnterScene()
        computePivot()
        sprite.update { current }
        collider.update { it.copy(pivot = Pivot.Center) }
        explosionArea.update { it.copy(pivot = Pivot.Center) }
        addCollider(collider)
        if (warningTimer.isEnable) audioPlayer.playSound(MainAudios.SND_ALERT)
    }

    override fun fixedUpdate(dt: Float) {
        if (warningTimer.isEnable) {
            warningTimer.update(dt)
            return
        }
        computePosition(dt)
        collider.update { it.copy(position = current.position) }
        sprite.animate(dt)
        sprite.update { current }
        if (isDestroying) executeFade()
        if (isExploding) onExplosion(dt)
        if (PowerItem.SlowTime in controller.availablePowers) lowGravity()
        super.fixedUpdate(dt)
    }

    override fun Renderer.onRender(state: TransformState) {
        if (warningTimer.isEnable) drawSprite(
            spriteId = SpritesMain.SPR_WARNING,
            state = TransformState(
                position = current.position.copy(y = 40f),
                pivot = Pivot.Center
            ),
            color = Color.White.copy(alpha = warningTimer.timer % 1f)
        ) else sprite.draw(
            color = Color.White.copy(alpha = fadeAnimation.alpha),
            deep = if (isBack) -1 else 1
        )
    }

    override fun onGroundFell() {
        if (isDestroying) return
        audioPlayer.playSound(MainAudios.SND_EXPLOSION)
        updatePosition { it.copy(y = groundPosition) }
        explosionArea.update { it.copy(position = current.position) }
        addCollider(explosionArea)
        removeCollider(collider)
        stopFalling()
        sprite = SpriteExplosion()
        isExploding = true
        computePivot()
    }

    private fun computePivot() {
        val halfSize = sprite.size() / 2f
        updatePivot { Pivot.Custom(halfSize.x, y = halfSize.y * 1.5f) }
    }

    private fun executeFade() {
        fadeAnimation.animate()
        if (fadeAnimation.isGone) deleteInstance(this)
    }

    private fun onExplosion(dt: Float) {
        executeShake(dt) {
            controller.hate()
            isExploding = false
        }
        if (sprite.isLastFrame()) {
            removeCollider(explosionArea)
            sprite.stop()
            isBack = true
        }

    }

    private fun executeShake(dt: Float, onFinished: () -> Unit) {
        shakeAnimation.animate(dt)
        zoomCamera(shakeAnimation.zoom, viewportSize() / 2f)
        rotateCamera(shakeAnimation.angle, viewportSize() / 2f)
        if (shakeAnimation.isFinished) onFinished()
    }

    fun destroy() {
        removeCollider(collider)
        controller.incrementScore()
        isDestroying = true
    }
}

class WarningTimer(var isEnable: Boolean) {
    var timer = 0f

    fun update(dt: Float) {
        timer += dt
        if (timer >= 2) {
            isEnable = false
            timer = 0f
        }
    }
}