package com.mc.gameengine.engine.audio

import com.mc.gameengine.engine.math.Vec2

interface AudioListener {
    fun onRequireListenPosition(): Vec2
}