package com.mc.gameengine.engine.collision

import com.mc.gameengine.engine.core.Instance

class CollisionSystem {

    fun check(instances: List<Instance>) {
        val colliders = instances.flatMap { it.allColliders() }
        for (i in colliders.indices) {
            for (j in i + 1 until colliders.size) {

                val a = colliders[i]
                val b = colliders[j]

                if (a == b) continue

                if (a.intersects(b)) {
                    dispatch(a, b)
                }
            }
        }
    }

    private fun dispatch(a: Collider, b: Collider) {
        when (val owner = a.owner) {
            is CollisionListener -> owner.onCollision(CollisionEvent(a, b))
        }
        when (val owner = b.owner) {
            is CollisionListener -> owner.onCollision(CollisionEvent(b, a))
        }
    }
}