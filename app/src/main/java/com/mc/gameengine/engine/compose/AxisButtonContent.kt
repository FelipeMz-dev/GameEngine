package com.mc.gameengine.engine.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.mc.gameengine.core.math.Vec2
import com.mc.gameengine.core.math.clamp
import com.mc.gameengine.core.math.plus
import com.mc.gameengine.core.math.toOffset
import com.mc.gameengine.engine.input.AxisEvent
import com.mc.gameengine.engine.input.InputManager
import com.mc.gameengine.engine.input.VirtualAxis

@Composable
fun AxisButtonContent(
    modifier: Modifier,
    id: String,
    radius: Float,
    input: InputManager,
    color: Color = Color.Companion.Red
) {
    val density = LocalDensity.current
    val radiusPx = with(density) { radius.dp.toPx() }

    val virtualAxis = remember(radiusPx, id, input) {
        VirtualAxis(
            id = id,
            center = Vec2(radiusPx, radiusPx),
            radius = radiusPx,
            input = input
        )
    }

    var analogOffset by remember {
        mutableStateOf(Vec2(0f, 0f))
    }

    Box(
        modifier = modifier
            .size((radius * 2).dp)
            .border(1.dp, color, CircleShape)
            .background(color.copy(alpha = 0.3f), CircleShape)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        virtualAxis.onDrag(Vec2(offset.x, offset.y))
                    },
                    onDrag = { change, _ ->
                        analogOffset = Vec2(
                            change.position.x - radiusPx,
                            change.position.y - radiusPx
                        )
                        virtualAxis.onDrag(Vec2(change.position.x, change.position.y))
                    },
                    onDragEnd = {
                        input.dispatch(
                            AxisEvent(
                                id = id,
                                value = Vec2(0f, 0f)
                            )
                        )
                        analogOffset = Vec2(0f, 0f)
                    }
                )
            }
            .drawWithContent {
                val offset = analogOffset.clamp(radiusPx) + Vec2(
                    this.size.width / 2,
                    this.size.height / 2
                )
                drawCircle(
                    center = offset.toOffset(),
                    color = color,
                    radius = radiusPx / 2
                )
            }
    )
}