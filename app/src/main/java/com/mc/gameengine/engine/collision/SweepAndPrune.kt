package com.mc.gameengine.engine.collision

class SweepAndPrune {

    private val entries = mutableListOf<SapEntry>()
    private val lookup = mutableMapOf<Collider, SapEntry>()

    fun add(collider: Collider) {
        val aabb = collider.aabb

        val entry = SapEntry(
            collider,
            aabb.x,
            aabb.x + aabb.width
        )

        entries += entry
        lookup[collider] = entry
    }

    fun update(collider: Collider) {

        val entry = lookup[collider] ?: return
        val aabb = collider.aabb

        entry.minX = aabb.x
        entry.maxX = aabb.x + aabb.width

        resort(entry)
    }

    private fun resort(entry: SapEntry) {

        var i = entries.indexOf(entry)

        // mover izquierda
        while (i > 0 &&
            entries[i - 1].minX > entry.minX
        ) {
            swap(i, i - 1)
            i--
        }

        // mover derecha
        while (i < entries.lastIndex &&
            entries[i + 1].minX < entry.minX
        ) {
            swap(i, i + 1)
            i++
        }
    }

    private fun swap(a: Int, b: Int) {
        val tmp = entries[a]
        entries[a] = entries[b]
        entries[b] = tmp
    }

    fun rebuild(colliders: List<Collider>) {
        entries.clear()

        for (c in colliders) {
            val aabb = c.aabb
            entries += SapEntry(
                collider = c,
                minX = aabb.x,
                maxX = aabb.x + aabb.width
            )
        }
    }

    fun sort() {
        for (i in 1 until entries.size) {
            val key = entries[i]
            var j = i - 1

            while (j >= 0 && entries[j].minX > key.minX) {
                entries[j + 1] = entries[j]
                j--
            }

            entries[j + 1] = key
        }
        entries.sortBy { it.minX }
    }

    fun computePairs(result: MutableList<Pair<Collider, Collider>>) {

        result.clear()

        for (i in entries.indices) {

            val a = entries[i]

            var j = i + 1

            while (j < entries.size) {

                val b = entries[j]

                if (b.minX > a.maxX)
                    break

                result += a.collider to b.collider
                j++
            }
        }
    }
}

data class SapEntry(
    val collider: Collider,
    var minX: Float = 0f,
    var maxX: Float = 0f,
    var index: Int = -1
)