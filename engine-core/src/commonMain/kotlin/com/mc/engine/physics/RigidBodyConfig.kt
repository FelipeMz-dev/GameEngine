package com.mc.engine.physics

import com.mc.engine.core.TransformState

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
    val layer: Int = CollisionLayers.Default,
    val mask: Int = CollisionLayers.All,
)
