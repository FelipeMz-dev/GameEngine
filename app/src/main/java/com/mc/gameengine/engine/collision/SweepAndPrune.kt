package com.mc.gameengine.engine.collision

class SweepAndPrune : BroadPhase {

    private val entries = mutableListOf<SapEntry>()

    override fun rebuild(colliders: List<Collider>) {
        entries.clear()

        colliders.forEach { c ->
            val aabb = c.aabb
            entries += SapEntry(
                collider = c,
                minX = aabb.x,
                maxX = aabb.x + aabb.width
            )
        }

        entries.sortBy { it.minX }
    }

    override fun computePairs(result: MutableList<Pair<Collider, Collider>>) {
        result.clear()

        for (i in entries.indices) {
            val a = entries[i]

            var j = i + 1
            while (j < entries.size) {
                val b = entries[j]
                if (b.minX > a.maxX) break

                result += a.collider to b.collider
                j++
            }
        }
    }
}

data class SapEntry(
    val collider: Collider,
    var minX: Float = 0f,
    var maxX: Float = 0f
)
