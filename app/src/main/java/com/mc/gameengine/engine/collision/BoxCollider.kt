package com.mc.gameengine.engine.collision

import com.mc.gameengine.core.math.Vec2
import com.mc.gameengine.core.math.minus
import com.mc.gameengine.core.math.plus
import com.mc.gameengine.core.math.toSize
import com.mc.gameengine.core.math.toVec2
import com.mc.gameengine.engine.core.ColliderId
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.math.AABB
import com.mc.gameengine.engine.math.resolve
import com.mc.gameengine.engine.render.Pivot

class BoxCollider(
    id: ColliderId,
    owner: Instance,
    private val offset: Vec2,
    private val size: Vec2,
    private val pivot: Pivot = Pivot.TopLeft
) : Collider(id, owner) {



    override fun bounds(position: Vec2): AABB {
        val pivotOffset = pivot.resolve(size.toSize()).toVec2()
        return AABB(
            position = position + offset - pivotOffset,
            size = size
        )
    }
}