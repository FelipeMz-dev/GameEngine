package com.mc.gameengine.engine.collision

import kotlin.math.floor

class SpatialHashBroadPhase(
    private val cellSize: Float = 256f
) : BroadPhase {

    private val buckets = mutableMapOf<Long, MutableList<Collider>>()

    override fun rebuild(colliders: List<Collider>) {
        buckets.clear()

        colliders.forEach { collider ->
            val aabb = collider.aabb
            val minCellX = floor(aabb.x / cellSize).toInt()
            val maxCellX = floor((aabb.x + aabb.width) / cellSize).toInt()
            val minCellY = floor(aabb.y / cellSize).toInt()
            val maxCellY = floor((aabb.y + aabb.height) / cellSize).toInt()

            for (cellX in minCellX..maxCellX) {
                for (cellY in minCellY..maxCellY) {
                    val key = toKey(cellX, cellY)
                    val list = buckets.getOrPut(key) { mutableListOf() }
                    list += collider
                }
            }
        }
    }

    override fun computePairs(result: MutableList<Pair<Collider, Collider>>) {
        result.clear()

        val pairCache = mutableSetOf<Long>()

        buckets.values.forEach { bucket ->
            for (i in 0 until bucket.lastIndex) {
                val a = bucket[i]
                for (j in i + 1 until bucket.size) {
                    val b = bucket[j]
                    val key = pairKey(a, b)
                    if (pairCache.add(key)) {
                        result += a to b
                    }
                }
            }
        }
    }

    private fun toKey(cellX: Int, cellY: Int): Long {
        val x = cellX.toLong() and 0xffffffffL
        val y = cellY.toLong() and 0xffffffffL
        return (x shl 32) or y
    }

    private fun pairKey(a: Collider, b: Collider): Long {
        val min = minOf(a.id, b.id)
        val max = maxOf(a.id, b.id)
        return (min.toLong() shl 32) or (max.toLong() and 0xffffffffL)
    }
}
