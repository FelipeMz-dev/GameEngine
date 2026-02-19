package com.mc.gameengine.app

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.mc.gameengine.engine.assets.AssetsManager
import com.mc.gameengine.engine.assets.AtlasSprite
import com.mc.gameengine.engine.assets.FrameListSprite
import com.mc.gameengine.engine.render.ImageLoader
import com.mc.gameengine.engine.assets.SingleImageSprite
import com.mc.gameengine.engine.assets.ImageLoaderImpl
import com.mc.gameengine.game.assets.SpritesDinoPlayer
import com.mc.gameengine.game.assets.SpritesMain
import com.mc.gameengine.game.assets.registerMainSprites
import kotlinx.coroutines.delay

@Preview(showBackground = true, device = "id:tv_4k")
@Composable
private fun ShowSpritePreview() {

    val resources = LocalResources.current
    val imageLoader: ImageLoader = ImageLoaderImpl(resources)
    val manager = AssetsManager(imageLoader)
    manager.registerMainSprites()

    val sprite = manager.get(SpritesMain.METEOR_EXPLOSION)  //TODO: Change sprite here for preview

    val frame = remember { mutableIntStateOf(0) }

    val totalFrames = when (sprite) {
        is AtlasSprite -> sprite.columns * sprite.rows
        is FrameListSprite -> sprite.frames.size
        else -> 1
    }

    FrameIncrementer(
        frame = frame,
        duration = 0.1f,
        totalFrames = totalFrames,
    )

    when (sprite) {
        is AtlasSprite -> ContentAnimationAtlas(
            atlasSprite = sprite,
            frame = frame.intValue
        )

        is FrameListSprite -> ContentAnimationFrameList(
            frameListSprite = sprite,
            frame = frame.intValue
        )

        is SingleImageSprite -> {
            ContentImageSprite(sprite = sprite)
        }
    }
}

@Composable
private fun ContentImageSprite(sprite: SingleImageSprite) {

    val density = LocalDensity.current

    Canvas(
        modifier = Modifier.Companion
            .width(density.run { (sprite.spriteWidth).toDp() })
            .height(density.run { (sprite.spriteHeight).toDp() })
            .drawBackgroundSquares()
    ) {
        drawImage(
            image = sprite.image,
            srcOffset = IntOffset(sprite.offsetX, sprite.offsetY),
            srcSize = IntSize(sprite.spriteWidth, sprite.spriteHeight),
        )
    }
}

@Composable
private fun ContentAnimationFrameList(
    frameListSprite: FrameListSprite,
    frame: Int
) {
    var showAnimation by remember { mutableStateOf(true) }
    val maxWidth = frameListSprite.frames.maxOf { it.width }
    val maxHeight = frameListSprite.frames.maxOf { it.height }
    val density = LocalDensity.current

    Column(
        modifier = Modifier.Companion.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        CheckboxText(
            text = "Animation",
            checked = showAnimation,
        ) { showAnimation = it }

        Canvas(
            modifier = Modifier.Companion
                .width(density.run { (maxWidth).toDp() })
                .height(density.run { (maxHeight).toDp() })
                .drawBackgroundSquares()
        ) {
            drawImage(
                image = frameListSprite.frames[if (showAnimation) frame else 0],
                srcOffset = IntOffset(frameListSprite.offsetX, frameListSprite.offsetY),
                srcSize = IntSize(frameListSprite.spriteWidth, frameListSprite.spriteHeight),
            )
        }
    }
}

@Composable
private fun ContentAnimationAtlas(
    atlasSprite: AtlasSprite,
    frame: Int
) {
    val density = LocalDensity.current
    var showGrid by remember { mutableStateOf(true) }
    var showAnimation by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.Companion.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        CheckboxText(
            text = "Gird Frames",
            checked = showGrid,
        ) { showGrid = it }

        Canvas(
            modifier = Modifier.Companion
                .width(density.run { (atlasSprite.image.width).toDp() })
                .height(density.run { (atlasSprite.image.height).toDp() })
                .drawBackgroundSquares()
        ) {
            drawImage(image = atlasSprite.image)

            if (showGrid) {
                repeat(atlasSprite.rows) { row ->
                    repeat(atlasSprite.columns) { column ->
                        val topLeft = Offset(
                            x = atlasSprite.offsetX + column * (atlasSprite.spriteWidth + atlasSprite.spacingX).toFloat(),
                            y = atlasSprite.offsetY + row * (atlasSprite.spriteHeight + atlasSprite.spacingY).toFloat()
                        )
                        drawRect(
                            color = Color.Companion.Black,
                            topLeft = topLeft,
                            size = Size(
                                atlasSprite.spriteWidth.toFloat(),
                                atlasSprite.spriteHeight.toFloat()
                            ),
                            style = Stroke(width = 1f)
                        )
                    }
                }
            }
        }

        CheckboxText(
            text = "Show Animation",
            checked = showAnimation,
        ) { showAnimation = it }

        Canvas(
            modifier = Modifier.Companion
                .width(with(LocalDensity.current) { (atlasSprite.spriteWidth).toDp() })
                .height(with(LocalDensity.current) { (atlasSprite.spriteHeight).toDp() })
                .drawBackgroundSquares()
        ) {
            val srcOffset = if (showAnimation) {
                IntOffset(
                    x = atlasSprite.offsetX + (frame % atlasSprite.columns) * (atlasSprite.spriteWidth + atlasSprite.spacingX),
                    y = atlasSprite.offsetY + (frame / atlasSprite.columns) * (atlasSprite.spriteHeight + atlasSprite.spacingY)
                )
            } else IntOffset(atlasSprite.offsetX, atlasSprite.offsetY)
            drawImage(
                atlasSprite.image,
                srcOffset = srcOffset,
                srcSize = IntSize(atlasSprite.spriteWidth, atlasSprite.spriteHeight),
            )
        }
    }
}

@Composable
private fun CheckboxText(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(verticalAlignment = Alignment.Companion.CenterVertically) {
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
    duration: Float,
    totalFrames: Int,
) {
    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos {
                frame.intValue++
                if (frame.intValue >= totalFrames) {
                    frame.intValue = 0
                }
            }
            delay((duration * 1000).toLong())
        }
    }
}

private fun Modifier.drawBackgroundSquares() = this.then(
    Modifier.Companion.drawBehind {
        val imageWidth = size.width
        val imageHeight = size.height
        val squareSize = 10f
        val horizontalSquares = (imageWidth / squareSize).toInt() + 1
        val verticalSquares = (imageHeight / squareSize).toInt() + 1

        for (i in 0 until horizontalSquares) {
            for (j in 0 until verticalSquares) {
                val isEven = (i + j) % 2 == 0
                drawRect(
                    color = if (isEven) Color.Companion.LightGray else Color.Companion.Gray,
                    topLeft = Offset(i * squareSize, j * squareSize),
                    size = when {
                        i == horizontalSquares - 1 && j == verticalSquares - 1 -> Size(
                            imageWidth - i * squareSize,
                            imageHeight - j * squareSize
                        )

                        i == horizontalSquares - 1 -> Size(imageWidth - i * squareSize, squareSize)
                        j == verticalSquares - 1 -> Size(squareSize, imageHeight - j * squareSize)
                        else -> Size(squareSize, squareSize)
                    }
                )
            }
        }
    }
)