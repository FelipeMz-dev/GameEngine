package com.mc.engine.physics

import com.mc.engine.core.Instance

data class PhysicsContactTarget(
    val id: Int,
    val owner: Instance,
    val source: Any,
    val layer: Int,
    val mask: Int,
) {
    fun canNotify(other: PhysicsContactTarget): Boolean {
        return (mask and other.layer) != 0 && (other.mask and layer) != 0
    }
}