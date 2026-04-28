package com.mc.gameengine.engine.collision

interface BroadPhase {
    fun rebuild(colliders: List<Collider>)
    fun computePairs(result: MutableList<Pair<Collider, Collider>>)
}
