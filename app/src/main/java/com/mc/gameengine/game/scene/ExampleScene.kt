package com.mc.gameengine.game.scene

import com.mc.gameengine.engine.core.GameScene
import com.mc.gameengine.engine.assets.AssetsManager
import com.mc.gameengine.engine.input.KeyboardManager
import com.mc.gameengine.engine.input.SensorManager
import com.mc.gameengine.engine.input.TouchManager
import com.mc.gameengine.game.instance.MovingSphere

class ExampleScene : GameScene() {

    init {
        addInstance(MovingSphere())
    }
}