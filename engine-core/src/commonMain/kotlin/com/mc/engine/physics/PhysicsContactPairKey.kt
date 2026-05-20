package com.mc.engine.physics

import com.mc.engine.core.Instance

data class PhysicsContactPairKey(
    val minId: Int,
    val maxId: Int,
    val ownerA: Instance,
    val ownerB: Instance,
) {
    fun includes(owner: Instance): Boolean = ownerA == owner || ownerB == owner

    fun includes(target: PhysicsContactTarget): Boolean = minId == target.id || maxId == target.id

    companion object {
        fun from(a: PhysicsContactTarget, b: PhysicsContactTarget): PhysicsContactPairKey {
            return if (a.id <= b.id) {
                PhysicsContactPairKey(a.id, b.id, a.owner, b.owner)
            } else {
                PhysicsContactPairKey(b.id, a.id, b.owner, a.owner)
            }
        }
    }
}