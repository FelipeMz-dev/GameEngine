package com.mc.gameengine.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.mc.gameengine.R
import com.mc.gameengine.app.ui.theme.GameEngineTheme
import com.mc.gameengine.engine.assets.SpriteManager
import com.mc.gameengine.engine.compose.GameSceneView
import com.mc.gameengine.engine.compose.rememberAudioManager
import com.mc.gameengine.engine.compose.rememberSpriteManager
import com.mc.gameengine.engine.render.VirtualResolution
import com.mc.gameengine.game.assets.MainAudios
import com.mc.gameengine.game.assets.MainAudios.loadMainAudio
import com.mc.gameengine.game.assets.registerMainSprites
import com.mc.gameengine.game.instance.PowerItem
import com.mc.gameengine.game.scene.MainScene

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideBars()
        setContent {
            var isSplash by remember { mutableStateOf(true) }
            GameEngineTheme {
                Scaffold(Modifier.background(Color.White)) {
                    if (isSplash) SplashScreen(Modifier.padding(it)) {
                        isSplash = false
                    } else DinoGameScreen(Modifier.padding(it))
                }
            }
        }
    }

    private fun hideBars() {
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.systemBarsBehavior = WindowInsetsControllerCompat
            .BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
    }
}

@Composable
fun DinoGameScreen(modifier: Modifier) {

    val assetManager = rememberSpriteManager().registerMainSprites()
    val audioManager = rememberAudioManager { MainAudios.also { loadMainAudio() } }
    val gameController = remember { GameController(audioManager) }
    val scene = remember { MainScene(gameController) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        GameSceneView(
            modifier = modifier
                .fillMaxHeight()
                .aspectRatio(VirtualResolution.LandscapeHD.aspectRatio())
                .align(Alignment.Center),
            scene = scene,
            spriteManager = assetManager,
            audioManager = audioManager,
            virtualResolution = VirtualResolution.LandscapeHD,
            contentScale = ContentScale.Fit,
        )

        DataListStatus(gameController)

        ButtonPause(Modifier.align(Alignment.TopEnd), gameController)

        ContentIconPowers(Modifier.align(Alignment.CenterEnd), assetManager, gameController)

        ButtonBoost(Modifier.align(Alignment.BottomStart), true, gameController)

        ButtonBoost(Modifier.align(Alignment.BottomEnd), false, gameController)
    }

    TextPause(gameController)
}

@Composable
fun ContentIconPowers(
    modifier: Modifier,
    spriteManager: SpriteManager,
    controller: GameController
) {
    FlowRow(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        maxItemsInEachRow = 2
    ) {
        controller.availablePowers.forEach {
            IconPower(it, spriteManager) {
                controller.onFinalized()
            }
        }
    }
}

@Composable
private fun IconPower(
    itemPower: PowerItem,
    spriteManager: SpriteManager,
    onFinalized: () -> Unit
){
    val imageSprite = remember(itemPower.type) {
        spriteManager.get(itemPower.type.sprite.spriteId).firstFrame()
    } ?: return

    Image(
        bitmap = imageSprite,
        contentDescription = null,
        modifier = Modifier
            .clip(CircleShape)
            .drawBehind {
                val level = size.height - size.height * itemPower.value / 100
                drawRect(color = Color.White.copy(alpha = 0.5f))
                drawRect(
                    size = size.copy(height = level),
                    color = Color.Black.copy(alpha = 0.8f)
                )
            }
            .padding(6.dp)
            .size(32.dp)
    )

    DisposableEffect(Unit) {
        onDispose {
            onFinalized()
        }
    }
}

@Composable
fun DataListStatus(gameController: GameController) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TextStatus("Score: ${gameController.score}")

        TextStatus("Balls: ${gameController.balls}")

        TextStatus("Holes: ${gameController.holes}")
    }
}

@Composable
fun TextStatus(text: String) {
    Text(
        modifier = Modifier
            .background(
                color = Color.White.copy(alpha = 0.5f),
                shape = CircleShape
            )
            .padding(horizontal = 16.dp),
        text = text,
        color = Color.Black,
        fontWeight = FontWeight.Black
    )
}

@Composable
fun ButtonPause(modifier: Modifier, gameController: GameController) {
    val icon = ImageVector.vectorResource(
        if (gameController.pause) R.drawable.ic_play else R.drawable.ic_pause
    )

    IconButton(
        modifier = modifier.size(64.dp),
        onClick = { gameController.togglePause() },
    ) {
        Icon(
            modifier = Modifier.fillMaxSize(),
            imageVector = icon,
            contentDescription = null,
            tint = Color.White
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextPause(gameController: GameController) {
    if (gameController.pause) {

        val title = when {
            gameController.isDie -> "GAME OVER"
            gameController.isInit -> "PLAY"
            else -> "PAUSE"
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        .padding(16.dp),
                    text = title,
                    color = MaterialTheme.colorScheme.surface,
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center
                )

                if (gameController.isDie) {
                    TextStatus("Your score: ${gameController.lastScore}")

                    TextStatus("Max score: ${gameController.bestScore}")

                    TextStatus("Balls Collected: ${gameController.lastBalls}")
                }

                ButtonPause(Modifier, gameController)
            }
        }
    }
}

@Composable
fun ButtonBoost(
    modifier: Modifier,
    isLeft: Boolean,
    gameController: GameController
) {
    val icon = if (isLeft) Icons.Rounded.KeyboardArrowLeft else Icons.Rounded.KeyboardArrowRight

    Icon(
        modifier = modifier
            .padding(16.dp)
            .size(128.dp)
            .clickable {
                gameController.also {
                    if (isLeft) it.boostLeft() else it.boostRight()
                }
            }
            .drawWithContent {
                val level = if (isLeft) gameController.leftBoost else gameController.rightBoost
                val percent = level / 100f
                val height = size.height - size.height * percent
                drawContent()
                drawRect(
                    color = Color.Black.copy(alpha = 0.8f),
                    topLeft = Offset.Zero,
                    size = size.copy(height = height)
                )
            },
        imageVector = icon,
        contentDescription = null,
        tint = Color.White
    )
}
