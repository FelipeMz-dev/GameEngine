package com.mc.gameengine.engine.compose

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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import com.mc.gameengine.engine.assets.SpriteManager
import com.mc.gameengine.engine.core.GameScene
import com.mc.gameengine.engine.core.SceneDependencies
import com.mc.gameengine.engine.input.GameInput
import com.mc.gameengine.engine.render.VirtualResolution
import com.mc.gameengine.engine.time.GameLoop
import com.mc.gameengine.engine.audio.AudioManager
import com.mc.gameengine.engine.core.Viewport
import com.mc.gameengine.engine.math.Vec2

@Composable
fun GameSceneView(
    modifier: Modifier = Modifier,
    scene: GameScene,
    spriteManager: SpriteManager = rememberSpriteManager(),
    audioManager: AudioManager = rememberAudioManager(),
    gameInput: GameInput = rememberGameInput(),
    contentScale: ContentScale = ContentScale.Crop,
    virtualResolution: VirtualResolution = VirtualResolution.Undefined,
) {
    val currentView = LocalView.current
    val loop = remember { GameLoop(scene) }
    val frameTicker = remember { mutableIntStateOf(0) }
    var resolution = remember { virtualResolution }
    var scale = remember { ScaleFactor.Unspecified }
    val textMeasurer = rememberTextMeasurer()
    val focusRequester = remember { FocusRequester() }
    val sensorInputAdapter = gameInput.sensorProcessor?.let {
        rememberSensorInputAdapter(it)
    }

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
                val touch = gameInput.touchProcessor
                touch?.let { gameTouchInput(touch) } ?: this
            }
            .run {
                val keyboard = gameInput.keyboardProcessor
                keyboard?.let { gameKeyboardInput(keyboard, focusRequester) } ?: this
            }
            .run {
                val mouse = gameInput.mouseProcessor
                mouse?.let { gameMouseInput(mouse) } ?: this
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
        currentView.keepScreenOn = true
        sensorInputAdapter?.start()
        onDispose {
            currentView.keepScreenOn = false
            sensorInputAdapter?.stop()
        }
    }
}