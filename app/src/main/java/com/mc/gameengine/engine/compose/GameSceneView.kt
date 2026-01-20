package com.mc.gameengine.engine.compose

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.toSize
import com.mc.gameengine.core.math.Vec2
import com.mc.gameengine.engine.assets.AssetsManager
import com.mc.gameengine.engine.core.GameScene
import com.mc.gameengine.engine.input.SensorInputAdapter
import com.mc.gameengine.engine.input.TouchProcessor
import com.mc.gameengine.engine.render.VirtualResolution
import com.mc.gameengine.engine.time.GameLoop

@Composable
fun GameSceneView(
    modifier: Modifier = Modifier,
    scene: GameScene,
    assetsManager: AssetsManager = rememberAssetsManager(),
    contentScale: ContentScale = ContentScale.Crop,
    virtualResolution: VirtualResolution = VirtualResolution.Undefined,
    touchProcessor: TouchProcessor? = null,
    sensorInputAdapter: SensorInputAdapter? = null
) {
    val currentView = LocalView.current
    val loop = remember { GameLoop(scene) }
    val frameTicker = remember { mutableIntStateOf(0) }
    val textMeasurer = rememberTextMeasurer()
    var resolution = remember { virtualResolution }
    var scale = remember { ScaleFactor.Unspecified }

    fun DrawScope.render() {
        clipRect(right = resolution.width, bottom = resolution.height) {
            val renderer = RendererImpl(this, assetsManager, textMeasurer)
            scene.render(renderer, loop.alpha())
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { frameTime ->
                loop.onFrame(frameTime)
                frameTicker.intValue++
            }
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
                scene.updateViewportSize(resolution)
            }
            .then(
                touchProcessor?.let {
                    Modifier.gameTouchInput(it, scale)
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