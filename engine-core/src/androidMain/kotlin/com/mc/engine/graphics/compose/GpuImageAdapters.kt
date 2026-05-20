package com.mc.engine.graphics.compose

import androidx.compose.ui.graphics.ImageBitmap

/**
 * Extensión para convertir GpuImage a ImageBitmap en plataformas que lo soporten.
 * En plataformas que no usan Compose (como Desktop), esta función puede no estar disponible.
 */
expect fun GpuImage.toImageBitmap(): ImageBitmap

