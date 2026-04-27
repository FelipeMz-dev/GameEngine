package com.mc.gameengine.engine.input

import com.mc.gameengine.engine.core.Instance

internal interface InstanceBinding {
    fun register(instance: Instance)
    fun unregister(instance: Instance)
}
