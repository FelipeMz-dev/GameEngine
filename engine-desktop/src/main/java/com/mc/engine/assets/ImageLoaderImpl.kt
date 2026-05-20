package com.mc.engine.assets

import com.mc.engine.graphics.GpuImage
import com.mc.engine.math.Vec2
import com.mc.engine.render.ImageLoader
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * Implementación Desktop de GpuImage usando BufferedImage.
 */
class DesktopImage(private val bufferedImage: BufferedImage) : GpuImage {
    override val width: Int = bufferedImage.width
    override val height: Int = bufferedImage.height

    fun toBufferedImage(): BufferedImage = bufferedImage
}

class ImageLoaderImpl : ImageLoader {
    override fun loadRes(
        resId: Int,
        hasAlpha: Boolean,
        srcOffset: Vec2?,
        srcSize: Vec2?
    ): GpuImage {
        // Para desktop, los recursos se cargan desde el classpath
        // Por ahora, lanzamos una excepción ya que desktop no tiene "resources" como Android
        throw UnsupportedOperationException("loadRes with resource ID is not supported on Desktop. Use loadPath instead.")
    }

    override fun loadPath(path: String): GpuImage {
        val file = File(path)
        val bufferedImage = ImageIO.read(file) ?: throw IllegalArgumentException("Could not load image from path: $path")
        return DesktopImage(bufferedImage)
    }
}
