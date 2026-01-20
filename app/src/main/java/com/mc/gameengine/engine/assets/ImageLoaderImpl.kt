package com.mc.gameengine.engine.assets

import android.content.res.Resources
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.core.graphics.createBitmap
import com.mc.gameengine.engine.render.ImageLoader

class ImageLoaderImpl(private val resources: Resources) : ImageLoader {
    override fun loadRes(resId: Int): ImageBitmap {
        return ImageBitmap.Companion.imageResource(resources, resId)
    }

    override fun loadPath(path: String): ImageBitmap {
        val bitmap = BitmapFactory.decodeFile(path)
        val safeBitmap = bitmap ?: createBitmap(1, 1)
        return safeBitmap.asImageBitmap()
    }
}