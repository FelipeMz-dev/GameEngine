package com.mc.gameengine.game.scene

import com.mc.gameengine.engine.core.GameScene
import com.mc.gameengine.game.instance.MovingSphere

class ExampleScene : GameScene() {

    init {
        addInstance(MovingSphere())
    }
}