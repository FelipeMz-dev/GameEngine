package com.mc.engine.assets

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.mc.engine.graphics.GpuImage
import com.mc.engine.math.Vec2
import com.mc.engine.render.ImageLoader

/**
 * Implementación Android de ImageLoader que envuelve ImageBitmap en GpuImage.
 */
class AndroidImage(private val imageBitmap: ImageBitmap) : GpuImage {
    override val width: Int = imageBitmap.width
    override val height: Int = imageBitmap.height

    fun toImageBitmap(): ImageBitmap = imageBitmap
}

class ImageLoaderImpl(private val resources: android.content.res.Resources) : ImageLoader {
    override fun loadRes(
        resId: Int,
        hasAlpha: Boolean,
        srcOffset: Vec2?,
        srcSize: Vec2?
    ): GpuImage {
        val options = BitmapFactory.Options()
        options.inScaled = false
        options.inPreferredConfig = if (hasAlpha) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
        val source = BitmapFactory.decodeResource(resources, resId, options)
        val bitmap = when {
            srcOffset != null && srcSize != null -> Bitmap.createBitmap(
                source,
                srcOffset.x.toInt(),
                srcOffset.y.toInt(),
                srcSize.x.toInt().coerceAtMost(source.width - srcOffset.x.toInt()),
                srcSize.y.toInt().coerceAtMost(source.height - srcOffset.y.toInt())
            )
            srcOffset != null -> Bitmap.createBitmap(
                source,
                srcOffset.x.toInt(),
                srcOffset.y.toInt(),
                source.width - srcOffset.x.toInt(),
                source.height - srcOffset.y.toInt()
            )
            srcSize != null -> Bitmap.createBitmap(
                source,
                0,
                0,
                srcSize.x.toInt(),
                srcSize.y.toInt()
            )
            else -> source
        }
        return AndroidImage(bitmap.asImageBitmap())
    }

    override fun loadPath(path: String): GpuImage {
        val bitmap = BitmapFactory.decodeFile(path)
        return AndroidImage(bitmap.asImageBitmap())
    }
}
