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
        addInstance(BallLauncher(launchPoint = Vec2(180f, floorY - 30f), floorY = floorY))
        buildStructure()
    }

    private fun buildStructure() {
        val woodLight = Color(0xFFD7B899)
        val woodDark = Color(0xFFB58B66)

        val blockSpecs = listOf(
            // Base: 3 columnas verticales pesadas.
            BlockSpec(Vec2(860f, floorY - 60f), Vec2(36f, 120f), 2.8f, woodDark),
            BlockSpec(Vec2(940f, floorY - 60f), Vec2(36f, 120f), 2.4f, woodLight),
            BlockSpec(Vec2(1020f, floorY - 60f), Vec2(36f, 120f), 3.0f, woodDark),

            // Primer nivel: vigas horizontales más ligeras.
            BlockSpec(Vec2(900f, floorY - 138f), Vec2(150f, 34f), 1.5f, woodLight),
            BlockSpec(Vec2(980f, floorY - 138f), Vec2(150f, 34f), 1.3f, woodLight),

            // Segundo nivel: dos columnas medianas.
            BlockSpec(Vec2(900f, floorY - 216f), Vec2(34f, 120f), 2.1f, woodDark),
            BlockSpec(Vec2(980f, floorY - 216f), Vec2(34f, 120f), 1.9f, woodDark),

            // Cima: viga horizontal liviana + bloque corto superior.
            BlockSpec(Vec2(940f, floorY - 292f), Vec2(170f, 30f), 1.2f, woodLight),
            BlockSpec(Vec2(940f, floorY - 340f), Vec2(56f, 70f), 1.0f, woodDark),
        )

        blockSpecs.forEach { spec ->
            addInstance(
                StackBlock(
                    start = spec.position,
                    size = spec.size,
                    mass = spec.mass,
                    color = spec.color,
                    floorY = floorY
                )
            )
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

private data class BlockSpec(
    val position: Vec2,
    val size: Vec2,
    val mass: Float,
    val color: Color
)
