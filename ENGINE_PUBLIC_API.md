# GameEngine - API Pública

## Descripción General

GameEngine es una librería de juegos multiplataforma para Kotlin que proporciona un motor de juego agnóstico de plataforma con soporte para física (dyn4j), entrada de usuario, audio y renderizado por Compose.

### Plataformas Soportadas
- **Android** (minSdk 21)
- **Desktop (JVM)** (Java 11+)
- **iOS** (en desarrollo)
- **Web** (en desarrollo)

---

## Clases Principales

### Core Engine

#### `GameSceneInterface` (commonMain/core)
Interfaz para implementar escenas de juego. Define el ciclo de vida y la estructura base.

```kotlin
interface GameSceneInterface {
    fun onInit(context: SceneDependencies)
    fun onResize(width: Int, height: Int)
    fun onUpdate(gameTime: GameTime)
    fun onRender(renderer: RendererInterface)
    fun onDispose()
}
```

#### `Instance` (commonMain/core)
Entidad del juego con transformación, física y renderizado.

```kotlin
class Instance {
    var x: Float
    var y: Float
    var rotation: Float
    var scaleX: Float
    var scaleY: Float
    var sprite: Sprite?
    var rigidBody: RigidBody?
}
```

#### `GameTime` (commonMain/time)
Información temporal del motor de juego.

```kotlin
class GameTime {
    val deltaTime: Float      // Tiempo entre frames
    val elapsed: Float        // Tiempo total transcurrido
}
```

### Renderizado

#### `RendererInterface` (commonMain/render)
Interfaz para implementar renderizadores personalizados.

```kotlin
interface RendererInterface {
    fun drawImage(gpuImage: GpuImage, x: Float, y: Float, rotation: Float, scaleX: Float, scaleY: Float)
    fun drawRect(x: Float, y: Float, width: Float, height: Float, color: Int)
    fun clear(color: Int)
    fun setVirtualResolution(width: Int, height: Int)
}
```

#### `GpuImage` (commonMain/graphics)
Representación de una imagen en GPU.

```kotlin
class GpuImage(
    val width: Int,
    val height: Int
)
```

### Física

#### `PhysicsManager` (commonMain/physics)
Gestor de física basado en dyn4j.

```kotlin
class PhysicsManager(
    gravity: Vec2 = Vec2(0f, -9.81f),
    scale: Float = 1f
)
```

#### `RigidBody` (commonMain/physics)
Cuerpo rígido para colisiones y dinámicas.

```kotlin
class RigidBody(config: RigidBodyConfig) {
    var linearVelocity: Vec2
    var angularVelocity: Float
    var mass: Float
}
```

### Entrada de Usuario

#### `TouchListener` (commonMain/input/touch)
Escucha eventos de toque.

```kotlin
interface TouchListener {
    fun onTouchDown(event: TouchEvent)
    fun onTouchMove(event: TouchEvent)
    fun onTouchUp(event: TouchEvent)
}
```

#### `TouchEvent` (commonMain/input/touch)
Evento de toque con posición y ID.

```kotlin
data class TouchEvent(
    val x: Float,
    val y: Float,
    val pointerId: Int
)
```

#### `MouseListener` (commonMain/input/mouse)
Escucha eventos de ratón (Desktop).

```kotlin
interface MouseListener {
    fun onMouseDown(event: MouseEvent)
    fun onMouseMove(event: MouseEvent)
    fun onMouseUp(event: MouseEvent)
}
```

### Audio

#### `AudioSystem` (commonMain/audio)
Gestor de audio.

```kotlin
class AudioSystem {
    fun playSound(audioPlayer: AudioPlayer)
    fun stopSound(audioPlayer: AudioPlayer)
}
```

#### `AudioPlayer` (commonMain/audio)
Reproductor de audio.

```kotlin
class AudioPlayer(
    val source: String,
    val volume: Float = 1f,
    val loop: Boolean = false
)
```

### Sprites y Gráficos

#### `Sprite` (commonMain/assets)
Definición de sprite con animación.

```kotlin
class Sprite(
    val source: SpriteSource,
    val frames: List<SpriteDefinition>
)
```

#### `SpriteManager` (commonMain/assets)
Gestor centralizado de sprites.

```kotlin
class SpriteManager {
    fun loadSprite(key: String): Sprite?
    fun registerSprite(key: String, sprite: Sprite)
}
```

---

## Uso Típico

### En Android (Compose)

```kotlin
@Composable
fun GameScreen() {
    val gameScene = remember { MyGameScene() }
    
    GameSceneView(
        gameScene = gameScene,
        modifier = Modifier.fillMaxSize()
    )
}

class MyGameScene : GameSceneInterface {
    override fun onInit(context: SceneDependencies) {
        // Inicializar la escena
    }
    
    override fun onUpdate(gameTime: GameTime) {
        // Lógica de actualización
    }
    
    override fun onRender(renderer: RendererInterface) {
        // Renderizar
    }
}
```

### En Desktop (JVM)

```kotlin
fun main() {
    val gameScene = MyGameScene()
    val window = GameWindow(gameScene)
    window.show()
}
```

---

## Integración en Otros Proyectos

### Gradle (build.gradle.kts)

```kotlin
dependencies {
    implementation("com.mc.engine:gameengine-core:1.0.0-alpha")
}
```

O incluir como módulo local:

```kotlin
include(":engine-core")

dependencies {
    implementation(project(":engine-core"))
}
```

---

## Excepciones

- `IllegalStateException`: Cuando se intenta usar componentes no inicializados
- `AssetNotFoundException`: Cuando falta un recurso (sprite, imagen, sonido)
- `PhysicsException`: Cuando ocurre un error en cálculos de física

---

## Extensibilidad

### Crear Renderer Personalizado

```kotlin
class CustomRenderer : RendererInterface {
    override fun drawImage(...) { /* implementar */ }
    override fun drawRect(...) { /* implementar */ }
}
```

### Crear Listener de Entrada Personalizado

```kotlin
class CustomInputListener : TouchListener {
    override fun onTouchDown(event: TouchEvent) { /* manejar */ }
    override fun onTouchMove(event: TouchEvent) { /* manejar */ }
    override fun onTouchUp(event: TouchEvent) { /* manejar */ }
}
```

---

## Changelog

### v1.0.0-alpha
- Estructura multiplatform (Android + Desktop)
- Interfaces agnósticas de plataforma
- Física (dyn4j)
- Sistema de entrada táctil y ratón
- Renderizado por Compose (Android) y AWT (Desktop)
- Sistema de audio básico

