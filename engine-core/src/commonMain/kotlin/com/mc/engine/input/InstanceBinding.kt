package com.mc.engine.input

import com.mc.engine.core.Instance

internal interface InstanceBinding {
    fun register(instance: Instance)
    fun unregister(instance: Instance)
}
