package com.mc.gameengine.engine.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import com.mc.gameengine.engine.input.GameInput
import com.mc.gameengine.engine.assets.AssetsManager
import com.mc.gameengine.engine.assets.ImageLoaderImpl
import com.mc.gameengine.engine.input.KeyboardManager
import com.mc.gameengine.engine.input.SensorInputAdapter
import com.mc.gameengine.engine.input.SensorManager
import com.mc.gameengine.engine.input.TouchManager
import com.mc.gameengine.engine.input.SensorProcessor

@Composable
fun rememberAssetsManager(): AssetsManager {
    val resources = LocalResources.current
    val assetsManager = remember { AssetsManager(ImageLoaderImpl(resources)) }
    return assetsManager
}

@Composable
fun rememberGameInput(): GameInput {
    val gameInput = remember {
        GameInput.Builder()
            .withSensor(SensorManager())
            .withTouch(TouchManager())
            .withKeyboard(KeyboardManager())
            .build()
    }
    return gameInput
}

@Composable
fun rememberSensorInputAdapter(sensorProcessor: SensorProcessor): SensorInputAdapter {
    val context = LocalContext.current
    val sensorInputAdapter = remember { SensorInputAdapter(context, sensorProcessor) }
    return sensorInputAdapter
}