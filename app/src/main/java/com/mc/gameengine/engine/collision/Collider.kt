package com.mc.gameengine.engine.collision

import com.mc.gameengine.core.math.Vec2
import com.mc.gameengine.engine.core.ColliderId
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.math.AABB

sealed class Collider(
    val id: ColliderId,
    val owner: Instance
) {
    abstract fun bounds(position: Vec2): AABB
}