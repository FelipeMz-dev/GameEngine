package com.mc.engine.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import com.mc.engine.assets.ImageLoaderImpl
import com.mc.engine.assets.SpriteManager
import com.mc.engine.AudioManager
import com.mc.engine.audio.AudioSystem
import com.mc.engine.input.GameInput
import com.mc.engine.input.keyboard.KeyboardManager
import com.mc.engine.input.mouse.MouseManager
import com.mc.engine.input.sensor.SensorInputAdapter
import com.mc.engine.input.sensor.SensorManager
import com.mc.engine.input.sensor.SensorProcessor
import com.mc.engine.input.touch.TouchManager

@Composable
fun rememberSpriteManager(): SpriteManager {
    val resources = LocalResources.current
    val spriteManager = remember { SpriteManager(ImageLoaderImpl(resources)) }
    return spriteManager
}

@Composable
fun rememberAudioSystem(block: (AudioSystem.() -> Unit) = {}): AudioSystem {
    val context = LocalContext.current
    val audioSystem = remember { 
        AudioManager(context).apply { block() } as AudioSystem
    }
    return audioSystem
}

@Composable
fun rememberSensorSystem(sensorProcessor: SensorProcessor): SensorInputAdapter {
    val context = LocalContext.current
    val sensorSystem = remember {
        SensorInputAdapter(context, sensorProcessor)
    }
    return sensorSystem
}

@Composable
fun rememberSensorInputAdapter(sensorProcessor: SensorProcessor): SensorInputAdapter {
    val context = LocalContext.current
    val sensorInputAdapter = remember { SensorInputAdapter(context, sensorProcessor) }
    return sensorInputAdapter
}

@Composable
fun rememberGameInput(): GameInput {
    val context = LocalContext.current
    val gameInput = remember {
        val sensorManager = SensorManager()
        val sensorProcessor = SensorProcessor(sensorManager)
        val sensorInputAdapter = SensorInputAdapter(context, sensorProcessor)
        
        GameInput.Builder()
            .withSensor(sensorInputAdapter)
            .withTouch(TouchManager())
            .withKeyboard(KeyboardManager())
            .withMouse(MouseManager())
            .build()
    }
    return gameInput
}

@Composable
fun rememberGameInput(
    sensorProcessor: SensorProcessor? = null,
    touchManager: TouchManager? = null,
    keyboardManager: KeyboardManager? = null,
    mouseManager: MouseManager? = null
): GameInput {
    val gameInput = remember {
        val builder = GameInput.Builder()
        sensorProcessor?.apply { builder.withSensor(this) }
        touchManager?.apply { builder.withTouch(this) }
        keyboardManager?.apply { builder.withKeyboard(this) }
        mouseManager?.apply { builder.withMouse(this) }
        builder.build()
    }
    return gameInput
}
