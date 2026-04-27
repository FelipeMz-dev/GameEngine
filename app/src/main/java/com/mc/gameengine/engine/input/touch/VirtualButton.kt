package com.mc.gameengine.engine.input.touch

data class VirtualButton(
    private val id: String,
    private val pressed: Boolean,
    private val input: TouchManager
) {
    fun onPress() {
        input.dispatch(
            TouchEvent.ButtonEvent(
                id = id,
                pressed = true
            )
        )
    }

    fun onTap() {
        input.dispatch(
            TouchEvent.ButtonEvent(
                id = id,
                pressed = false
            )
        )
    }
}