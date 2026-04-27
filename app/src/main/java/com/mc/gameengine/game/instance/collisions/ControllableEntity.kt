package com.mc.gameengine.game.instance.collisions

import androidx.compose.ui.input.key.Key
import com.mc.gameengine.engine.assets.Sprite
import com.mc.gameengine.engine.audio.AudioListener
import com.mc.gameengine.engine.collision.BoxCollider
import com.mc.gameengine.engine.collision.Collider
import com.mc.gameengine.engine.collision.CollisionEvent
import com.mc.gameengine.engine.collision.CollisionListener
import com.mc.gameengine.engine.collision.EllipseCollider
import com.mc.gameengine.engine.collision.MaskCollider
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.input.keyboard.KeyboardEvent
import com.mc.gameengine.engine.input.keyboard.KeyboardListener
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.minus
import com.mc.gameengine.engine.math.plus
import com.mc.gameengine.engine.math.resolve
import com.mc.gameengine.engine.math.times
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer
import com.mc.gameengine.game.assets.SpritesMain
import com.mc.gameengine.game.sprite.SpriteDinoWalk

class ControllableEntity : Instance(), CollisionListener, KeyboardListener, AudioListener {

    private val speed = 300f

    private val rotation = 200f

    private var collidingObject: Collider? = null

    private val sprite = Sprite(SpritesMain.meteor)

    private val collider = MaskCollider(this, sprite.spriteId)

    private lateinit var boxCollider: BoxCollider

    private var centerCollider = Vec2.Zero

    override fun onEnterScene() {
        val size = spriteSize(sprite.spriteId)
        boxCollider = BoxCollider(this, size.x, size.y)
        updatePosition { Vec2.from(350f) }
        updatePivot { Pivot.Center }
        addCollider(collider)
        //addCollider(boxCollider)
    }

    override fun fixedUpdate(dt: Float) {
        computePosition(dt)
        sprite.update { current }
        boxCollider.update { current }
        collider.update { current }
        collider.updateFrame(sprite.currentFrame)
        centerCollider = collider.getCenter()
        collidingObject = null
    }

    override fun Renderer.onRender(state: TransformState) {
        /*drawOval(
            position = centerCollider,
            size = Vec2.from(50f),
            pivot = Pivot.Center,
            color = when (collidingObject) {
                is EllipseCollider -> Color.Green
                is BoxCollider -> Color.Blue
                else -> Color.Red
            }
        )

        //sprite.draw()

        with(collider) {
            drawRect(
                position = Vec2(x = aabb.x, y = aabb.y),
                size = Vec2(x = aabb.width, y = aabb.height),
                color = Color.Red.copy(alpha = 0.3f),
            )

            drawBuffer(
                position = collider.state.position,
                buffer = collider.getBuffer() ?: IntArray(0),
                width = collider.size.x.toInt(),
                height = collider.size.y.toInt()
            )
        }*/

        with(collider.state) {
            drawText(
                "position: $position \n angle: $angle \n scale: $scale",
                position = Vec2.from(100f)
            )
        }
    }

    override fun onCollision(event: CollisionEvent) {
        (event.other.owner as Obstacle).collisionText = event.other.toString()
        collidingObject = event.other
    }

    override fun onKeyEvent(event: KeyboardEvent) {
        when (event) {
            is KeyboardEvent.KeyDown -> {
                when (event.key) {
                    Key.R -> audioPlayer.playSoundAt("explosion", Vec2.Zero)
                    Key.F -> audioPlayer.playSound("destruction")
                    Key.Three -> current = current.copy(flipX = !current.flipX)
                    Key.Four -> current = current.copy(flipY = !current.flipY)
                    Key.One -> sprite.nextFrame()
                    Key.Two -> sprite.previousFrame()
                }
            }

            is KeyboardEvent.KeyHeld -> {
                when (event.key) {
                    Key.Q -> updateScale { it - event.dt }
                    Key.E -> updateScale { it + event.dt }
                    Key.Z -> updateAngle { it + rotation * event.dt }
                    Key.X -> updateAngle { it - rotation * event.dt }
                    Key.DirectionLeft -> updatePosition { it.copy(x = it.x - speed * event.dt) }
                    Key.DirectionRight -> updatePosition { it.copy(x = it.x + speed * event.dt) }
                    Key.DirectionUp -> updatePosition { it.copy(y = it.y - speed * event.dt) }
                    Key.DirectionDown -> updatePosition { it.copy(y = it.y + speed * event.dt) }
                    Key.W -> updatePivot {
                        val offset = getPivotOffset(it)
                        Pivot.Custom(offset.x, offset.y - speed * event.dt)
                    }

                    Key.S -> updatePivot {
                        val offset = getPivotOffset(it)
                        Pivot.Custom(offset.x, offset.y + speed * event.dt)
                    }

                    Key.A -> updatePivot {
                        val offset = getPivotOffset(it)
                        Pivot.Custom(offset.x - speed * event.dt, offset.y)
                    }

                    Key.D -> updatePivot {
                        val offset = getPivotOffset(it)
                        Pivot.Custom(offset.x + speed * event.dt, offset.y)
                    }
                }
            }

            else -> Unit
        }
    }

    private fun getPivotOffset(pivot: Pivot): Vec2 {
        val spriteSize = spriteSize(sprite.spriteId) * sprite.state.scale
        return pivot.resolve(spriteSize)
    }

    override fun onRequireListenPosition(): Vec2 = centerCollider
}