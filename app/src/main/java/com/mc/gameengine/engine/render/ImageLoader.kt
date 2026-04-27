package com.mc.gameengine.engine.render

import androidx.compose.ui.graphics.ImageBitmap
import com.mc.gameengine.engine.math.Vec2

interface ImageLoader {
    fun loadRes(
        resId: Int,
        hasAlpha: Boolean = true,
        srcOffset: Vec2? = null,
        srcSize: Vec2? = null
    ): ImageBitmap
    fun loadPath(path: String): ImageBitmap
}