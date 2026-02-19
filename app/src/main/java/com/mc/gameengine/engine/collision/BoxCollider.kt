package com.mc.gameengine.engine.collision

import androidx.compose.ui.graphics.Color
import com.mc.gameengine.engine.compose.RenderDepth
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.render.Renderer

open class BoxCollider(
    override val owner: Instance,
    val width: Float,
    val height: Float,
) : Collider(owner),
    CollisionCenterDelegate by CollisionCenterDelegateImpl(),
    CollisionVerticesDelegate by CollisionVerticesDelegateImpl()
{

    override var state: ColliderState = ColliderState()

    override fun Renderer.debugDraw() {
        drawPolygon(
            position = Vec2.Zero,
            points = getVertices(),
            color = Color.Gray.copy(alpha = 0.5f),
            deep = RenderDepth.DEBUG
        )
    }

    override fun onUpdate() {
        clearVertices()
        clearCenter()
    }

    fun getVertices(): List<Vec2> = computeVertices()

    fun getAxes(): List<Vec2> = computeAxes()

    fun getCenter() = computeCenter()

}