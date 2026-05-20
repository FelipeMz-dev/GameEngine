package com.mc.engine.graphics.compose

import androidx.compose.ui.graphics.ImageBitmap
import com.mc.engine.assets.AndroidImage
import com.mc.engine.graphics.GpuImage

/**
 * Conversión Android-specific de GpuImage a ImageBitmap.
 */
actual fun GpuImage.toImageBitmap(): ImageBitmap {
	return (this as? AndroidImage)?.toImageBitmap()
		?: throw IllegalStateException("GpuImage is not an AndroidImage and cannot be converted to ImageBitmap on Android")
}
