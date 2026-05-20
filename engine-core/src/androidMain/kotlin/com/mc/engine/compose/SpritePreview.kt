package com.mc.engine.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.unit.dp
import com.mc.engine.assets.AtlasSpriteDef
import com.mc.engine.assets.FrameListSpriteDef
import com.mc.engine.assets.ImageLoaderImpl
import com.mc.engine.assets.SpriteDefinition
import com.mc.engine.assets.SpriteManager
import com.mc.engine.assets.SpriteSource
import com.mc.engine.core.TransformState
import com.mc.engine.render.ImageLoader
import com.mc.engine.time.GameTime
import com.mc.engine.time.TimeConfig

@Composable
fun SpritePreview(def: SpriteDefinition) {
    val frame = remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier.Companion
            .padding(16.dp)
            .background(Color.Companion.White),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        when (def) {
            is AtlasSpriteDef -> ContentAnimationAtlas(
                def = def,
                frame = frame.intValue
            )

            is FrameListSpriteDef -> ContentAnimationFrameList(
                def = def,
                frame = frame.intValue
            )

            else -> Unit
        }

        ContentPreviewAnimation(
            def = def,
            frame = frame
        )
    }
}

@Composable
private fun ContentPreviewAnimation(
    def: SpriteDefinition,
    frame: MutableIntState
) {
    val resources = LocalResources.current
    val density = LocalDensity.current
    var showAnimation by remember { mutableStateOf(false) }
    val manager = remember(resources) {
        val imageLoader: ImageLoader = ImageLoaderImpl(resources)
        SpriteManager(imageLoader).apply { load(def) }
    }
    val sprite: SpriteSource? = remember(manager, def.spriteId) {
        runCatching {
            manager.get(def.spriteId)
        }.getOrNull()
    }

    if (sprite == null) {
        Text("Sprite not found: ${def.spriteId}")
        return
    }

    val frameWidth = def.srcSize?.x ?: sprite.frameWidth.toFloat()
    val frameHeight = def.srcSize?.y ?: sprite.frameHeight.toFloat()
    val frameWidthDp = density.run { (frameWidth * 2).toDp() }
    val frameHeightDp = density.run { (frameHeight * 2).toDp() }

    if (showAnimation) FrameIncrementer(
        frame = frame,
        speed = 0.05f,
        totalFrames = def.totalFrames
    )

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        CheckboxText(
            text = "Show Animation",
            checked = showAnimation,
        ) { showAnimation = it }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalButton(
                onClick = { frame.intValue = (frame.intValue - 1).coerceAtLeast(0) }
            ) { Text("<") }

            Text("Frame: ${frame.intValue + 1}")

            FilledTonalButton(
                onClick = { frame.intValue = (frame.intValue + 1) % def.totalFrames }
            ) { Text(">") }
        }

        Canvas(
            modifier = Modifier
                .width(frameWidthDp)
                .height(frameHeightDp)
                .drawBackgroundSquares()
        ) {
            withTransform({ scale(2f, 2f, Offset.Zero) }) {
                drawSpriteInternal(
                    image = sprite.frameAt(frame.intValue) ?: return@withTransform,
                    state = TransformState(),
                    colorFilter = ColorFilter.tint(
                        color = Color.White,
                        blendMode = BlendMode.Modulate
                    )
                )
            }
        }
    }
}

@Composable
private fun ContentAnimationFrameList(
    def: FrameListSpriteDef,
    frame: Int
) {
    val resources = LocalResources.current
    val density = LocalDensity.current
    var showAnimation by remember { mutableStateOf(true) }

    val images = remember(def.resIds) {
        val imageLoader = ImageLoaderImpl(resources)
        def.resIds.map {
            imageLoader.loadRes(
                resId = it,
                hasAlpha = def.hasAlpha,
            )
        }
    }

    if (images.isEmpty()) {
        Text("No images in FrameListSprite")
        return
    }

    val maxWidth = images.first().width
    val maxHeight = images.first().height

    FlowColumn(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        images.forEach {
            Canvas(
                modifier = Modifier
                    .width(density.run { it.width.toDp() })
                    .height(density.run { it.height.toDp() })
                    .drawBackgroundSquares()
            ) {
                withTransform({ scale(2f, 2f, Offset.Zero) }) {
                    drawImage(image = it)
                }
            }
        }
    }
}

@Composable
private fun ContentAnimationAtlas(
    def: AtlasSpriteDef,
    frame: Int
) {
    val resources = LocalResources.current
    val density = LocalDensity.current
    var showGrid by remember { mutableStateOf(true) }
    val spacingX = def.srcSpacing?.x ?: 0f
    val spacingY = def.srcSpacing?.y ?: 0f

    val image = remember(def.resId, def.hasAlpha) {
        ImageLoaderImpl(resources).loadRes(
            resId = def.resId,
            hasAlpha = def.hasAlpha,
        )
    }

    val frameWidth = def.srcSize?.x ?: 0f
    val frameHeight = def.srcSize?.y ?: 0f

    val size = Size(
        width = frameWidth,
        height = frameHeight
    )
    val width = image.width * 2
    val height = image.height * 2

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        CheckboxText(
            text = "Gird Frames",
            checked = showGrid,
        ) { showGrid = it }

        Canvas(
            modifier = Modifier
                .width(density.run { width.toDp() })
                .height(density.run { height.toDp() })
                .drawBackgroundSquares()
        ) {
            withTransform({ scale(2f, 2f, Offset.Zero) }) {
                drawImage(image = image)

                if (showGrid) {
                    repeat(def.rows) { row ->
                        repeat(def.columns) { column ->
                            val topLeft = Offset(
                                x = (def.srcOffset?.x ?: 0f) + column * (size.width + spacingX),
                                y = (def.srcOffset?.y ?: 0f) + row * (size.height + spacingY)
                            )
                            drawRect(
                                color = Color.Black,
                                topLeft = topLeft,
                                size = size,
                                style = Stroke(width = 1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckboxText(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )

        Text(text)
    }
}

@Composable
private fun FrameIncrementer(
    frame: MutableIntState,
    speed: Float = 0.1f,
    totalFrames: Int,
) {
    val gameTime = remember { GameTime() }
    LaunchedEffect(totalFrames) {
        if (totalFrames <= 0) return@LaunchedEffect
        while (true) {
            withFrameNanos {
                gameTime.fixedUpdate(it)
                while (gameTime.accumulator >= TimeConfig.FIXED_DELTA_60) {
                    gameTime.accumulator -= TimeConfig.FIXED_DELTA_60
                }
                if (gameTime.deltaTime >= speed) {
                    frame.intValue = (frame.intValue + 1) % totalFrames
                }
            }
        }
    }
}

private fun Modifier.drawBackgroundSquares() = this.then(
    Modifier.drawBehind {
        val imageWidth = size.width
        val imageHeight = size.height
        val squareSize = 10f
        val horizontalSquares = (imageWidth / squareSize).toInt() + 1
        val verticalSquares = (imageHeight / squareSize).toInt() + 1

        for (i in 0 until horizontalSquares) {
            for (j in 0 until verticalSquares) {
                val isEven = (i + j) % 2 == 0
                drawRect(
                    color = if (isEven) Color.LightGray else Color.Gray,
                    topLeft = Offset(i * squareSize, j * squareSize),
                    size = when {
                        i == horizontalSquares - 1 && j == verticalSquares - 1 -> Size(
                            imageWidth - i * squareSize,
                            imageHeight - j * squareSize
                        )

                        i == horizontalSquares - 1 -> Size(
                            imageWidth - i * squareSize,
                            squareSize
                        )

                        j == verticalSquares - 1 -> Size(
                            squareSize,
                            imageHeight - j * squareSize
                        )

                        else -> Size(squareSize, squareSize)
                    }
                )
            }
        }
    }
)

