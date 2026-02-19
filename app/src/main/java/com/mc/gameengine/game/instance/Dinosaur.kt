package com.mc.gameengine.game.instance

import androidx.compose.ui.input.key.Key
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.div
import com.mc.gameengine.engine.assets.Sprite
import com.mc.gameengine.engine.collision.CollisionEvent
import com.mc.gameengine.engine.collision.CollisionListener
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.input.AccelerometerEvent
import com.mc.gameengine.engine.input.KeyUp
import com.mc.gameengine.engine.input.KeyboardEvent
import com.mc.gameengine.engine.input.KeyboardListener
import com.mc.gameengine.engine.input.PressEvent
import com.mc.gameengine.engine.input.SensorEvent
import com.mc.gameengine.engine.input.SensorListener
import com.mc.gameengine.engine.input.TouchEvent
import com.mc.gameengine.engine.input.TouchListener
import com.mc.gameengine.engine.render.Renderer
import com.mc.gameengine.game.assets.SpritesDinoPlayer
import com.mc.gameengine.game.effects.BlurFX
import com.mc.gameengine.game.effects.MeteorDestruction
import com.mc.gameengine.game.mask.CollisionBatMeteor
import com.mc.gameengine.game.mask.CollisionMeteor
import com.mc.gameengine.game.sprite.SpriteDinoBat
import com.mc.gameengine.game.sprite.SpriteDinoWalk
import kotlin.math.absoluteValue

class Dinosaur : Instance(), TouchListener, SensorListener, KeyboardListener, CollisionListener {

    private var collision = CollisionBatMeteor(this)

    private var currentSprite: Sprite = SpriteDinoWalk(Vec2.Zero)
    private val particlesMeteor = MeteorDestruction()

    private val blurFX = BlurFX()
    private val speed = 40f

    private var finalVelocity = Vec2.Zero

    private val boost = BoostManager(speed * 10, 20)

    override fun onEnterScene() {
        current = current.copy(
            position = Vec2(viewportSize().x / 2, 300f),
            scale = Vec2.from(1f),
        )
        addCollider(collision)
    }

    override fun onTouchEvent(event: TouchEvent) {
        when (event) {
            is PressEvent -> {
                val position = event.position / viewportScale()
                if (position.x < viewportSize().x / 2) boost.moveLeft()
                else boost.moveRight()
            }

            else -> Unit
        }
    }

    override fun onSensorEvent(event: SensorEvent) {
        when (event) {
            is AccelerometerEvent -> finalVelocity = finalVelocity.copy(x = event.value.y * speed)
            else -> Unit
        }
    }

    override fun onKeyEvent(event: KeyboardEvent) {
        when (event) {
            is KeyUp -> {
                when (event.key) {
                    Key.D -> particlesMeteor.emit(current.position, 80)
                    Key.DirectionLeft -> boost.moveLeft()
                    Key.DirectionRight -> boost.moveRight()
                }
            }

            else -> Unit
        }
    }

    override fun fixedUpdate(dt: Float) {
        collision.update { it.copy(position = current.position) }
        particlesMeteor.update(dt)
        blurFX.update(dt)
        val velocity = boost.compute(finalVelocity)
        if (velocity.x > 350f || velocity.x < -350f) blurFX.emit(
            position = current.position,
            flipX = velocity.x < 0f
        )
        physics = physics.copy(velocity = boost.compute(finalVelocity))
        computePosition(dt)
        stopInLimits()
    }

    override fun update(dt: Float) {
        currentSprite.animate(dt)
        when (currentSprite.spriteId) {
            SpritesDinoPlayer.BAT -> {
                if (currentSprite.isLastFrame()) currentSprite = SpriteDinoWalk(current.position)
            }

            SpritesDinoPlayer.WALK -> {
                currentSprite.frameDuration = speed / 20 / physics.velocity.x.absoluteValue
            }
        }
        currentSprite.update {
            it.copy(
                position = current.position,
                flipX = physics.velocity.x < 0f
            )
        }
    }

    override fun Renderer.onRender(state: TransformState) {
        blurFX.draw(this)
        drawSprite(
            sprite = currentSprite.spriteId,
            frame = currentSprite.currentFrame,
            position = currentSprite.metrics.position,
            flipX = currentSprite.metrics.flipX
        )
        particlesMeteor.draw(this)
    }

    override fun onCollision(event: CollisionEvent) {
        when (event.self) {
            is CollisionBatMeteor -> if (event.other is CollisionMeteor) {
                batMeteor(event.other.owner)
            }
        }
    }

    private fun stopInLimits() {
        val limitEnd = viewportSize().x - SpritesDinoPlayer.walk.spriteWidth
        when {
            current.position.x < 0f -> current = current.copy(
                position = current.position.copy(x = 0f)
            )

            current.position.x > limitEnd -> current = current.copy(
                position = current.position.copy(x = limitEnd)
            )
        }
    }

    private fun batMeteor(meteor: Instance) {
        particlesMeteor.emit(meteor.currentState().position, 80)
        currentSprite = SpriteDinoBat(current.position)
        deleteInstance(meteor)
    }
}