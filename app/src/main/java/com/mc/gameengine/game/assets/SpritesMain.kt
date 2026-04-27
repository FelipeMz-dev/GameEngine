package com.mc.gameengine.game.assets

import com.mc.gameengine.R
import com.mc.gameengine.engine.assets.AtlasSpriteDef
import com.mc.gameengine.engine.assets.SingleImageSpriteDef
import com.mc.gameengine.engine.math.Vec2

object SpritesMain {
    const val METEOR = "meteor"
    const val METEOR_EXPLOSION = "meteorExplosion"
    const val BACKGROUND = "backIsland"
    const val SPR_BALL = "spr_ball"
    const val SPR_EGG = "spr_egg"
    const val SPR_MAGNET = "spr_magnet"
    const val SPR_HELMET = "helmet"
    const val SPR_WARNING = "warning"
    const val SPR_MULTIPLIER = "spr_ball_multiplier"
    const val SPR_PORTAL = "spr_portal"
    const val SPR_PORTAL_ICON = "spr_portal_icon"
    const val SPR_CLOCK = "spr_clock"

    val meteor = AtlasSpriteDef(
        spriteId = METEOR,
        resId = R.drawable.sheet_meteor,
        columns = 4,
        rows = 1,
        srcSize = Vec2(50, 110),
        srcSpacing = Vec2(8, 0)
    )

    val meteorExplosion = AtlasSpriteDef(
        spriteId = METEOR_EXPLOSION,
        resId = R.drawable.explosion_meteor,
        columns = 3,
        rows = 3,
        srcSize = Vec2(214, 178)
    )


    val background = SingleImageSpriteDef(
        spriteId = BACKGROUND,
        resId = R.drawable.background_island
    )

    val ball = SingleImageSpriteDef(
        spriteId = SPR_BALL,
        resId = R.drawable.spr_ball
    )

    val egg = AtlasSpriteDef(
        spriteId = SPR_EGG,
        resId = R.drawable.sheet_egg,
        columns = 3,
        rows = 3,
        srcSize = Vec2(64, 64),
    )

    val magnet = SingleImageSpriteDef(
        spriteId = SPR_MAGNET,
        resId = R.drawable.spr_magnet
    )

    val helmet = SingleImageSpriteDef(
        spriteId = SPR_HELMET,
        resId = R.drawable.spr_helment,
    )

    val warning = SingleImageSpriteDef(
        spriteId = SPR_WARNING,
        resId = R.drawable.spr_warning,
    )

    val multiplier = SingleImageSpriteDef(
        spriteId = SPR_MULTIPLIER,
        resId = R.drawable.spr_ball_x2,
    )

    val portal = AtlasSpriteDef(
        spriteId = SPR_PORTAL,
        resId = R.drawable.spr_portal,
        columns = 7,
        rows = 1,
        srcOffset = Vec2(1f, 5f),
        srcSize = Vec2(72, 96),
        srcSpacing = Vec2(1, 0)
    )

    val portalIcon = SingleImageSpriteDef(
        spriteId = SPR_PORTAL_ICON,
        resId = R.drawable.spr_portal_icon,
    )

    val clock = SingleImageSpriteDef(
        spriteId = SPR_CLOCK,
        resId = R.drawable.spr_clock
    )

    val entries = listOf(
        meteor,
        background,
        meteorExplosion,
        ball,
        egg,
        magnet,
        helmet,
        warning,
        multiplier,
        portal,
        portalIcon,
        clock
    )
}

