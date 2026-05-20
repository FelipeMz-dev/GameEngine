package com.mc.engine.graphics.compose

import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.geometry.Size as ComposeSize
import androidx.compose.ui.geometry.Offset as ComposeOffset
import androidx.compose.ui.geometry.Rect as ComposeRect
import com.mc.engine.graphics.GpuColor
import com.mc.engine.graphics.GpuSize
import com.mc.engine.graphics.GpuOffset
import com.mc.engine.graphics.GpuRect

/**
 * Adaptadores para convertir entre tipos agnósticos y tipos de Compose.
 * Permite que el engine use tipos agnósticos mientras se comunica con Compose.
 */

fun GpuColor.toCompose(): ComposeColor =
    ComposeColor(red, green, blue, alpha)

fun ComposeColor.toGpu(): GpuColor =
    GpuColor(red, green, blue, alpha)

fun GpuSize.toCompose(): ComposeSize =
    ComposeSize(width, height)

fun ComposeSize.toGpu(): GpuSize =
    GpuSize(width, height)

fun GpuOffset.toCompose(): ComposeOffset =
    ComposeOffset(x, y)

fun ComposeOffset.toGpu(): GpuOffset =
    GpuOffset(x, y)

fun GpuRect.toCompose(): ComposeRect =
    ComposeRect(left, top, right, bottom)

fun ComposeRect.toGpu(): GpuRect =
    GpuRect(left, top, right, bottom)


