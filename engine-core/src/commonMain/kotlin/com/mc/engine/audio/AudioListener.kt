package com.mc.engine.audio

import com.mc.engine.math.Vec2

interface AudioListener {
    fun onRequireListenPosition(): Vec2
}