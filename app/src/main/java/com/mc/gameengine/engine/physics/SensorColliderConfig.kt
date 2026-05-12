package com.mc.gameengine.engine.physics

import com.mc.gameengine.engine.collision.CollisionBodyType
import com.mc.gameengine.engine.collision.CollisionLayers
import com.mc.gameengine.engine.core.TransformState

/**
 * Lightweight trigger configuration backed by a dyn4j sensor fixture.
 *
 * Sensor colliders are intended for gameplay overlap notifications without the
 * extra SAT/mask checks performed by the legacy collision system.
 */
data class SensorColliderConfig(
    val shape: Shape,
    val state: TransformState = TransformState(),
    val type: CollisionBodyType = CollisionBodyType.Static,
    val layer: Int = CollisionLayers.Default,
    val mask: Int = CollisionLayers.All,
    val physicState: PhysicState = PhysicState(),
)
