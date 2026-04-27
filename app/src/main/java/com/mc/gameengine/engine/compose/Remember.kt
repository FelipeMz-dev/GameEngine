package com.mc.gameengine.engine.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import com.mc.gameengine.engine.input.GameInput
import com.mc.gameengine.engine.assets.SpriteManager
import com.mc.gameengine.engine.assets.ImageLoaderImpl
import com.mc.gameengine.engine.input.keyboard.KeyboardManager
import com.mc.gameengine.engine.input.mouse.MouseManager
import com.mc.gameengine.engine.input.sensor.SensorInputAdapter
import com.mc.gameengine.engine.input.sensor.SensorManager
import com.mc.gameengine.engine.input.touch.TouchManager
import com.mc.gameengine.engine.input.sensor.SensorProcessor
import com.mc.gameengine.engine.audio.AudioManager
import com.mc.gameengine.engine.math.toVec2
import com.mc.gameengine.engine.render.VirtualResolution

@Composable
fun rememberSpriteManager(): SpriteManager {
    val resources = LocalResources.current
    val spriteManager = remember { SpriteManager(ImageLoaderImpl(resources)) }
    return spriteManager
}

@Composable
fun rememberAudioManager(block: (AudioManager.() -> Unit) = {}): AudioManager {
    val context = LocalContext.current
    val audioManager = remember { AudioManager(context) }
    audioManager.block()
    return audioManager
}

@Composable
fun rememberGameInput(): GameInput {
    val gameInput = remember {
        GameInput.Builder()
            .withSensor(SensorManager())
            .withTouch(TouchManager())
            .withKeyboard(KeyboardManager())
            .withMouse(MouseManager())
            .build()
    }
    return gameInput
}

@Composable
fun rememberGameInput(
    sensorManager: SensorManager? = null,
    touchManager: TouchManager? = null,
    keyboardManager: KeyboardManager? = null,
    mouseManager: MouseManager? = null
): GameInput {
    val gameInput = remember {
        val builder = GameInput.Builder()
        sensorManager?.apply { builder.withSensor(this) }
        touchManager?.apply { builder.withTouch(this) }
        keyboardManager?.apply { builder.withKeyboard(this) }
        mouseManager?.apply { builder.withMouse(this) }
        builder.build()
    }
    return gameInput
}

@Composable
fun rememberSensorInputAdapter(sensorProcessor: SensorProcessor): SensorInputAdapter {
    val context = LocalContext.current
    val sensorInputAdapter = remember { SensorInputAdapter(context, sensorProcessor) }
    return sensorInputAdapter
}

@Composable
fun rememberCamera2D(
    virtualResolution: VirtualResolution
) = remember { Camera2D(viewportSize = virtualResolution.toSize().toVec2()) }