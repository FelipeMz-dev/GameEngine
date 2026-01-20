package com.mc.gameengine.engine.collision

import com.mc.gameengine.engine.core.Instance

class CollisionSystem {

    fun check(instances: List<Instance>) {
        val colliders = instances.flatMap { it.allColliders() }

        for (i in colliders.indices) {
            for (j in i + 1 until colliders.size) {

                val a = colliders[i]
                val b = colliders[j]

                if (a.owner == b.owner) continue

                val stateA = a.owner.currentState()
                val stateB = b.owner.currentState()

                if (a.bounds(stateA.position).intersects(b.bounds(stateB.position))) {
                    dispatch(a, b)
                }
            }
        }
    }

    private fun dispatch(a: Collider, b: Collider) {
        if (a.owner is CollisionListener) {
            a.owner.onCollision(CollisionEvent(a, b))
        }
        if (b.owner is CollisionListener) {
            b.owner.onCollision(CollisionEvent(b, a))
        }
    }
}