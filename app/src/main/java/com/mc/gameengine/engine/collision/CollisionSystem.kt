package com.mc.gameengine.engine.collision

import com.mc.gameengine.engine.core.Instance

internal class CollisionSystem {

    val colliders = mutableListOf<Collider>()

    fun addCollider(collider: Collider) {
        colliders += collider
    }

    fun removeCollider(collider: Collider) {
        colliders -= collider
    }

    fun clearInstanceColliders(instance: Instance) {
        colliders.removeIf { it.owner == instance }
    }

    fun check() {
        val collidersEnabled = colliders.filter { it.isEnabled }

        for (i in collidersEnabled.indices) {
            for (j in i + 1 until collidersEnabled.size) {
                val a = collidersEnabled[i]
                val b = collidersEnabled[j]
                if (a.owner !is CollisionListener && b.owner !is CollisionListener) continue
                if (!a.aabb.overlaps(b.aabb)) continue
                if (a.intersects(b)) dispatch(a, b)
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