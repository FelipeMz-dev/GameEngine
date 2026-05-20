package com.mc.engine.graphics

/**
 * Interfaz agnóstica de plataforma para representar una imagen.
 * Las implementaciones específicas de plataforma (Android, Desktop, etc.)
 * proporcionarán la funcionalidad concreta.
 */
interface GpuImage {
    val width: Int
    val height: Int
}
