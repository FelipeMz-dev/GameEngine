package com.mc.gameengine.game.scene

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.mc.gameengine.app.GameController
import com.mc.gameengine.engine.assets.Sprite
import com.mc.gameengine.engine.core.GameScene
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer
import com.mc.gameengine.game.assets.SpritesMain
import com.mc.gameengine.game.instance.Ball
import com.mc.gameengine.game.instance.Dinosaur
import com.mc.gameengine.game.instance.Egg
import com.mc.gameengine.game.instance.FallObject
import com.mc.gameengine.game.instance.FallObjectSpawner
import com.mc.gameengine.game.instance.Meteor
import com.mc.gameengine.game.instance.PowerItem
import com.mc.gameengine.game.instance.PowerItemInstance

class MainScene(
    private val gameController: GameController
) : GameScene() {

    private val dinosaur = Dinosaur(gameController)
    private val meteorSpawner = FallObjectSpawner(gameController, ::Meteor)
    private val eggSpawner = FallObjectSpawner(gameController, ::Egg, 5..20)
    private val ballSpawner = FallObjectSpawner(gameController, ::Ball, 1..4)

    private val spritePortal = Sprite(SpritesMain.portal)
    private var hasPortal = false

    init {
        addInstance(dinosaur)
        addInstance(meteorSpawner)
        addInstance(eggSpawner)
        addInstance(ballSpawner)
        spritePortal.update { it.copy(pivot = Pivot.Center) }
    }

    override fun fixedUpdate(dt: Float) {
        if (!gameController.pause) super.fixedUpdate(dt)
        hasPortal = PowerItem.Portal in gameController.availablePowers
        if (hasPortal) spritePortal.animate(dt)
        when {
            gameController.isBoostingLeft -> dinosaur.onBoostLeft()
            gameController.isBoostingRight -> dinosaur.onBoostRight()
            gameController.isDie -> {
                gameController.clearPowers()
                meteorSpawner.resetSpawnTimer()
                eggSpawner.resetSpawnTimer()
                ballSpawner.resetSpawnTimer()
                deleteAllInstances { it is Meteor || it is FallObject || it is PowerItemInstance }
            }
        }
        gameController.syncBoost(dt)
        gameController.syncPowers(dt)
    }

    override fun render(renderer: Renderer, alpha: Float) {
        renderer.clear(Color.Yellow)

        renderer.drawBackground(
            sprite = SpritesMain.BACKGROUND,
            state = TransformState(
                position = viewportSize(),
                pivot = Pivot.BottomRight
            ),
            contentScale = ContentScale.FillBounds
        )

        if (hasPortal) {
            renderer.drawSprite(
                spriteId = spritePortal.spriteId,
                frame = spritePortal.currentFrame,
                state = spritePortal.state.copy(position = Vec2(0f, 360f))
            )

            renderer.drawSprite(
                spriteId = spritePortal.spriteId,
                frame = spritePortal.currentFrame,
                state = spritePortal.state.copy(position = Vec2(viewportSize().x, 360f))
            )
        }
        super.render(renderer, alpha)
    }
}