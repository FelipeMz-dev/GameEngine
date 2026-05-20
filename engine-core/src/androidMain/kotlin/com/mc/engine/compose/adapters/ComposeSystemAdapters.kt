package com.mc.engine.compose.adapters

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.mc.engine.input.sensor.SensorInputAdapter
import com.mc.engine.input.sensor.SensorSystem
import com.mc.engine.input.sensor.SensorProcessor

/**
 * Adaptadores Compose para crear sistemas agnósticos.
 * Estos adapters casan LocalContext y LocalResources con interfaces agnósticas.
 */

@Composable
fun rememberAudioSystem(block: (com.mc.engine.audio.AudioSystem.() -> Unit) = {}): com.mc.engine.audio.AudioSystem {
    val context = LocalContext.current
    val audioSystem = remember {
        AudioSystem.androidImpl(context).apply { block() }
    }
    return audioSystem
}

@Composable
fun rememberSensorSystem(sensorProcessor: SensorProcessor): SensorSystem {
    val context = LocalContext.current
    val sensorSystem = remember {
        SensorInputAdapter(context, sensorProcessor) as SensorSystem
    }
    return sensorSystem
}

/**
 * Factory function para crear AudioSystem específico de Android.
 */
object AudioSystem {
    fun androidImpl(context: android.content.Context): com.mc.engine.audio.AudioSystem {
        return com.mc.engine.audio.AudioManager(context)
    }
}

