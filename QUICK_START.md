# 🎮 GameEngine Multiplatform - Inicio Rápido

## ¿Qué es?

GameEngine es una librería multiplatform de juegos construida con Kotlin que permite:
- Crear juegos para Android y Desktop (JVM) desde una base de código
- Usar Compose para las interfaces
- Acceder a física (dyn4j), entrada, audio y gráficos agnósticos de plataforma

## En 5 Minutos

### 1. Agregar a tu proyecto

```kotlin
// settings.gradle.kts
include(":engine-core")

// build.gradle.kts (tu app)
dependencies {
    implementation(project(":engine-core"))
}
```

### 2. Crear tu escena de juego

```kotlin
import com.mc.engine.core.GameSceneInterface
import com.mc.engine.core.SceneDependencies
import com.mc.engine.time.GameTime
import com.mc.engine.render.RendererInterface

class MiJuego : GameSceneInterface {
    override fun onInit(context: SceneDependencies) {
        // Inicializar
    }
    
    override fun onUpdate(gameTime: GameTime) {
        // Lógica de juego
    }
    
    override fun onRender(renderer: RendererInterface) {
        renderer.clear(0xFF000000.toInt())
        // Dibujar
    }
    
    override fun onResize(width: Int, height: Int) {}
    override fun onDispose() {}
}
```

### 3. Mostrar en Android (Compose)

```kotlin
@Composable
fun GameScreen() {
    val scene = remember { MiJuego() }
    GameSceneView(
        gameScene = scene,
        modifier = Modifier.fillMaxSize()
    )
}
```

### 4. Mostrar en Desktop

```kotlin
fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        val scene = remember { MiJuego() }
        GameSceneView(gameScene = scene)
    }
}
```

## Documentación Completa

| Documento | Contenido |
|-----------|-----------|
| [ENGINE_PUBLIC_API.md](ENGINE_PUBLIC_API.md) | API completa con todas las clases |
| [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md) | Guía paso a paso de integración |
| [LIBRARY_SETUP.md](LIBRARY_SETUP.md) | Detalles técnicos de la estructura |
| [README.md](README.md) | Descripción general del proyecto |

## Compilar

```bash
# Compilar librería multiplatform
./gradlew :engine-core:assemble

# Publicar a Maven Local
./gradlew :engine-core:publishToMavenLocal

# Ejecutar app Android
./gradlew :app:build
```

## Características

- ✅ **Agnóstico de plataforma**: Una base de código para múltiples plataformas
- ✅ **Compose Multiplatform**: Renderización nativa con Compose
- ✅ **Física 2D**: Sistema integrado usando dyn4j
- ✅ **Entrada multitoque**: Táctil, ratón, teclado
- ✅ **Audio**: Reproducción de sonidos
- ✅ **Sprites**: Sistema de sprites y animación
- ✅ **Colisiones**: Detección y manejo de colisiones

## Estructura del Código

```
engine-core/src/
├── commonMain/        # Código compartido (física, input, audio)
├── androidMain/       # Android específico (Compose, sensores)
└── desktopMain/       # Desktop específico (stubs, AWT)
```

## Api Principales

### Core
- `GameSceneInterface` - Base para tus escenas
- `Instance` - Entidades del juego
- `GameTime` - Información temporal

### Renderizado
- `RendererInterface` - Interfaz de renderizado
- `Canvas` - Dibujo 2D

### Física
- `PhysicsManager` - Gestor de física
- `RigidBody` - Cuerpo rígido

### Entrada
- `TouchListener` - Escuchar toques
- `MouseListener` - Escuchar ratón
- `KeyboardListener` - Escuchar teclado

### Audio
- `AudioSystem` - Reproducción de audio
- `AudioPlayer` - Reproductor individual

## Ejemplos

Consulta `app/` para ejemplos de uso completo en Android.

## Requisitos

- Kotlin 2.0.21+
- Gradle 8.0+
- Java 11+ / Android minSdk 21

## Próximas Plataformas

- 🔄 **En desarrollo**: iOS (Kotlin Native)
- 🔄 **En desarrollo**: Web (Kotlin JS)

## ¿Necesitas ayuda?

1. Lee [ENGINE_PUBLIC_API.md](ENGINE_PUBLIC_API.md) para la referencia de API
2. Sigue [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md) para paso a paso
3. Revisa ejemplos en el módulo `app/`
4. Consulta [LIBRARY_SETUP.md](LIBRARY_SETUP.md) para detalles técnicos

---

**¡Listo para crear tu primer juego multiplatform! 🎮**

