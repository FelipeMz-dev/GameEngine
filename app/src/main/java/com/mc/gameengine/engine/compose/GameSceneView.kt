package com.mc.gameengine.engine.compose

import android.content.Context
import android.view.KeyEvent
import android.view.SurfaceView
import android.view.View
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.viewinterop.AndroidView
import com.mc.gameengine.engine.math.toVec2
import com.mc.gameengine.engine.assets.AssetsManager
import com.mc.gameengine.engine.core.GameScene
import com.mc.gameengine.engine.input.GameInput
import com.mc.gameengine.engine.render.VirtualResolution
import com.mc.gameengine.engine.time.GameLoop

@Composable
fun GameSceneView(
    modifier: Modifier = Modifier,
    scene: GameScene,
    assetsManager: AssetsManager = rememberAssetsManager(),
    gameInput: GameInput = rememberGameInput(),
    contentScale: ContentScale = ContentScale.Crop,
    virtualResolution: VirtualResolution = VirtualResolution.Undefined,
    isPaused: Boolean = false,
    camera2D: Camera2D = remember { Camera2D(viewportSize = virtualResolution.toSize().toVec2()) }
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
        scene.attachAssets(assetsManager)
        scene.attachInput(gameInput)
    }

    LaunchedEffect(Unit, isPaused) {
        while (true) {
            withFrameNanos { frameTime ->
                if (!isPaused) {
                    loop.onFrame(frameTime)
                    frameTicker.intValue++
                }
            }
        }
    }

    fun DrawScope.render() {
        clipRect(right = resolution.width, bottom = resolution.height) {
            val renderer = RendererImpl(
                this,
                assetsManager,
                textMeasurer,
                camera2D,
            )
            scene.apply { render(renderer, loop.alpha()) }
        }
    }

    Canvas(
        modifier = modifier
            .onSizeChanged {
                if (virtualResolution is VirtualResolution.Undefined) {
                    resolution = VirtualResolution.Custom(it.toSize())
                }
                scale = contentScale.computeScaleFactor(
                    resolution.toSize(),
                    it.toSize()
                )
                scene.updateViewportScale(scale)
                scene.updateViewportSize(resolution)
            }
            .then(
                gameInput.touchProcessor?.let {
                    Modifier.gameTouchInput(it)
                } ?: Modifier
            )
            .then(
                gameInput.keyboardProcessor?.run {
                    Modifier
                        .focusable()
                        .focusRequester(focusRequester)
                        .onKeyEvent {
                            onKeyEvent(it)
                        }
                } ?: Modifier
            )
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