package com.mc.gameengine.game.scene

import com.mc.gameengine.engine.core.GameScene
import com.mc.gameengine.engine.assets.AssetsManager
import com.mc.gameengine.engine.input.InputManager
import com.mc.gameengine.game.instance.MovingSphere

class ExampleScene(
    assets: AssetsManager,
    input: InputManager
) : GameScene(input, assets) {

    init {
        addInstance(MovingSphere())
    }
}