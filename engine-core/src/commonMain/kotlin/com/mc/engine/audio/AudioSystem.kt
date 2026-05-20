package com.mc.engine.audio

import com.mc.engine.core.AudioId
import com.mc.engine.core.Instance
import com.mc.engine.math.Vec2

/**
 * Interfaz agnóstica de plataforma para gestión de audio.
 * Las implementaciones específicas de plataforma manejan los detalles.
 */
interface AudioSystem : AudioPlayer {

    fun loadSound(id: AudioId, resourceId: Any)

    fun loadMusic(id: AudioId, resourceId: Any)
}
