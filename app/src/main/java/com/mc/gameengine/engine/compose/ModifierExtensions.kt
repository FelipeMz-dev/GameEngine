package com.mc.gameengine.engine.compose

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.mc.gameengine.engine.math.toVec2
import com.mc.gameengine.engine.input.TouchProcessor

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