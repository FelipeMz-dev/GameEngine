package com.mc.gameengine.game.instance.collisions

import com.mc.gameengine.engine.assets.Sprite
import com.mc.gameengine.engine.collision.Collider
import com.mc.gameengine.engine.collision.MaskCollider
import com.mc.gameengine.engine.collision.PolygonalCollider
import com.mc.gameengine.engine.compose.RenderDepth
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.input.mouse.MouseEvent
import com.mc.gameengine.engine.input.mouse.MouseListener
import com.mc.gameengine.engine.input.touch.TouchEvent
import com.mc.gameengine.engine.input.touch.TouchListener
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.div
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer
import com.mc.gameengine.game.assets.SpritesMain

class Obstacle : Instance(), MouseListener, TouchListener {

    var collisionText = String()

    val sprite = Sprite(SpritesMain.meteor)

    private val collider: Collider = MaskCollider(this, sprite.spriteId)
    private val collider2: Collider = PolygonalCollider(
        this,
        listOf(
            Vec2(10f, 0f),
            Vec2(20f, 10f),
            Vec2(15f, 20f),
            Vec2(5f, 20f),
            Vec2(0f, 10f)
        )
    )

    override fun onEnterScene() {
        collider.update { it.copy(pivot = Pivot.Center, position = viewportSize() / 2f, scale = Vec2.from(1f)) }
        collider2.update { it.copy(pivot = Pivot.Top, angle = 56f, scale = Vec2.from(2.5f), position = viewportSize() / 3f) }
        addCollider(collider)
        addCollider(collider2)
    }

    override fun fixedUpdate(dt: Float) {
        collisionText = "not collisioned"
    }

    override fun Renderer.onRender(state: TransformState) {
        drawText(
            position = Vec2(x = 100f, viewportSize().y - 100),
            text = collisionText,
            deep = RenderDepth.DEBUG
        )
    }

    override fun onMouseEvent(event: MouseEvent) {
        when(event) {
            is MouseEvent.MouseClickEvent -> {
                collisionText = "mouse click ${event.position} ${event.button}"
            }
            is MouseEvent.MouseMoveCursorEvent -> {
                collisionText = "mouse move ${event.position}"
            }
            is MouseEvent.MouseReleaseEvent -> {
                collisionText = "mouse release ${event.position} ${event.button}"
            }
            is MouseEvent.MouseScrollEvent -> {
                collisionText = "mouse scroll x: ${event.scrollX} y: ${event.scrollY}"
            }
            is MouseEvent.MouseDragEvent -> {
                collisionText = "mouse drag ${event.delta} ${event.button}"
            }
            else -> Unit
        }
    }

    override fun onTouchEvent(event: TouchEvent) {
        when(event) {
            is TouchEvent.TapEvent -> collider.update { it.copy(position = viewportSize() / 2f) }
            else -> Unit
        }
    }
}