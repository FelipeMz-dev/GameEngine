package com.mc.engine.input.touch

import com.mc.engine.input.TouchEvent

interface TouchListener {
    fun onTouchEvent(event: TouchEvent)
}