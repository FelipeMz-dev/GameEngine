package com.mc.gameengine.engine.input

import com.mc.gameengine.engine.core.GameObject

internal interface InstanceBinding {
    fun register(instance: GameObject)
    fun unregister(instance: GameObject)
}
