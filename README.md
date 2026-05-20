# GameEngine - Motor de Juegos Multiplataforma

Un motor de juegos agnóstico de plataforma construido con **Kotlin Multiplatform**, diseñado para ser una librería reutilizable en proyectos de **Compose Multiplaform**.

## Características

✅ **Multiplataforma**: Android, Desktop (JVM), con soporte para iOS y Web en desarrollo  
✅ **Agnóstico de plataforma**: Lógica común aislada en sourcesets agnósticos  
✅ **Compose Multiplatform**: Integración nativa con Compose para renderizado  
✅ **Física 2D**: Utilizando librería dyn4j  
✅ **Sistema de entrada**: Soporte para toque, ratón, sensores y teclado  
✅ **Audio**: Sistema de reproducción de sonido multiplataforma  
✅ **Gestión de sprites**: Sistema de sprites con animación  
✅ **Entidades**: Sistema de entidades (Instances) con transformación y física  

---

## Estructura del Proyecto

```
GameEngine/
├── engine-core/                  # Librería multiplatform (principal)
│   ├── src/commonMain/           # Código agnóstico (todas las plataformas)
│   ├── src/androidMain/          # Implementaciones específicas Android
│   ├── src/desktopMain/          # Implementaciones específicas Desktop
│   └── build.gradle.kts          # Configuración multiplatform
│
├── engine-android/               # Módulo Android consumidor
│   └── src/main/                 # Componentes específicos de Android (Compose, UI)
│
├── engine-desktop/               # Módulo Desktop consumidor
│   └── src/main/                 # Componentes específicos de Desktop
│
├── app/                          # Aplicación de prueba Android
└── gradle/libs.versions.toml     # Catálogo de dependencias
```

---

## Instalación

### Como Dependencia Local

En tu `settings.gradle.kts`:

```kotlin
include(":engine-core")
```

En tu `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":engine-core"))
}
```

### Como Dependencia Remota (próximamente)

```kotlin
dependencies {
    implementation("com.mc.engine:gameengine-core:1.0.0-alpha")
}
```

---

## Uso Rápido

### Android con Compose

```kotlin
@Composable
fun GameScreen() {
    val gameScene = remember { MiEscena() }
    
    GameSceneView(
        gameScene = gameScene,
        modifier = Modifier.fillMaxSize()
    )
}

class MiEscena : GameSceneInterface {
    private lateinit var renderer: RendererInterface
    private lateinit var physics: PhysicsManager
    
    override fun onInit(context: SceneDependencies) {
        renderer = context.renderer
        physics = context.physics
        // Inicializar lógica
    }
    
    override fun onUpdate(gameTime: GameTime) {
        physics.update(gameTime.deltaTime)
    }
    
    override fun onRender(renderer: RendererInterface) {
        renderer.clear(Color.BLACK.rgb)
        // Renderizar entidades
    }
}
```

### Desktop JVM

```kotlin
fun main() {
    val scene = MiEscena()
    val window = GameWindow(scene, width = 1280, height = 720)
    window.show()
}
```

---

## Módulos y Sourcesets

### `commonMain/`
Código compartido entre plataformas:
- Interfaces `RendererInterface`, `GameSceneInterface`
- Sistema de física (física.*)
- Gestión de entrada (input.*)
- Gestión de sprites (assets.*)
- Audio sistema (audio.*)
- Matemáticas (math.*)
- Tipos comunes (graphics.*, core.*)

### `androidMain/`
Implementaciones específicas de Android:
- `RendererImpl`: Renderizador usando Compose Canvas
- `ComposeSystemAdapters`: Adaptadores de Compose
- `SensorInputAdapter`: Entrada de sensores de dispositivo
- `ImageLoaderImpl`: Carga de imágenes desde recursos Android

### `desktopMain/`
Implementaciones específicas de Desktop (JVM):
- Stubs de Compose (para compilación cruzada)
- Soporte para ratón y teclado
- Renderizado AWT (en desarrollo)

---

## API Pública

Consulta [ENGINE_PUBLIC_API.md](ENGINE_PUBLIC_API.md) para documentación completa de:

- **Clases principales**: `Instance`, `GameTime`, `RigidBody`, `Sprite`
- **Interfaces**: `GameSceneInterface`, `RendererInterface`, `TouchListener`
- **Sistemas**: `PhysicsManager`, `AudioSystem`, `SpriteManager`
- **Eventos**: `TouchEvent`, `MouseEvent`, `CollisionEvent`

---

## Requisitos

- **Kotlin**: 2.0.21+
- **Gradle**: 8.0+
- **Android**: AGP 8.12.3+, minSdk 21
- **Java/Desktop**: JDK 11+

---

## Compilación

```bash
# Compilar engine-core (multiplatform)
./gradlew :engine-core:assemble

# Compilar android
./gradlew :engine-android:assemble

# Compilar desktop
./gradlew :engine-desktop:assemble

# Ejecutar pruebas
./gradlew test

# Construir aplicación de prueba
./gradlew :app:build
```

---

## Desarrollo

### Agregar Nueva Funcionalidad

1. **Si es agnóstica**: Agregar en `engine-core/src/commonMain/`
2. **Si es Android-específica**: Agregar en `engine-core/src/androidMain/` o `engine-android/src/main/`
3. **Si es Desktop-específica**: Agregar en `engine-core/src/desktopMain/` o `engine-desktop/src/main/`

### Naming Conventions

- Interfaces agnósticas: `FunctionalityInterface` (ej: `RendererInterface`)
- Implementaciones: `FunctionalityImpl` (ej: `RendererImpl`)
- Listeners: `FunctionalityListener` (ej: `TouchListener`)
- Eventos: `FunctionalityEvent` (ej: `TouchEvent`)
- Managers: `FunctionalityManager` (ej: `PhysicsManager`)

---

## Migración Multiplatform

Este proyecto fue refactorizado de un proyecto monolítico a una arquitectura Kotlin Multiplatform.

Consulta [MULTIPLATFORM_MIGRATION_GUIDE.md](MULTIPLATFORM_MIGRATION_GUIDE.md) para detalles de la migración.

---

## Roadmap

- [ ] Implementación completa de Desktop (renderizado AWT/Swing)
- [ ] Soporte iOS (Kotlin Native)
- [ ] Soporte Web (Kotlin JS)
- [ ] Sistema de partículas avanzado
- [ ] Editor de escenas visual
- [ ] Exportación a Maven Central

---

## Contribuir

Las contribuciones son bienvenidas. Por favor:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

---

## Licencia

[Especificar licencia aquí]

---

## Contacto

[Información de contacto aquí]
