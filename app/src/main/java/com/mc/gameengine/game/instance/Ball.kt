package com.mc.gameengine.game.instance

import androidx.compose.ui.graphics.Color
import com.mc.gameengine.app.GameController
import com.mc.gameengine.engine.collision.BoxCollider
import com.mc.gameengine.engine.collision.CollisionEvent
import com.mc.gameengine.engine.collision.CollisionListener
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.length
import com.mc.gameengine.engine.math.minus
import com.mc.gameengine.engine.math.normalized
import com.mc.gameengine.engine.math.times
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer
import com.mc.gameengine.game.assets.MainAudios
import com.mc.gameengine.game.assets.SpritesMain
import com.mc.gameengine.game.effects.BlurEffect
import com.mc.gameengine.game.effects.FadeEffect
import com.mc.gameengine.game.effects.MeteorDestruction
import kotlin.math.abs

class Ball(
    initialHorizontalPosition: Float,
    private val controller: GameController
) : FallObject(initialHorizontalPosition), CollisionListener {

    override val initialVerticalPosition: Float = -38f

    private var collider = BoxCollider(this, 38f, 38f)

    private val blurFX = BlurEffect(SpritesMain.SPR_BALL)

    private val fadeEffect = FadeEffect(0.015f)

    private val particlesMeteor = MeteorDestruction()

    private val bounceRestitution = 0.4f

    private var isDestroying = false

    override fun onEnterScene() {
        super.onEnterScene()
        updatePivot { Pivot.Center }
        addCollider(collider)
    }

    override fun fixedUpdate(dt: Float) {
        super.fixedUpdate(dt)
        blurFX.update(dt)
        collider.update { current }
        if (isDestroying) executeDestroy(dt)
        if (physics.velocity.y == 0f) {
            isDestroying = true
            removeCollider(collider)
        } else blurFX.emit(
            position = current.position,
            alpha = fadeEffect.alpha
        )
        if (PowerItem.SlowTime in controller.availablePowers) lowGravity()
    }

    override fun onGroundFell() {
        updatePosition { it.copy(y = groundPosition) }
        updateVelocity { currentVel ->
            if (abs(currentVel.y) < 50f) currentVel.copy(y = 0f)
            else {
                audioPlayer.playSound(MainAudios.SND_BALL)
                currentVel.copy(y = -currentVel.y * bounceRestitution)
            }
        }
    }

    override fun Renderer.onRender(state: TransformState) {
        val sprite = SpritesMain.run { if (controller.isMultiplier) SPR_MULTIPLIER else SPR_BALL }
        blurFX.draw(this)
        particlesMeteor.draw(this)
        drawSprite(
            spriteId = sprite,
            state = current,
            frame = 1,
            color = Color.White.copy(alpha = fadeEffect.alpha)
        )
    }

    private fun executeDestroy(dt: Float) {
        fadeEffect.animate()
        particlesMeteor.update(dt)
        if (fadeEffect.isGone) {
            removeCollider(collider)
            if (particlesMeteor.hasParticle) return
            deleteInstance(this)
        }
    }

    fun hit(from: Float, force: Float = 1.2f) {
        audioPlayer.playSound(MainAudios.SND_HIT_BAT)
        controller.collectBall()
        isDestroying = true
        updateVelocity {
            Vec2(
                (current.position.x - from) * 30,
                -(gravityValue * force)
            )
        }
    }

    private fun collideWithMeteor(meteor: Meteor) {
        isDestroying = true
        if (physics.velocity.length() > 60f) {
            particlesMeteor.emit(meteor.currentState().position, 100)
            audioPlayer.playSound(MainAudios.SND_ROCK_DESTROY)
            deleteInstance(meteor)
        }
        executeBounce(meteor.currentState().position)
    }

    override fun onCollision(event: CollisionEvent) {
        when (val other = event.other.owner) {
            is Meteor -> collideWithMeteor(other)
            is Ball -> {
                isDestroying = true
                executeBounce(other.current.position)
                other.hit(current.position.x, 0.6f)
            }
        }
    }

    fun executeBounce(from: Vec2) {
        updateVelocity { currentVel ->
            val collisionVector = current.position - from
            val normal = collisionVector.normalized()

            val dot = currentVel.x * normal.x + currentVel.y * normal.y
            if (dot < 0) {
                val reflection = currentVel - (normal * (2f * dot))
                reflection * bounceRestitution
            } else currentVel
        }
    }
}
