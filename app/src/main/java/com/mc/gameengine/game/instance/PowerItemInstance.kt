package com.mc.gameengine.game.instance

import androidx.compose.ui.graphics.Color
import com.mc.gameengine.app.GameController
import com.mc.gameengine.engine.assets.Sprite
import com.mc.gameengine.engine.collision.BoxCollider
import com.mc.gameengine.engine.core.Instance
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.plus
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer
import com.mc.gameengine.game.assets.MainAudios
import com.mc.gameengine.game.effects.FadeEffect

class PowerItemInstance(
    private val controller: GameController,
    private val position: Vec2
) : Instance() {
    val type: PowerItemType = PowerItemType.entries.random()

    private val sprite = Sprite(type.sprite)

    private val collider = BoxCollider(this, 32f, 32f)

    private val fadeEffect = FadeEffect(0.02f)

    private var timer = 0f

    override fun onEnterScene() {
        updatePivot { Pivot.Center }
        updatePosition { position }
        collider.update { current }
        sprite.update { current }
        addCollider(collider)
    }

    override fun fixedUpdate(dt: Float) {
        timer += 0.02f

        if (timer >= 3) fadeEffect.animate()
        if (fadeEffect.isGone) deleteInstance(this)
    }

    override fun Renderer.onRender(state: TransformState) {
        val modifier = timer % 1f
        val size = Vec2.from(40) + 20 * modifier
        val alpha = (1f - modifier).coerceAtMost(0.8f)

        drawOval(
            size = size,
            state = current,
            color = Color.Yellow.copy(alpha = alpha),
        )

        sprite.draw(
            color = Color.White.copy(alpha = fadeEffect.alpha)
        )
    }

    fun onTake(): PowerItemType {
        audioPlayer.playSound(MainAudios.SND_COLLECT)
        controller.onPower(type)
        deleteInstance(this)
        return type
    }
}