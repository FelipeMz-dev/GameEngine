package com.mc.engine.render

import com.mc.engine.graphics.GpuImage
import com.mc.engine.math.Vec2

interface ImageLoader {
    fun loadRes(
        resId: Int,
        hasAlpha: Boolean = true,
        srcOffset: Vec2? = null,
        srcSize: Vec2? = null
    ): GpuImage
    fun loadPath(path: String): GpuImage
}