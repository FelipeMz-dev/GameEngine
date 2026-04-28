package com.mc.gameengine.game.instance.collisions

import com.mc.gameengine.engine.collision.BoxCollider
import com.mc.gameengine.engine.collision.Collider
import com.mc.gameengine.engine.collision.CollisionBodyType
import com.mc.gameengine.engine.collision.CollisionLayers
import com.mc.gameengine.engine.collision.EllipseCollider
import com.mc.gameengine.engine.compose.RenderDepth
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.div
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer

class Obstacle : Instance() {

    var collisionText = String()

    private val collider: Collider = BoxCollider(this, 100f, 100f)
    private val collider2: Collider = EllipseCollider(this, 100f, 200f)
    /*PolygonalCollider(
        this,
        listOf(
            Vec2(10f, 0f),
            Vec2(20f, 10f),
            Vec2(15f, 20f),
            Vec2(5f, 20f),
            Vec2(0f, 10f)
        )
    )*/

    override fun onEnterScene() {
        collider.update { it.copy(pivot = Pivot.Center) }
        collider2.update { it.copy(pivot = Pivot.Top) }
        collider.setBodyType(CollisionBodyType.Static)
        collider2.setBodyType(CollisionBodyType.Static)
        collider.setCollisionFilter(layer = CollisionLayers.World, mask = CollisionLayers.Player)
        collider2.setCollisionFilter(layer = CollisionLayers.World, mask = CollisionLayers.Player)
        addCollider(collider)
        addCollider(collider2)
    }

    override fun fixedUpdate(dt: Float) {
        collider.update { it.copy(position = viewportSize() / 1.5f) }
        collider2.update { it.copy(position = viewportSize() / 3f) }
        collisionText = "not collisioned"
    }

    override fun Renderer.onRender(state: TransformState) {
        drawText(
            position = Vec2(x = 100f, viewportSize().y - 100),
            text = collisionText,
            deep = RenderDepth.DEBUG
        )
    }
}