package com.mc.gameengine.engine.compose

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.isTertiaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import com.mc.gameengine.engine.input.keyboard.KeyboardProcessor
import com.mc.gameengine.engine.input.mouse.MouseButton
import com.mc.gameengine.engine.input.mouse.MouseProcessor
import com.mc.gameengine.engine.input.touch.TouchProcessor
import com.mc.gameengine.engine.math.toVec2

fun Modifier.gameTouchInput(processor: TouchProcessor) = this
    .pointerInput(Unit) {
        detectDragGestures(
            onDragStart = { offset ->
                processor.onDown(offset.toVec2())
            },
            onDrag = { change, _ ->
                processor.onMove(change.position.toVec2())
            },
            onDragEnd = {
                processor.onStop()
            }
        )
    }
    .pointerInput(Unit) {
        detectTapGestures(
            onPress = { offset ->
                val position = offset.toVec2()
                processor.onDown(position)
                tryAwaitRelease()
                processor.onUp(position)
            }
        )
    }

fun Modifier.gameMouseInput(processor: MouseProcessor) = this.pointerInput(Unit) {
    awaitPointerEventScope {

        while (true) {

            val event = awaitPointerEvent()
            val change = event.changes.first()

            if (change.type != PointerType.Mouse) continue

            val scroll = change.scrollDelta
            val pos = change.position

            val button = when {
                event.buttons.isPrimaryPressed -> MouseButton.Left
                event.buttons.isSecondaryPressed -> MouseButton.Right
                event.buttons.isTertiaryPressed -> MouseButton.Middle
                else -> MouseButton.Unknown
            }

            if (event.type == PointerEventType.Scroll) {
                processor.scroll(
                    position = pos.toVec2(),
                    scrollX = scroll.x,
                    scrollY = scroll.y
                )
            } else {
                processor.mouseMove(pos.toVec2())
            }

            if (change.pressed && !change.previousPressed) {
                processor.mouseDown(pos.toVec2(), button)
            }

            if (!change.pressed && change.previousPressed) {
                processor.mouseUp(pos.toVec2())
            }
        }
    }
}

fun Modifier.gameKeyboardInput(processor: KeyboardProcessor, focusRequester: FocusRequester) = this
    .focusRequester(focusRequester)
    .focusable()
    .onFocusChanged {
        if (!it.isFocused) focusRequester.requestFocus()
    }
    .onKeyEvent {
        processor.onKeyEvent(it)
    }