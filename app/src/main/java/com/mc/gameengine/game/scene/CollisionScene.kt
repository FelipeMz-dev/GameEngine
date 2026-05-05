package com.mc.gameengine.game.scene

import androidx.compose.ui.graphics.Color
import com.mc.gameengine.engine.core.GameScene
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer
import com.mc.gameengine.game.instance.BallLauncher
import com.mc.gameengine.game.instance.StackBlock
import com.mc.gameengine.game.instance.spec.BlockSpec

class CollisionScene : GameScene() {

    private val floorY = 700f

    init {
        addInstance(BallLauncher(launchPoint = Vec2(180f, floorY - 100f), floorY = floorY))
        buildStructure()
    }

    private fun buildStructure() {
        val woodLight = Color(0xFFD2AE8B)
        val woodDark = Color(0xFF9F7451)
        val woodAccent = Color(0xFFA28869)

        val blockSpecs = listOf(
            // Base: 5 columnas verticales compactas y simétricas.
            BlockSpec(Vec2(740f, floorY - 80f), Vec2(40f, 160f), 2.8f, woodDark),
            BlockSpec(Vec2(810f, floorY - 80f), Vec2(40f, 160f), 2.3f, woodLight),
            BlockSpec(Vec2(880f, floorY - 80f), Vec2(40f, 160f), 2.5f, woodDark),
            BlockSpec(Vec2(950f, floorY - 80f), Vec2(40f, 160f), 2.3f, woodLight),
            BlockSpec(Vec2(1020f, floorY - 80f), Vec2(40f, 160f), 2.8f, woodDark),

            // Nivel 1: vigas horizontales de soporte sólidas y compactas.
            BlockSpec(Vec2(880f, floorY - 200f), Vec2(200f, 35f), 2.2f, woodLight),

            // Nivel 2: columnas medianas simétricas más cercanas.
            BlockSpec(Vec2(795f, floorY - 300f), Vec2(35f, 120f), 2.2f, woodDark),
            BlockSpec(Vec2(880f, floorY - 300f), Vec2(35f, 120f), 2.1f, woodLight),
            BlockSpec(Vec2(965f, floorY - 300f), Vec2(35f, 120f), 2.0f, woodDark),

            // Nivel 3: vigas de soporte intermedias.
            BlockSpec(Vec2(880f, floorY - 400f), Vec2(160f, 32f), 1.8f, woodAccent),

            // Nivel 4: pilares que convergen hacia el centro.
            BlockSpec(Vec2(820f, floorY - 480f), Vec2(32f, 100f), 2.0f, woodLight),
            BlockSpec(Vec2(940f, floorY - 480f), Vec2(32f, 100f), 2.1f, woodDark),

            // Nivel 5: estructura central compacta.
            BlockSpec(Vec2(880f, floorY - 580f), Vec2(100f, 30f), 1.4f, woodLight),

            // Nivel 6: cúspide piramidal con bloques más juntos.
            BlockSpec(Vec2(860f, floorY - 650f), Vec2(45f, 80f), 1.2f, woodDark),
            BlockSpec(Vec2(900f, floorY - 650f), Vec2(45f, 80f), 1.2f, woodLight),

            // Cima: techo de la estructura.
            BlockSpec(Vec2(880f, floorY - 730f), Vec2(70f, 28f), 1.0f, woodAccent),
            BlockSpec(Vec2(880f, floorY - 760f), Vec2(45f, 23f), 0.9f, woodDark),
        )

        blockSpecs.forEach { spec ->
            addInstance(
                StackBlock(
                    start = spec.position,
                    size = spec.size,
                    density = spec.mass,
                    color = spec.color,
                )
            )
        }
    }

    override fun render(renderer: Renderer, alpha: Float) {
        renderer.drawRect(
            size = Vec2(viewportSize().x, 10f),
            state = com.mc.gameengine.engine.core.TransformState(
                position = Vec2(viewportSize().x / 2f, floorY),
                pivot = Pivot.Center
            ),
            color = Color(0xFF455A64)
        )
        super.render(renderer, alpha)
    }
}

