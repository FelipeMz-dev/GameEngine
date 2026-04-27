package com.mc.gameengine.engine.collision

import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import com.mc.gameengine.engine.assets.SpriteManager
import com.mc.gameengine.engine.assets.SpriteSource
import com.mc.gameengine.engine.compose.RenderDepth
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.core.SpriteId
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.math.AABB
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.minus
import com.mc.gameengine.engine.math.plus
import com.mc.gameengine.engine.math.resolve
import com.mc.gameengine.engine.math.rotate
import com.mc.gameengine.engine.math.times
import com.mc.gameengine.engine.render.Renderer

class MaskCollider(
    override val owner: Instance,
    private var spriteId: SpriteId
) : Collider(owner),
    CollisionCenterDelegate by CollisionCenterDelegateImpl(),
    CollisionVerticesDelegate by CollisionVerticesDelegateImpl() {

    private lateinit var source: SpriteSource

    var size = Vec2.Zero
        private set
    var bufferCache: List<Vec2>? = null
    var frame = 0

    override fun Renderer.debugDraw() {
        drawSprite(
            spriteId = spriteId,
            frame = frame,
            state = state,
            deep = RenderDepth.DEBUG,
            color = Color.Red.copy(alpha = 0.5f),
            blendMode = BlendMode.SrcIn
        )
    }

    override fun onUpdateAABB(): AABB {
        val vertices = getVertices()
        val minX = vertices.minOf { it.x }
        val minY = vertices.minOf { it.y }
        val maxX = vertices.maxOf { it.x }
        val maxY = vertices.maxOf { it.y }
        return AABB(
            x = minX,
            y = minY,
            width = maxX - minX,
            height = maxY - minY
        )
    }

    override fun onUpdate() {
        bufferCache = null
        clearVertices()
        clearCenter()
    }

    fun updateFrame(frame: Int) {
        if (this.frame != frame) {
            this.frame = frame
            bufferCache = null
        }
    }

    internal fun SpriteManager.loadSource() {
        if (!::source.isInitialized) {
            source = get(spriteId)
            size = Vec2(source.frameWidth, source.frameHeight)
            onUpdate()
            syncAABB()
        }
    }

    internal fun getBuffer(): List<Vec2>? {
        if (bufferCache == null) {
            val spriteHeight = source.frameHeight
            val spriteWidth = source.frameWidth
            val buffer = IntArray(spriteWidth * spriteHeight)
            source.frameAt(frame)?.readPixels(
                buffer = buffer,
                width = spriteWidth,
                height = spriteHeight
            )
            bufferCache = buffer.transformBuffer(size, state)
        }
        return bufferCache
    }

    fun getVertices(): List<Vec2> = computeVertices()

    fun getAxes(): List<Vec2> = computeAxes()

    fun getCenter() = computeCenter()
}

fun IntArray.transformBuffer(size: Vec2, transform: TransformState): List<Vec2> {
    val maskPos = transform.position
    val maskAngle = transform.angle
    val maskScale = transform.scale
    val maskPivot = transform.pivot
    val maskFlipX = transform.flipX
    val maskFlipY = transform.flipY
    val maskWidth = size.x
    val maskHeight = size.y
    val scaledSize = size * maskScale
    val pivotOffset = maskPivot.resolve(scaledSize)

    return this.mapIndexed { index, item ->
        if (item == 0) null
        else {
            val x = index % maskWidth
            val y = index / maskWidth
            val localFlipped = Vec2(
                x = if (maskFlipX) maskWidth - x else x,
                y = if (maskFlipY) maskHeight - y else y
            )
            val localRel = localFlipped * maskScale
            val rotated = (localRel - pivotOffset).rotate(maskAngle)
            rotated + maskPos
        }
    }.filterNotNull()
}
