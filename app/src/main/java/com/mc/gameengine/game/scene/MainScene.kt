package com.mc.gameengine.game.scene

import com.mc.gameengine.engine.core.GameScene
import com.mc.gameengine.engine.render.Renderer
import com.mc.gameengine.game.assets.SpritesMain
import com.mc.gameengine.game.instance.Dinosaur
import com.mc.gameengine.game.instance.Meteor
import kotlin.random.Random

class MainScene: GameScene() {

    private var randomPosition = 0
    private var spawnDuration = 0
    private var spawnTimer = 0f

    init {
        spawnDuration = Random.nextInt(2, 10)
        addInstance(Dinosaur())
    }

    override fun update(dt: Float) {
        super.update(dt)
        spawnTimer += dt
        if (spawnTimer > spawnDuration) {
            spawnDuration = Random.nextInt(2, 10)
            spawnTimer = 0f
            randomPosition = Random.nextInt(1, viewportSize().x.toInt())
            addInstance(Meteor(randomPosition.toFloat()))
        }
    }

    override fun render(renderer: Renderer, alpha: Float) {
        renderer.drawBackground(SpritesMain.BACKGROUND)
        renderer.drawForeground(SpritesMain.FOREGROUND)
        super.render(renderer, alpha)
    }
}