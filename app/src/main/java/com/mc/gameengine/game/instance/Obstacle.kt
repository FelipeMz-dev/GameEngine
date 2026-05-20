package com.mc.gameengine.game.instance

import com.mc.engine.compose.RenderDepth
import com.mc.engine.core.Instance
import com.mc.engine.core.TransformState
import com.mc.engine.math.Vec2
import com.mc.engine.math.div
import com.mc.engine.physics.CollisionLayers
import com.mc.engine.physics.SensorCollider
import com.mc.engine.physics.Shape
import com.mc.engine.render.Renderer

class Obstacle : Instance() {

    var collisionText = String()

    private lateinit var boxSensor: SensorCollider
    private lateinit var ovalSensor: SensorCollider

    override fun onEnterScene() {
        boxSensor = createSensorCollider(
            shape = Shape.BoxShape(Vec2(100f, 100f)),
            layer = CollisionLayers.World,
            mask = CollisionLayers.Player,
        )
        ovalSensor = createSensorCollider(
            shape = Shape.EllipseShape(Vec2(50f, 100f)),
            layer = CollisionLayers.World,
            mask = CollisionLayers.Player,
        )
    }

    override fun fixedUpdate(dt: Float) {
        boxSensor.updateTransform { it.copy(position = viewport().size / 1.5f) }
        ovalSensor.updateTransform { it.copy(position = viewport().size / 3f) }
        collisionText = "not collisioned"
    }

    override fun Renderer.onRender(state: TransformState) {
        drawText(
            position = Vec2(x = 100f, viewport().size.y - 100),
            text = collisionText,
            deep = RenderDepth.DEBUG
        )
    }
}
