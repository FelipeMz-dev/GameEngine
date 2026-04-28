package com.mc.gameengine.game.scene

import androidx.compose.ui.graphics.Color
import com.mc.gameengine.engine.core.GameScene
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer
import com.mc.gameengine.game.instance.collisions.BallLauncher
import com.mc.gameengine.game.instance.collisions.StackBlock

class CollisionScene : GameScene() {

    private val floorY = 700f

    init {
        addInstance(BallLauncher(launchPoint = Vec2(180f, floorY - 30f)))
        buildStructure()
    }

    private fun buildStructure() {
        val blockWidth = 76f
        val blockHeight = 44f
        val baseX = 880f
        val spacing = 10f

        repeat(4) { row ->
            val blocksInRow = 4 - row
            val offsetX = (row * (blockWidth + spacing)) / 2f
            repeat(blocksInRow) { col ->
                addInstance(
                    StackBlock(
                        start = Vec2(
                            x = baseX + offsetX + col * (blockWidth + spacing),
                            y = floorY - blockHeight / 2f - row * (blockHeight + 8f)
                        ),
                        size = Vec2(blockWidth, blockHeight),
                        mass = 1.4f + row * 0.4f,
                        color = if ((row + col) % 2 == 0) Color(0xFFBCAAA4) else Color(0xFFA1887F),
                        floorY = floorY
                    )
                )
            }
        }
    }

    override fun render(renderer: Renderer, alpha: Float) {
        renderer.drawRect(
            size = Vec2(viewportSize().x, 8f),
            state = com.mc.gameengine.engine.core.TransformState(
                position = Vec2(viewportSize().x / 2f, floorY),
                pivot = Pivot.Center
            ),
            color = Color(0xFF455A64)
        )
        super.render(renderer, alpha)
    }
}
