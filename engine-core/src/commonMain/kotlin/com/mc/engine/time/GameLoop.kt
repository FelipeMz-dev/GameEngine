package com.mc.engine.time

import com.mc.engine.core.GameSceneInterface

class GameLoop(private val scene: GameSceneInterface) {

    private val time = GameTime()

    fun onFrame(frameTimeNanos: Long) {
        time.fixedUpdate(frameTimeNanos)
        while (time.accumulator >= TimeConfig.FIXED_DELTA_60) {
            scene.fixedUpdate(TimeConfig.FIXED_DELTA_60)
            time.accumulator -= TimeConfig.FIXED_DELTA_60
        }
        scene.update(time.deltaTime)
    }

    fun alpha(): Float = time.alpha
}