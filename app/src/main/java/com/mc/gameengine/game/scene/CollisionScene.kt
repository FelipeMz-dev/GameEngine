package com.mc.gameengine.game.scene

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.mc.gameengine.engine.core.GameScene
import com.mc.gameengine.engine.math.div
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer
import com.mc.gameengine.game.instance.collisions.ControllableEntity
import com.mc.gameengine.game.instance.collisions.Obstacle

class CollisionScene: GameScene() {

    init {
        addInstance(ControllableEntity())
        addInstance(Obstacle())
    }

    override fun render(renderer: Renderer, alpha: Float) {
        renderer.drawAxis(viewportSize() / 2f)
        renderer.drawText(
            position = viewportSize() / 2f,
            text = "CENTER",
            pivot = Pivot.Center,
            style = TextStyle(color = Color.Gray)
        )
        super.render(renderer, alpha)
    }
}