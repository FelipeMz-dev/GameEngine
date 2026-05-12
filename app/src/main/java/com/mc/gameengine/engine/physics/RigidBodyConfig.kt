package com.mc.gameengine.engine.physics

import com.mc.gameengine.engine.collision.CollisionBodyType
import com.mc.gameengine.engine.collision.CollisionLayers
import com.mc.gameengine.engine.collision.PhysicsMaterial
import com.mc.gameengine.engine.core.TransformState

/**
 * Declarative description used by the engine to create a [RigidBody].
 *
 * Keeping all creation parameters in one object makes the public API easier to
 * read in game code and gives PhysicsManager a single input to validate/build.
 */
data class RigidBodyConfig(
    val shape: Shape,
    val state: TransformState = TransformState(),
    val type: CollisionBodyType = CollisionBodyType.Dynamic,
    val material: PhysicsMaterial = PhysicsMaterial(),
    val physicState: PhysicState = PhysicState(),
    val isSensor: Boolean = false,
    val layer: Int = CollisionLayers.Default,
    val mask: Int = CollisionLayers.All,
)
