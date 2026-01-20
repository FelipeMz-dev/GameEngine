package com.mc.gameengine.engine.compose

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ScaleFactor
import com.mc.gameengine.core.math.Vec2
import com.mc.gameengine.engine.input.TouchProcessor

fun Modifier.gameTouchInput(processor: TouchProcessor, scale: ScaleFactor) = this
    .pointerInput(Unit, scale) {
        detectDragGestures(
            onDragStart = { offset ->
                processor.onDown(offset.screenToWorld(scale))
            },
            onDrag = { change, _ ->
                processor.onMove(change.position.screenToWorld(scale))
            },
            onDragEnd = {
                processor.onStop()
            }
        )
    }
    .pointerInput(Unit, scale) {
        detectTapGestures(
            onPress = { offset ->
                val position = offset.screenToWorld(scale)
                processor.onDown(position)
                tryAwaitRelease()
                processor.onUp(position)
            }
        )
    }

fun Offset.screenToWorld(scale: ScaleFactor): Vec2 {
    val scaledX = this.x / scale.scaleX
    val scaledY = this.y / scale.scaleY
    return Vec2(scaledX, scaledY)
}