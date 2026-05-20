package com.mc.engine.input.touch

data class VirtualButton(
    private val id: String,
    private val pressed: Boolean,
    private val input: TouchManager
) {
    fun onPress() {
        input.dispatchLocal(
            TouchEvent.ButtonEvent(
                id = id,
                pressed = true
            )
        )
    }

    fun onTap() {
        input.dispatchLocal(
            TouchEvent.ButtonEvent(
                id = id,
                pressed = false
            )
        )
    }
}