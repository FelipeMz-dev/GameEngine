package com.mc.gameengine.game.instance

import androidx.compose.ui.graphics.Color
import com.mc.gameengine.core.math.Vec2
import com.mc.gameengine.core.math.times
import com.mc.gameengine.engine.assets.Sprite
import com.mc.gameengine.engine.collision.BoxCollider
import com.mc.gameengine.engine.collision.CollisionEvent
import com.mc.gameengine.engine.collision.CollisionListener
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.input.AccelerometerEvent
import com.mc.gameengine.engine.input.InputEvent
import com.mc.gameengine.engine.input.InputListener
import com.mc.gameengine.engine.input.TapEvent
import com.mc.gameengine.engine.particles.ParticleConfig
import com.mc.gameengine.engine.particles.ParticleEmitter
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer
import com.mc.gameengine.game.assets.SpritesDinoPlayer
import com.mc.gameengine.game.assets.SpritesMain
import kotlin.math.absoluteValue

class Dinosaur : Instance(), InputListener, CollisionListener {

    companion object {
        const val COLLIDER_BAT_METEOR = "batMeteor"
    }

    private var currentSprite = Sprite(SpritesDinoPlayer.walk)

    private lateinit var collision: BoxCollider

    private val speed = 40f

    private val particles = ParticleEmitter(
        ParticleConfig(
            spawnRate = 8f,
            life = 1f..2f,
            speed = 60f..100f,
            angle = 0f..180f,
            startScale = 0.2f,
            endScale = 1.8f,
            startColor = Color.Yellow,
            endColor = Color.LightGray,
            spriteId = SpritesMain.METEOR_DESTRUCTION
        )
    )

    override fun onEnterScene() {
        current = current.copy(
            position = Vec2(viewportSize().x / 2, 300f),
            scale = Vec2(1f, 1f),
        )
        collision = BoxCollider(
            COLLIDER_BAT_METEOR,
            this,
            Vec2.Zero.copy(
                y = currentSprite.size().y / 2f,
                x = currentSprite.size().x / 5.5f
            ),
            currentSprite.size().let {
                it.copy(y = it.y / 3.5f, x = it.x / 1.5f)
            },
        )
        addCollider(collision)
    }

    override fun onInput(event: InputEvent) {
        when (event) {

            is TapEvent -> {
                particles.emit(current.position, 1)
            }

            is AccelerometerEvent -> {
                physics = physics.copy(Vec2(event.value.y, 0f) * speed)
                if (currentSprite.spriteIs(SpritesDinoPlayer.WALK)) {
                    currentSprite.frameDuration = 0.06f / event.value.y.absoluteValue
                    if (event.value.y == 0f) currentSprite.stop() else currentSprite.start()
                }
            }
            else -> Unit
        }
    }

    override fun fixedUpdate(dt: Float) {
        current = current.copy(
            position = Vec2(
                x = current.position.x + physics.velocity.x * dt,
                y = current.position.y + physics.velocity.y * dt
            )
        )
        if (current.position.x < 0f) current = current.copy(
            current.position.copy(x = viewportSize().x - currentSprite.size().x)
        )
        if (current.position.x > viewportSize().x - currentSprite.size().x){
            current = current.copy(current.position.copy(x = 0f))
        }
    }

    override fun update(dt: Float) {
        with(currentSprite) {
            update(dt)
            if (spriteId == SpritesDinoPlayer.BAT && isLastFrame()) {
                currentSprite = Sprite(SpritesDinoPlayer.walk)
            }
        }
    }

    override fun Renderer.onRender(state: TransformState) {
        /*drawAABB(
            aabb = collision.bounds(current.position),
            color = Color.LightGray
        )*/

        drawSprite(
            sprite = currentSprite.spriteId,
            frame = currentSprite.currentFrame,
            position = state.position,
            scale = state.scale,
            pivot = Pivot.TopLeft,
            color = state.color,
            flipX = physics.velocity.x < 0f
        )
    }

    override fun onCollision(event: CollisionEvent) {
        when(event.self.id) {
            COLLIDER_BAT_METEOR -> if (event.other.id == Meteor.COLLISION_METEOR) {
                batMeteor(event.other.owner)
            }
        }
    }

    private fun batMeteor(meteor: Instance) {
        deleteInstance(meteor)
        currentSprite = Sprite(SpritesDinoPlayer.bat, frameDuration = 0.04f)
    }
}