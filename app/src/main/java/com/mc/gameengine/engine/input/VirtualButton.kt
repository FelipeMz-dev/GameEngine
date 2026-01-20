package com.mc.gameengine.engine.input

data class VirtualButton(
    private val id: String,
    private val pressed: Boolean,
    private val input: InputManager
) {
    fun onPress() {
        input.dispatch(
            ButtonEvent(
                id = id,
                pressed = true
            )
        )
    }

    fun onTap() {
        input.dispatch(
            ButtonEvent(
                id = id,
                pressed = false
            )
        )
    }
}