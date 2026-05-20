package com.mc.engine.compose

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import com.mc.engine.assets.SpriteManager
import com.mc.engine.audio.AudioSystem
import com.mc.engine.core.GameScene
import com.mc.engine.core.SceneDependencies
import com.mc.engine.core.Viewport
import com.mc.engine.input.GameInput
import com.mc.engine.input.SensorInputAdapter
import com.mc.engine.math.Vec2
import com.mc.engine.VirtualResolution
import com.mc.engine.time.GameLoop

@Composable
fun GameSceneView(
    modifier: Modifier = Modifier,
    scene: GameScene,
    spriteManager: SpriteManager = rememberSpriteManager(),
    audioManager: AudioSystem = rememberAudioSystem(),
    gameInput: GameInput = rememberGameInput(),
    contentScale: ContentScale = ContentScale.Crop,
    virtualResolution: VirtualResolution = VirtualResolution.Undefined,
) {
    val loop = remember { GameLoop(scene) }
    val frameTicker = remember { mutableIntStateOf(0) }
    var resolution = remember { virtualResolution }
    var scale = remember { ScaleFactor.Unspecified }
    val textMeasurer = rememberTextMeasurer()
    val focusRequester = remember { FocusRequester() }
    val sensorInputAdapter = gameInput.sensorProcessor as? SensorInputAdapter

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        scene.attach(
            SceneDependencies(
                spriteManager = spriteManager,
                audioManager = audioManager,
                gameInput = gameInput
            )
        )
    }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { frameTime ->
                loop.onFrame(frameTime)
                frameTicker.intValue++
            }
        }
    }

    fun DrawScope.render() {
        clipRect(right = resolution.width, bottom = resolution.height) {
            val renderer = RendererImpl(
                this,
                spriteManager,
                textMeasurer,
                scene.camera2D,
            )
            scene.apply { render(renderer, loop.alpha()) }
        }
    }

    fun updateScreenSize(size: IntSize) {
        if (virtualResolution is VirtualResolution.Undefined) {
            resolution = VirtualResolution.Custom(size.toSize())
        }
        scale = contentScale.computeScaleFactor(
            resolution.toSize(),
            size.toSize()
        )
        val viewport = Viewport(
            size = Vec2(resolution.width, resolution.height),
            scale = Vec2(scale.scaleX, scale.scaleY)
        )
        scene.updateViewport(viewport)
    }

    Canvas(
        modifier = modifier
            .onSizeChanged { updateScreenSize(it) }
            .run {
                // Adapt the generic GameInput processors to the platform-specific "Processor" wrappers
                val tp = gameInput.touchProcessor
                val touchAdapter = when (tp) {
                    is com.mc.engine.input.touch.TouchProcessor -> tp
                    is com.mc.engine.input.touch.TouchManager -> com.mc.engine.input.touch.TouchProcessor(tp)
                    else -> null
                }
                touchAdapter?.let { gameTouchInput(it) } ?: this
            }
            .run {
                val kp = gameInput.keyboardProcessor
                val keyboardAdapter = when (kp) {
                    is com.mc.engine.input.keyboard.KeyboardProcessor -> kp
                    is com.mc.engine.input.keyboard.KeyboardManager -> com.mc.engine.input.keyboard.KeyboardProcessor(kp)
                    else -> null
                }
                keyboardAdapter?.let { gameKeyboardInput(it, focusRequester) } ?: this
            }
            .run {
                val mp = gameInput.mouseProcessor
                val mouseAdapter = when (mp) {
                    is com.mc.engine.input.mouse.MouseProcessor -> mp
                    is com.mc.engine.input.mouse.MouseManager -> com.mc.engine.input.mouse.MouseProcessor(mp)
                    else -> null
                }
                mouseAdapter?.let { gameMouseInput(it) } ?: this
            }
    ) {
        if (virtualResolution !is VirtualResolution.Undefined) {
            inset(scale.scaleX, scale.scaleY) {
                withTransform(
                    transformBlock = {
                        scale(scale.scaleX, scale.scaleY, Offset.Zero)
                    }
                ) { render() }
            }
        } else render()
        frameTicker.intValue
    }

    DisposableEffect(Unit) {
        sensorInputAdapter?.start()
        onDispose {
            sensorInputAdapter?.stop()
        }
    }
}