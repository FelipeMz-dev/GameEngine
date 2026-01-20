package com.mc.gameengine.engine.render

import androidx.compose.ui.graphics.ImageBitmap

interface ImageLoader {
    fun loadRes(resId: Int): ImageBitmap
    fun loadPath(path: String): ImageBitmap
}