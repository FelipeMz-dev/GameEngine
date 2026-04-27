package com.mc.gameengine.engine.time

import com.mc.gameengine.engine.core.GameScene
import org.junit.Assert.assertEquals
import org.junit.Test

class GameLoopTest {

    @Test
    fun `onFrame limits fixed updates per frame to avoid spiral of death`() {
        val scene = TestScene()
        val gameLoop = GameLoop(scene)

        gameLoop.onFrame(1_000_000_000L)
        gameLoop.onFrame(1_250_000_000L)

        assertEquals(TimeConfig.MAX_FIXED_STEPS_PER_FRAME, scene.fixedUpdateCalls)
        assertEquals(1, scene.updateCalls)
    }

    @Test
    fun `onFrame keeps running fixed updates normally under step cap`() {
        val scene = TestScene()
        val gameLoop = GameLoop(scene)

        gameLoop.onFrame(1_000_000_000L)
        gameLoop.onFrame(1_050_000_000L)

        assertEquals(3, scene.fixedUpdateCalls)
        assertEquals(1, scene.updateCalls)
    }

    private class TestScene : GameScene() {
        var fixedUpdateCalls = 0
        var updateCalls = 0

        override fun fixedUpdate(dt: Float) {
            fixedUpdateCalls++
        }

        override fun update(dt: Float) {
            updateCalls++
        }
    }
}
