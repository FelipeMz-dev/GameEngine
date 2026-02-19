package com.mc.gameengine.engine.input

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type

class KeyboardProcessor(
    var keyboardManager: KeyboardManager
) {

    private val pressedKeys = mutableSetOf<Key>()

    fun onKeyEvent(event: KeyEvent): Boolean {
        val key = event.key

        when (event.type) {
            KeyEventType.KeyDown -> {
                if (pressedKeys.add(key)) {
                    keyboardManager.dispatchKeyEvent(KeyDown(key))
                }
            }
            KeyEventType.KeyUp -> {
                if (pressedKeys.remove(key)) {
                    keyboardManager.dispatchKeyEvent(KeyUp(key))
                }
            }
        }
        return true
    }

    fun update(dt: Float) {
        pressedKeys.forEach {
            keyboardManager.dispatchKeyEvent(KeyHeld(it, dt))
        }
    }
}