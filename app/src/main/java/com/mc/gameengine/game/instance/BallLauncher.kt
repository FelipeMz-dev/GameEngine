package com.mc.gameengine.game.instance

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.mc.gameengine.engine.collision.CollisionBodyType
import com.mc.gameengine.engine.compose.RenderDepth
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.input.touch.TouchEvent
import com.mc.gameengine.engine.input.touch.TouchListener
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.clamp
import com.mc.gameengine.engine.math.length
import com.mc.gameengine.engine.math.minus
import com.mc.gameengine.engine.physics.Shape
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer
import com.mc.gameengine.game.instance.spec.BallSpec

class BallLauncher(
    private val launchPoint: Vec2 = Vec2(180f, 620f),
    private val floorY: Float = 700f
) : Instance(), TouchListener {

    private var nextBall = 0
    private val ballSpecs = listOf(
        BallSpec(radius = 24f, density = 1.0f, color = Color(0xFFe53935)),
        BallSpec(radius = 30f, density = 1.4f, color = Color(0xFF1e88e5)),
        BallSpec(radius = 38f, density = 1.8f, color = Color(0xFF43a047)),
    )

    override fun onEnterScene() {
        current = current.copy(position = Vec2(viewportSize().x / 2f, floorY), pivot = Pivot.Center)
        val shape = Shape.BoxShape(Vec2(viewportSize().x, 10f))
        createRigidBody(
            shape = shape,
            state = current,
            type = CollisionBodyType.Static
        )
    }

    override fun onTouchEvent(event: TouchEvent) {
        if (event !is TouchEvent.TapEvent) return

        val worldTap = screenToWorld(event.position)
        val direction = (worldTap - launchPoint)
        if (direction.length() < 100f) return
        val velocity = direction.length() * 0.03f
        val spec = ballSpecs[nextBall]
        nextBall = (nextBall + 1) % ballSpecs.size

        addInstance(
            ProjectileBall(
                start = launchPoint,
                target = worldTap,
                velocity = velocity,
                spec = spec
            )
        )
    }

    override fun Renderer.onRender(state: TransformState) {

        drawRect(
            size = Vec2(100f, 10f),
            state = state.copy(position = Vec2(launchPoint.x + 20f, floorY - 80f)),
            color = Color.DarkGray,
            deep = RenderDepth.DEBUG
        )

        drawCircle(
            radius = 28f,
            state = TransformState(position = launchPoint, pivot = Pivot.Center),
            color = Color.Companion.DarkGray,
            deep = RenderDepth.DEBUG
        )

        drawText(
            text = "Tap para lanzar bolas de distinto peso/tamaño",
            position = Vec2(24f, 28f),
            style = TextStyle(color = Color.Companion.DarkGray),
            deep = RenderDepth.DEBUG
        )
    }
}