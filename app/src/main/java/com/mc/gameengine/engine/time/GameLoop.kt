package com.mc.gameengine.engine.time

import com.mc.gameengine.core.time.GameTime
import com.mc.gameengine.core.time.TimeConfig
import com.mc.gameengine.engine.core.GameScene

class GameLoop(private val scene: GameScene) {

    private val time = GameTime()

    fun onFrame(frameTimeNanos: Long) {
        time.fixedUpdate(frameTimeNanos)
        while (time.accumulator >= TimeConfig.FIXED_DELTA) {
            scene.fixedUpdate(TimeConfig.FIXED_DELTA)
            time.accumulator -= TimeConfig.FIXED_DELTA
        }
        scene.update(time.deltaTime)
    }

    fun alpha(): Float = time.alpha
}