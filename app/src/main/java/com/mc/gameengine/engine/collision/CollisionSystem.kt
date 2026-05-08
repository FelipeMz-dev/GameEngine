package com.mc.gameengine.engine.collision

import com.mc.gameengine.engine.core.GameObject

internal class CollisionSystem(
    private val broadPhase: BroadPhase = SpatialHashBroadPhase()
) {

    val colliders = mutableListOf<Collider>()

    private val candidatePairs = mutableListOf<Pair<Collider, Collider>>()
    private val activePairs = mutableSetOf<PairKey>()

    fun addCollider(collider: Collider) {
        colliders += collider
    }

    fun removeCollider(collider: Collider) {
        colliders -= collider
        removeActivePairsFor(collider)
    }

    fun clearInstanceColliders(instance: GameObject) {
        val toRemove = colliders.filter { it.owner == instance }
        colliders.removeAll(toRemove)
        toRemove.forEach { removeActivePairsFor(it) }
    }

    fun check() {
        val collidersEnabled = colliders.filter { it.isEnabled }
        if (collidersEnabled.isEmpty()) {
            activePairs.clear()
            return
        }

        broadPhase.rebuild(collidersEnabled)
        broadPhase.computePairs(candidatePairs)

        val currentPairs = mutableSetOf<PairKey>()

        candidatePairs.forEach { (a, b) ->
            if (!canCollide(a, b)) return@forEach
            if (!a.aabb.overlaps(b.aabb)) return@forEach
            if (!a.intersects(b)) return@forEach

            val key = PairKey.from(a, b)
            currentPairs += key

            val phase = if (key in activePairs) CollisionPhase.Stay else CollisionPhase.Enter
            dispatch(a, b, phase)
        }

        activePairs
            .asSequence()
            .filter { it !in currentPairs }
            .mapNotNull { key -> key.resolve(colliders) }
            .forEach { (a, b) ->
                dispatch(a, b, CollisionPhase.Exit)
            }

        activePairs.clear()
        activePairs += currentPairs
    }

    private fun canCollide(a: Collider, b: Collider): Boolean {
        if (!wantsNotifications(a, b)) return false
        return (a.mask and b.layer) != 0 && (b.mask and a.layer) != 0
    }

    private fun wantsNotifications(a: Collider, b: Collider): Boolean {
        if (a.owner is CollisionListener || b.owner is CollisionListener) return true
        if (a.bodyType != CollisionBodyType.Static || b.bodyType != CollisionBodyType.Static) return true
        return false
    }

    private fun dispatch(a: Collider, b: Collider, phase: CollisionPhase) {
        when (val owner = a.owner) {
            is CollisionListener -> owner.onCollision(CollisionEvent(a, b, phase))
        }
        when (val owner = b.owner) {
            is CollisionListener -> owner.onCollision(CollisionEvent(b, a, phase))
        }
    }

    private fun removeActivePairsFor(collider: Collider) {
        activePairs.removeIf { it.includes(collider.id) }
    }
}

private data class PairKey(
    val minId: Int,
    val maxId: Int
) {
    fun includes(id: Int): Boolean = minId == id || maxId == id

    fun resolve(colliders: List<Collider>): Pair<Collider, Collider>? {
        val minCollider = colliders.firstOrNull { it.id == minId } ?: return null
        val maxCollider = colliders.firstOrNull { it.id == maxId } ?: return null
        return minCollider to maxCollider
    }

    companion object {
        fun from(a: Collider, b: Collider): PairKey {
            val min = minOf(a.id, b.id)
            val max = maxOf(a.id, b.id)
            return PairKey(min, max)
        }
    }
}
