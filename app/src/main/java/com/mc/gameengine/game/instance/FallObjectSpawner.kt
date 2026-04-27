package com.mc.gameengine.game.instance

import com.mc.gameengine.app.GameController
import com.mc.gameengine.engine.core.Instance
import kotlin.random.Random
import kotlin.random.nextInt

class FallObjectSpawner(
    private val gameController: GameController,
    private val factory: (Float, GameController) -> FallObject,
    private val range: IntRange = 3..6
): Instance() {

    private var spawnDuration = 0
    private var spawnTimer = 0f

    override fun onEnterScene() {
        resetSpawnTimer()
    }

    override fun fixedUpdate(dt: Float) {
        spawnTimer += dt
        if (spawnTimer > spawnDuration) {
            resetSpawnTimer()
            val randomX = Random.nextInt(
                from = 24,
                until = (viewportSize().x.toInt() - 24).coerceAtLeast(11)
            ).toFloat()
            
            addInstance(factory(randomX, gameController))
        }
    }

    fun resetSpawnTimer() {
        spawnDuration = Random.nextInt(range)
        spawnTimer = 0f
    }
}