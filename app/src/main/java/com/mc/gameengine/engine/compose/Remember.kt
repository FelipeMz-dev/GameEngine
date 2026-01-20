package com.mc.gameengine.engine.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import com.mc.gameengine.engine.assets.AssetsManager
import com.mc.gameengine.engine.assets.ImageLoaderImpl
import com.mc.gameengine.engine.input.DeviceRotationAdapter
import com.mc.gameengine.engine.input.InputManager
import com.mc.gameengine.engine.input.SensorInputAdapter
import com.mc.gameengine.engine.input.TouchProcessor

@Composable
fun rememberAssetsManager(): AssetsManager {
    val resources = LocalResources.current
    val assetsManager = remember { AssetsManager(ImageLoaderImpl(resources)) }
    return assetsManager
}

@Composable
fun rememberInputManager() = remember { InputManager() }

@Composable
fun rememberTouchProcessor(input: InputManager) = remember { TouchProcessor(input) }

@Composable
fun rememberSensorInputAdapter(input: InputManager): SensorInputAdapter {
    val context = LocalContext.current
    val sensorInputAdapter = remember { SensorInputAdapter(context, input) }
    return sensorInputAdapter
}

@Composable
fun rememberDeviceRotationAdapter(inputManager: InputManager): DeviceRotationAdapter {
    val context = LocalContext.current
    val deviceRotationAdapter = remember { DeviceRotationAdapter(context, inputManager) }
    return deviceRotationAdapter
}