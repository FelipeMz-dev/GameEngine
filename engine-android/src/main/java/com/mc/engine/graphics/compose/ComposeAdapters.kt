package com.mc.engine.graphics.compose

import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.geometry.Size as ComposeSize
import androidx.compose.ui.geometry.Offset as ComposeOffset
import androidx.compose.ui.geometry.Rect as ComposeRect
import com.mc.engine.graphics.GpuColor
import com.mc.engine.graphics.GpuSize
import com.mc.engine.graphics.GpuOffset
import com.mc.engine.graphics.GpuRect
import com.mc.engine.render.ContentScaleMode
import com.mc.engine.render.FontSizeUnit
import com.mc.engine.render.RenderBlendMode
import com.mc.engine.render.RenderStyle
import com.mc.engine.render.TextOverflow
import com.mc.engine.render.TextRenderStyle

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

fun RenderStyle.toCompose(): DrawStyle = when (this) {
    RenderStyle.Fill -> Fill
    is RenderStyle.Stroke -> androidx.compose.ui.graphics.drawscope.Stroke(width)
}

fun RenderBlendMode.toCompose(): androidx.compose.ui.graphics.BlendMode = when (this) {
    RenderBlendMode.Modulate -> androidx.compose.ui.graphics.BlendMode.SrcAtop
    RenderBlendMode.Screen -> androidx.compose.ui.graphics.BlendMode.Screen
    RenderBlendMode.Multiply -> androidx.compose.ui.graphics.BlendMode.Multiply
    RenderBlendMode.Plus -> androidx.compose.ui.graphics.BlendMode.Plus
}

fun TextRenderStyle.toCompose(): TextStyle =
    TextStyle(
        color = color.toCompose(),
        fontSize = when (fontSizeUnit) {
            FontSizeUnit.Sp -> androidx.compose.ui.unit.TextUnit(fontSize, androidx.compose.ui.unit.TextUnitType.Sp)
            FontSizeUnit.Px -> androidx.compose.ui.unit.TextUnit(fontSize, androidx.compose.ui.unit.TextUnitType.Unspecified)
            FontSizeUnit.Em -> androidx.compose.ui.unit.TextUnit(fontSize, androidx.compose.ui.unit.TextUnitType.Em)
        }
    )

fun TextOverflow.toCompose(): androidx.compose.ui.text.style.TextOverflow = when (this) {
    TextOverflow.Clip -> androidx.compose.ui.text.style.TextOverflow.Clip
    TextOverflow.Ellipsis -> androidx.compose.ui.text.style.TextOverflow.Ellipsis
    TextOverflow.Visible -> androidx.compose.ui.text.style.TextOverflow.Visible
}

fun ContentScaleMode.toCompose(): ContentScale = when (this) {
    ContentScaleMode.Fit -> ContentScale.Fit
    ContentScaleMode.Crop -> ContentScale.Crop
    ContentScaleMode.FillBounds -> ContentScale.FillBounds
    ContentScaleMode.FillHeight -> ContentScale.FillHeight
    ContentScaleMode.FillWidth -> ContentScale.FillWidth
    ContentScaleMode.Inside -> ContentScale.Inside
    ContentScaleMode.None -> ContentScale.None
}