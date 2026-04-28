package com.mc.gameengine.game.scene

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.mc.gameengine.engine.core.GameScene
import com.mc.gameengine.engine.math.Vec2
import com.mc.gameengine.engine.math.div
import com.mc.gameengine.engine.physics.PhysicsSimulationMode
import com.mc.gameengine.engine.render.Renderer
import com.mc.gameengine.game.instance.PhysicsShowcaseEntity

class PhysicsShowcaseScene : GameScene() {

    init {
        configurePhysicsWorld {
            it.copy(
                gravity = Vec2(0f, 920f),
                maxLinearSpeed = 1300f
            )
        }

        addInstance(
            PhysicsShowcaseEntity(
                label = "Dynamic + gravity",
                color = Color(0xFFEF5350),
                initialPosition = Vec2(180f, 120f),
                radius = 28f,
                physicsMode = PhysicsSimulationMode.Dynamic,
                gravityScale = 1f,
                linearDamping = 0.02f,
                mass = 1f,
                maxSpeed = 1200f,
                behavior = PhysicsShowcaseEntity.Behavior.FreeFall
            )
        )

        addInstance(
            PhysicsShowcaseEntity(
                label = "ForceMode.Force",
                color = Color(0xFFFF9800),
                initialPosition = Vec2(360f, 180f),
                radius = 26f,
                physicsMode = PhysicsSimulationMode.Dynamic,
                gravityScale = 0.6f,
                linearDamping = 0.06f,
                mass = 2f,
                maxSpeed = 1000f,
                behavior = PhysicsShowcaseEntity.Behavior.ConstantForce
            )
        )

        addInstance(
            PhysicsShowcaseEntity(
                label = "ForceMode.Acceleration",
                color = Color(0xFF42A5F5),
                initialPosition = Vec2(560f, 150f),
                radius = 24f,
                physicsMode = PhysicsSimulationMode.Dynamic,
                gravityScale = 0.4f,
                linearDamping = 0.1f,
                mass = 8f,
                maxSpeed = 850f,
                behavior = PhysicsShowcaseEntity.Behavior.ConstantAcceleration
            )
        )

        addInstance(
            PhysicsShowcaseEntity(
                label = "ForceMode.Impulse",
                color = Color(0xFF66BB6A),
                initialPosition = Vec2(220f, 360f),
                radius = 24f,
                physicsMode = PhysicsSimulationMode.Dynamic,
                gravityScale = 1f,
                linearDamping = 0.02f,
                mass = 1.2f,
                maxSpeed = 1200f,
                behavior = PhysicsShowcaseEntity.Behavior.TimedImpulse
            )
        )

        addInstance(
            PhysicsShowcaseEntity(
                label = "VelocityChange",
                color = Color(0xFFAB47BC),
                initialPosition = Vec2(460f, 360f),
                radius = 22f,
                physicsMode = PhysicsSimulationMode.Dynamic,
                gravityScale = 0f,
                linearDamping = 0f,
                mass = 1f,
                maxSpeed = 520f,
                behavior = PhysicsShowcaseEntity.Behavior.VelocityPulse
            )
        )

        addInstance(
            PhysicsShowcaseEntity(
                label = "Kinematic",
                color = Color(0xFF26C6DA),
                initialPosition = Vec2(700f, 330f),
                radius = 20f,
                physicsMode = PhysicsSimulationMode.Kinematic,
                gravityScale = 0f,
                linearDamping = 0f,
                mass = 1f,
                maxSpeed = 999f,
                behavior = PhysicsShowcaseEntity.Behavior.KinematicOrbit
            )
        )

        addInstance(
            PhysicsShowcaseEntity(
                label = "Static",
                color = Color(0xFF78909C),
                initialPosition = Vec2(820f, 500f),
                radius = 18f,
                physicsMode = PhysicsSimulationMode.Static,
                gravityScale = 0f,
                linearDamping = 0f,
                mass = 1f,
                maxSpeed = 999f,
                behavior = PhysicsShowcaseEntity.Behavior.StaticAnchor
            )
        )
    }

    override fun render(renderer: Renderer, alpha: Float) {
        renderer.drawAxis(viewportSize() / 2f)
        renderer.drawText(
            text = "Physics Showcase: modos Dynamic/Kinematic/Static + Force/Acceleration/Impulse/VelocityChange",
            position = Vec2(20f, 24f),
            style = TextStyle(
                color = Color(0xFF111111),
                fontSize = TextUnit(12f, TextUnitType.Sp)
            )
        )
        super.render(renderer, alpha)
    }
}
