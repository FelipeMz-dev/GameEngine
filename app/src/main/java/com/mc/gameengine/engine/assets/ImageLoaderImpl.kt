package com.mc.gameengine.engine.assets

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.render.ImageLoader

class ImageLoaderImpl(private val resources: Resources) : ImageLoader {
    override fun loadRes(
        resId: Int,
        hasAlpha: Boolean,
        srcOffset: Vec2?,
        srcSize: Vec2?
    ): ImageBitmap {
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
        return bitmap.asImageBitmap()
    }

    override fun loadPath(path: String): ImageBitmap {
        val bitmap = BitmapFactory.decodeFile(path)
        return bitmap.asImageBitmap()
    }

}