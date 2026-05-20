package com.mc.engine.graphics

/**
 * Representación agnóstica de plataforma para rectángulos.
 */
data class GpuRect(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
) {
    val width: Float
        get() = right - left

    val height: Float
        get() = bottom - top

    val size: GpuSize
        get() = GpuSize(width, height)

    val topLeft: GpuOffset
        get() = GpuOffset(left, top)

    val bottomRight: GpuOffset
        get() = GpuOffset(right, bottom)

    val center: GpuOffset
        get() = GpuOffset((left + right) / 2, (top + bottom) / 2)

    companion object {
        fun fromLTWH(left: Float, top: Float, width: Float, height: Float): GpuRect =
            GpuRect(left, top, left + width, top + height)

        fun fromCenter(center: GpuOffset, width: Float, height: Float): GpuRect {
            val halfWidth = width / 2
            val halfHeight = height / 2
            return GpuRect(
                center.x - halfWidth,
                center.y - halfHeight,
                center.x + halfWidth,
                center.y + halfHeight
            )
        }
    }

    fun contains(offset: GpuOffset): Boolean =
        offset.x >= left && offset.x <= right && offset.y >= top && offset.y <= bottom

    fun overlaps(other: GpuRect): Boolean =
        left < other.right && right > other.left && top < other.bottom && bottom > other.top

    fun translate(dx: Float, dy: Float): GpuRect =
        GpuRect(left + dx, top + dy, right + dx, bottom + dy)

    fun inflate(dx: Float, dy: Float): GpuRect =
        GpuRect(left - dx, top - dy, right + dx, bottom + dy)
}

