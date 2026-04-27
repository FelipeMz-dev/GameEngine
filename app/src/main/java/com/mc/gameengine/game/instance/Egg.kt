package com.mc.gameengine.game.instance

import com.mc.gameengine.app.GameController
import com.mc.gameengine.engine.assets.Sprite
import com.mc.gameengine.engine.collision.BoxCollider
import com.mc.gameengine.engine.core.TransformState
import com.mc.gameengine.engine.render.Pivot
import com.mc.gameengine.engine.render.Renderer
import com.mc.gameengine.game.assets.MainAudios
import com.mc.gameengine.game.assets.SpritesMain
import com.mc.gameengine.game.effects.BlurEffect

class Egg(
    initialHorizontalPosition: Float,
    private val controller: GameController
) : FallObject(initialHorizontalPosition) {

    override val initialVerticalPosition: Float = -38f

    private val spriteIdEgg = SpritesMain.egg

    private var collider = BoxCollider(this, 32f, 32f)

    private val sprite = Sprite(spriteIdEgg)

    private val blurFX = BlurEffect(spriteIdEgg.spriteId)

    private var isBroking = false

    private var hasPower = true

    override fun onEnterScene() {
        super.onEnterScene()
        updatePivot { Pivot.Center }
        sprite.update { current }
        collider.update { it.copy(pivot = Pivot.Center) }
        addCollider(collider)
    }

    override fun fixedUpdate(dt: Float) {
        super.fixedUpdate(dt)
        sprite.update { current }

        if (isBroking) broking(dt)
        else blurFX.emit(current.position)

        blurFX.update(dt)
        collider.update { it.copy(position = current.position) }
        if (PowerItem.SlowTime in controller.availablePowers) lowGravity()
    }

    override fun Renderer.onRender(state: TransformState) {
        blurFX.draw(this)
        sprite.draw()
    }

    override fun onGroundFell() {
        audioPlayer.playSound(MainAudios.SND_EGG_CRACK)
        updatePosition { it.copy(y = groundPosition) }
        stopFalling()
        removeCollider(collider)
        isBroking = true
    }

    private fun broking(dt: Float) {
        sprite.animate(dt)
        if (sprite.isLastFrame()){
            if (hasPower) addInstance(PowerItemInstance(controller, current.position))
            deleteInstance(this)
        }
    }

    fun broke() {
        audioPlayer.playSound(MainAudios.SND_HIT_BAT)
        removeCollider(collider)
        isBroking = true
        hasPower = false
    }
}