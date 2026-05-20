# Guía de Integración - GameEngine Multiplatform

Esta guía te ayudará a integrar GameEngine como librería en tu proyecto de Compose Multiplatform.

---

## Requisitos Previos

- Android Studio / IntelliJ IDEA 2023.3+
- Kotlin Plugin 2.0.21+
- Gradle 8.0+
- JDK 11+

---

## Paso 1: Agregar el módulo al proyecto

### Opción A: Incluir como módulo local

En tu raíz `settings.gradle.kts`:

```kotlin
include(":engine-core")

project(":engine-core").projectDir = file("../path/to/GameEngine/engine-core")
// O si está en el mismo workspace:
// include(":engine-core")
```

En tu `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":engine-core"))
}
```

### Opción B: Publicar a Maven Local

En tu terminal:

```bash
cd GameEngine
./gradlew :engine-core:publishToMavenLocal
```

Luego en tu `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}
```

En tu `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.mc.engine:gameengine-core:1.0.0-alpha")
}
```

---

## Paso 2: Estructura de tu proyecto Multiplatform

### Crear un proyecto Compose Multiplatform

Si aún no tienes un proyecto multiplatform, crealo con:

```bash
# Usar Jetbrains Compose Multiplatform template
# O crear manualmente la estructura:

miJuego/
├── composeApp/
│   ├── src/
│   │   ├── commonMain/
│   │   ├── androidMain/
│   │   └── desktopMain/
│   └── build.gradle.kts
├── settings.gradle.kts
└── build.gradle.kts
```

### Configurar build.gradle.kts

```kotlin
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    androidTarget()
    jvm("desktop")

    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(project(":engine-core"))
            }
        }
        
        androidMain {
            dependencies {
                implementation("androidx.appcompat:appcompat:1.7.0")
            }
        }
        
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }
}
```

---

## Paso 3: Crear tu primera escena de juego

### En `commonMain/`

```kotlin
// miJuego/src/commonMain/kotlin/com/example/MiJuego.kt

import com.mc.engine.core.GameSceneInterface
import com.mc.engine.core.SceneDependencies
import com.mc.engine.time.GameTime
import com.mc.engine.render.RendererInterface
import com.mc.engine.graphics.Color

class MiEscenaDeJuego : GameSceneInterface {
    
    private lateinit var renderer: RendererInterface
    private lateinit var physics: PhysicsManager
    
    override fun onInit(context: SceneDependencies) {
        renderer = context.renderer
        physics = context.physics
        
        // Inicializar física
        physics.setGravity(0f, -9.81f)
    }
    
    override fun onResize(width: Int, height: Int) {
        renderer.setVirtualResolution(width, height)
    }
    
    override fun onUpdate(gameTime: GameTime) {
        // Lógica de actualización
        physics.update(gameTime.deltaTime)
    }
    
    override fun onRender(renderer: RendererInterface) {
        // Limpiar pantalla
        renderer.clear(Color.BLACK.rgb)
        
        // Renderizar entidades
        // renderer.drawImage(...)
    }
    
    override fun onDispose() {
        // Limpiar recursos
    }
}
```

---

## Paso 4: Renderizar en Android

### Crear composable de GameScene

```kotlin
// miJuego/src/commonMain/kotlin/com/example/GameApp.kt

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.mc.engine.compose.GameSceneView

@Composable
fun GameApp() {
    val gameScene = remember { MiEscenaDeJuego() }
    
    Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
        GameSceneView(
            gameScene = gameScene,
            modifier = Modifier.fillMaxSize()
        )
    }
}
```

### En Android Main

```kotlin
// miJuego/src/androidMain/kotlin/com/example/MainActivity.kt

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GameApp()
        }
    }
}
```

---

## Paso 5: Renderizar en Desktop

### Crear ventana de juego

```kotlin
// miJuego/src/desktopMain/kotlin/com/example/Main.kt

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.mc.engine.compose.GameSceneView

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Mi Juego"
    ) {
        val gameScene = remember { MiEscenaDeJuego() }
        GameSceneView(gameScene = gameScene)
    }
}
```

---

## Paso 6: Usar componentes del Engine

### Trabajar con Sprites

```kotlin
class MiEscena : GameSceneInterface {
    override fun onInit(context: SceneDependencies) {
        val spriteManager = context.spriteManager
        spriteManager.loadSprite("player") // Cargar sprite
    }
}
```

### Trabajar con Física

```kotlin
val rigidBody = RigidBody(
    RigidBodyConfig(
        type = CollisionBodyType.DYNAMIC,
        density = 1f,
        friction = 0.3f
    )
)

instance.rigidBody = rigidBody
physics.addBody(instance, rigidBody)
```

### Trabajar con Entrada

```kotlin
class MiListener : TouchListener {
    override fun onTouchDown(event: TouchEvent) {
        println("Touch at ${event.x}, ${event.y}")
    }
    
    override fun onTouchMove(event: TouchEvent) {}
    override fun onTouchUp(event: TouchEvent) {}
}

val listener = MiListener()
touchManager.register(listener)
```

### Trabajar con Audio

```kotlin
val audioSystem = AudioSystem()
val audioPlayer = AudioPlayer(
    source = "sfx_jump.wav",
    volume = 0.8f
)
audioSystem.playSound(audioPlayer)
```

---

## Paso 7: Compilar y ejecutar

### Android

```bash
./gradlew :composeApp:installDebug
```

### Desktop

```bash
./gradlew :composeApp:run
```

---

## Solución de Problemas

### Error: "Cannot resolve symbol 'GameSceneInterface'"

**Solución**: Asegúrate de que `engine-core` está incluido en `settings.gradle.kts` y en las dependencias de `build.gradle.kts`.

### Error: "No matching variant of project :engine-core"

**Solución**: Verifica que tu `build.gradle.kts` incluye los targets correctos (androidTarget, jvm).

### Clase no encontrada en runtime

**Solución**: RevISA que las dependencias están en el sourceset correcto:
- Común: `commonMain`
- Android: `androidMain`
- Desktop: `desktopMain`

### APK/ejecutable no se genera

**Solución**: Ejecuta `./gradlew clean` y luego `./gradlew build`.

---

## Estructura recomendada

```
miJuego/
├── composeApp/src
│   ├── commonMain/kotlin/MyGame.kt
│   ├── AndroidManifest.xml
│   ├── androidMain/kotlin/MainActivity.kt
│   └── desktopMain/kotlin/Main.kt
├── settings.gradle.kts
└── build.gradle.kts
```

---

## Próximos pasos

1. Consulta [ENGINE_PUBLIC_API.md](../ENGINE_PUBLIC_API.md) para ver todas las APIs disponibles
2. Revisa ejemplos en el módulo `app/` del proyecto GameEngine
3. Lee [MULTIPLATFORM_MIGRATION_GUIDE.md](../MULTIPLATFORM_MIGRATION_GUIDE.md) para entender la arquitectura

---

## Soporte

Para preguntas o problemas:
- Revisa la documentación en ENGINE_PUBLIC_API.md
- Consulta los ejemplos en engine-android/src/main
- Abre un issue en el repositorio


