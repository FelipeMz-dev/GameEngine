package com.mc.gameengine.game.instance

import androidx.compose.ui.input.key.Key
import com.mc.gameengine.app.GameController
import com.mc.gameengine.engine.assets.Sprite
import com.mc.gameengine.engine.collision.CollisionEvent
import com.mc.gameengine.engine.collision.CollisionListener
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.input.keyboard.KeyboardEvent
import com.mc.gameengine.engine.input.keyboard.KeyboardListener
import com.mc.gameengine.engine.input.sensor.SensorEvent
import com.mc.gameengine.engine.input.sensor.SensorListener
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.plus
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer
import com.mc.gameengine.game.assets.MainAudios
import com.mc.gameengine.game.assets.MainAudios.SND_TELEPORT
import com.mc.gameengine.game.effects.BlurEffect
import com.mc.gameengine.game.effects.BoostEffect
import com.mc.gameengine.game.effects.MagneticEffect
import com.mc.gameengine.game.effects.MeteorDestruction
import com.mc.gameengine.game.mask.CollisionBatMeteor
import com.mc.gameengine.game.mask.CollisionExplosion
import com.mc.gameengine.game.mask.MagnetCollider
import com.mc.gameengine.game.sprite.SpriteDinoBat
import com.mc.gameengine.game.sprite.SpriteDinoHit
import com.mc.gameengine.game.sprite.SpriteDinoWalk
import com.mc.gameengine.game.sprite.SpriteHelmet
import kotlin.math.absoluteValue

class Dinosaur(
    private val controller: GameController
) : Instance(), SensorListener, KeyboardListener, CollisionListener {

    private var collision = CollisionBatMeteor(this)
    private var currentSprite: Sprite = SpriteDinoWalk()
    private val helmet: Sprite = SpriteHelmet()
    private val particlesMeteor = MeteorDestruction()
    private val blurFX = BlurEffect(currentSprite.spriteId)

    private val speed = 80f

    private val boost = BoostEffect(650f, 40)

    private var finalVelocity = Vec2.Zero

    private val magnetCollider = MagnetCollider(this)

    private val magneticEffect = MagneticEffect()

    private val hitAnimation = HitAnimation(2.5f)

    private var hasHelmet = false

    override fun onEnterScene() {
        current = current.copy(
            position = Vec2(viewportSize().x / 2, 350f),
            pivot = Pivot.Center,
        )
        currentSprite.update { current }
        collision.update { current.copy(pivot = Pivot.Top) }
        magnetCollider.disable()
        addCollider(collision)
        addCollider(magnetCollider)
    }

    override fun onSensorEvent(event: SensorEvent) {
        when (event) {
            is SensorEvent.AccelerometerEvent -> {
                if (hitAnimation.isAnimating) return
                val speed = (event.value.y * speed).coerceAtMost(500f)
                finalVelocity = finalVelocity.copy(x = speed)
            }

            else -> Unit
        }
    }

    override fun onKeyEvent(event: KeyboardEvent) {
        when (event) {
            is KeyboardEvent.KeyUp -> {
                when (event.key) {
                    //Key.D -> particlesMeteor.emit(Vec2.from(200f), 80)
                }
            }

            is KeyboardEvent.KeyDown -> {
                when (event.key) {
                    Key.A -> {
                        currentSprite = SpriteDinoHit()
                        hitAnimation.animate()
                    }
                }
            }

            is KeyboardEvent.KeyHeld -> {
                when (event.key) {
                    Key.DirectionLeft -> updateVelocity { it.copy(x = -300f) }
                    Key.DirectionRight -> updateVelocity { it.copy(x = 300f) }
                }
            }

            else -> Unit
        }
    }

    override fun fixedUpdate(dt: Float) {
        computePosition(dt)
        computePowers(controller.availablePowers)
        updateVelocity { boost.compute(finalVelocity) }
        collision.update { it.copy(position = current.position) }
        magnetCollider.update { current }
        currentSprite.update { current }
        particlesMeteor.update(dt)
        currentSprite.animate(dt)
        hitAnimation.update(dt)
        handleSpriteAnimation()
        computeMagnetFx(dt)
        computeBlurFx(dt)
        stopInLimits()
        applyFlip()
    }

    override fun Renderer.onRender(state: TransformState) {
        if (magnetCollider.isEnabled) magneticEffect.draw(this)
        if (!hitAnimation.isAnimating) blurFX.draw(this)
        currentSprite.draw()
        if (hasHelmet) helmet.draw()
        particlesMeteor.draw(this)
    }

    override fun onCollision(event: CollisionEvent) {
        when (event.self) {
            is CollisionBatMeteor -> when (event.other) {
                is CollisionExplosion -> animateHit()
                else -> when (val other = event.other.owner) {
                    is Meteor -> batMeteor(other)
                    is Ball -> batBall(other)
                    is Egg -> brokeEgg(other)
                    is PowerItemInstance -> other.onTake()
                }
            }

            is MagnetCollider -> {
                (event.other.owner as? FallObject)?.apply {
                    if (event.other !is CollisionExplosion) {
                        magneticAttraction(current.position)
                    }
                }
            }
        }
    }

    private fun stopInLimits() {
        if (hitAnimation.isAnimating) updateVelocity { Vec2.Zero }
        val limitStart = currentSprite.size().x / 2
        val limitEnd = viewportSize().x - limitStart
        when {
            current.position.x < limitStart -> {
                if (PowerItem.Portal in controller.availablePowers) {
                    updatePosition { it.copy(x = limitEnd - 10) }
                    audioPlayer.playSound(SND_TELEPORT)
                } else updatePosition { it.copy(x = limitStart) }
            }

            current.position.x > limitEnd -> {
                if (PowerItem.Portal in controller.availablePowers) {
                    updatePosition { it.copy(x = limitStart + 10) }
                    audioPlayer.playSound(SND_TELEPORT)
                } else updatePosition { it.copy(x = limitEnd) }
            }
        }
    }

    private fun batMeteor(meteor: Meteor) {
        audioPlayer.playSound(MainAudios.SND_ROCK_DESTROY)
        particlesMeteor.emit(meteor.currentState().position, 120)
        currentSprite = SpriteDinoBat()
        currentSprite.update { current }
        meteor.destroy()
    }

    private fun batBall(ball: Ball) {
        ball.hit(current.position.x)
        currentSprite = SpriteDinoBat()
        currentSprite.update { current }
    }

    private fun brokeEgg(egg: Egg) {
        //particlesMeteor.emit(egg.currentState().position, 120)
        egg.broke()
        animateHit()
    }

    private fun animateHit() {
        if (hasHelmet) return
        if (hitAnimation.isAnimating) return
        currentSprite = SpriteDinoHit()
        hitAnimation.animate()
        audioPlayer.playSound(MainAudios.SND_COMPUTER_BEEP)
    }

    fun computeBlurFx(dt: Float) {
        blurFX.update(dt)
        val velocity = boost.compute(finalVelocity)
        if (velocity.x > 350f || velocity.x < -350f) blurFX.emit(
            position = current.position,
            flipX = velocity.x < 0f
        )
    }

    private fun computeMagnetFx(dt: Float) {
        if (magnetCollider.isEnabled) {
            magneticEffect.update(dt)
            magneticEffect.updatePosition(current.position)
            magneticEffect.emit(current.position, audioPlayer)
        }
    }

    private fun applyFlip() {
        val isFlipped = physics.velocity.x < 0f
        currentSprite.update {
            it.copy(
                position = current.position,
                flipX = isFlipped
            )
        }
        helmet.update {
            it.copy(
                position = current.position + Vec2(0f, -25),
                angle = 12f * if (isFlipped) 1f else -1f,
                flipX = isFlipped
            )
        }
    }

    private fun handleSpriteAnimation() {
        when (currentSprite) {
            is SpriteDinoBat -> {
                if (currentSprite.isLastFrame()) currentSprite = SpriteDinoWalk()
            }

            is SpriteDinoWalk -> {
                currentSprite.frameDuration = speed / 20 / physics.velocity.x.absoluteValue
            }

            is SpriteDinoHit -> {
                if (!hitAnimation.isAnimating) currentSprite = SpriteDinoWalk()
            }
        }
    }

    private fun computePowers(powers: List<PowerItem>) {
        if (PowerItem.Magnet in powers) magnetCollider.enable()
        else magnetCollider.disable()
        hasHelmet = PowerItem.Helmet in powers
    }

    fun onBoostLeft() {
        if (hitAnimation.isAnimating) return
        audioPlayer.playSound(MainAudios.SND_BOOST)
        boost.moveLeft()
    }

    fun onBoostRight() {
        if (hitAnimation.isAnimating) return
        audioPlayer.playSound(MainAudios.SND_BOOST)
        boost.moveRight()
    }
}