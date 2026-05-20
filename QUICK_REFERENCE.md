# 🎯 GameEngine - Tarjeta de Referencia Rápida

## 🚀 Comandos Principales

### Compilar
```bash
cd GameEngine
./gradlew :engine-core:assemble       # Compilar todo
./gradlew :engine-core:build          # Compilar y test
```

### Publicar
```bash
./gradlew :engine-core:publishToMavenLocal    # Publicar localmente
```

### Limpiar
```bash
./gradlew clean                       # Limpiar todo
./gradlew :engine-core:clean          # Limpiar solo core
```

### Debug
```bash
./gradlew :engine-core:assemble --info     # Con info
./gradlew :engine-core:assemble --debug    # Con debug
```

---

## 📚 Documentación

| Iniciar | Aprender | Integrar | Referencia |
|---------|----------|----------|-----------|
| [START_HERE.md](START_HERE.md) | [QUICK_START.md](QUICK_START.md) | [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md) | [ENGINE_PUBLIC_API.md](ENGINE_PUBLIC_API.md) |

---

## 💻 Estructura Multiplatform

```
engine-core/src/
├── commonMain/          # Código compartido (todas las plataformas)
├── androidMain/         # Android específico
└── desktopMain/         # Desktop específico
```

---

## 🔑 Clases Principales

```kotlin
// Interfaz base
interface GameSceneInterface {
    fun onInit(context: SceneDependencies)
    fun onUpdate(gameTime: GameTime)
    fun onRender(renderer: RendererInterface)
    fun onResize(width: Int, height: Int)
    fun onDispose()
}

// Contexto
class SceneDependencies {
    val physics: PhysicsManager
    val renderer: RendererInterface
    val audioSystem: AudioSystem
    // ... más propiedades
}

// Entidades
class Instance {
    var x: Float
    var y: Float
    var sprite: Sprite?
    var rigidBody: RigidBody?
}

// Tiempo
class GameTime {
    val deltaTime: Float
    val elapsed: Float
}
```

---

## 📦 Dependencias en Tu Proyecto

### Opción 1: Módulo Local
```gradle
// settings.gradle.kts
include(":engine-core")
project(":engine-core").projectDir = file("../GameEngine/engine-core")

// build.gradle.kts
dependencies {
    implementation(project(":engine-core"))
}
```

### Opción 2: Maven Local
```gradle
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
}

// build.gradle.kts
dependencies {
    implementation("com.mc.engine:gameengine-core:1.0.0-alpha")
}
```

---

## 🎮 Ejemplo Mínimo

```kotlin
import com.mc.engine.core.GameSceneInterface
import com.mc.engine.core.SceneDependencies
import com.mc.engine.time.GameTime
import com.mc.engine.render.RendererInterface

class MinimalGame : GameSceneInterface {
    override fun onInit(context: SceneDependencies) {}
    override fun onUpdate(gameTime: GameTime) {}
    override fun onRender(renderer: RendererInterface) {
        renderer.clear(0xFF000000.toInt())  // Negro
    }
    override fun onResize(width: Int, height: Int) {}
    override fun onDispose() {}
}

// Uso en Compose
@Composable
fun MyGameScreen() {
    GameSceneView(
        gameScene = remember { MinimalGame() },
        modifier = Modifier.fillMaxSize()
    )
}
```

---

## 🔧 Propiedades del Build

| Propiedad | Valor |
|-----------|-------|
| Version | 1.0.0-alpha |
| Group | com.mc.engine |
| Artifact | gameengine-core |
| Kotlin | 2.0.21+ |
| Android | minSdk 21, compileSdk 35 |
| Java | 11+ |
| Gradle | 8.0+ |

---

## 📱 Plataformas Soportadas

- ✅ Android (minSdk 21)
- ✅ Desktop JVM (Java 11+)
- 🔄 iOS (extensible)
- 🔄 Web (extensible)

---

## ⚙️ Configuración Gradle

### build.gradle.kts (engine-core)
```kotlin
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
}

kotlin {
    androidTarget()
    jvm("desktop")
    
    sourceSets {
        commonMain { ... }
        androidMain { ... }
        desktopMain { ... }
    }
}
```

---

## 📊 Verificación Rápida

```bash
# ¿Compila?
./gradlew :engine-core:assemble
# Esperado: BUILD SUCCESSFUL

# ¿Se publica?
./gradlew :engine-core:publishToMavenLocal
# Esperado: BUILD SUCCESSFUL

# ¿Está en Maven Local?
ls ~/.m2/repository/com/mc/engine/gameengine-core/1.0.0-alpha/
# Debería mostrar archivos .jar, .aar, .pom
```

---

## 🆘 Troubleshooting Rápido

| Problema | Solución |
|----------|----------|
| "Cannot resolve symbol" | `./gradlew sync` y verifica `build.gradle.kts` |
| "No matching variant" | Verifica targets en `build.gradle.kts` |
| "Build failed" | `./gradlew clean` luego `./gradlew build` |
| "Maven Local no funciona" | Ejecuta `publishToMavenLocal` primero |

---

## 📞 Documentación Completa

- [INDEX.md](INDEX.md) - Centro de documentación
- [START_HERE.md](START_HERE.md) - Inicio rápido
- [QUICK_START.md](QUICK_START.md) - 5 minutos
- [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md) - Guía completa
- [ENGINE_PUBLIC_API.md](ENGINE_PUBLIC_API.md) - API completa
- [LIBRARY_SETUP.md](LIBRARY_SETUP.md) - Detalles técnicos
- [VERIFICATION_GUIDE.md](VERIFICATION_GUIDE.md) - Verificación
- [COMPLETION_SUMMARY.md](COMPLETION_SUMMARY.md) - Resumen

---

## ✨ Una Línea de Código

```kotlin
@Composable
fun GameScreen() = GameSceneView(gameScene = remember { MiJuego() })
```

¡**Eso crea tu pantalla de juego!**

---

## 📌 Notas Importantes

- La librería está **100% multiplatform**
- El código agnóstico está en `commonMain`
- Las implementaciones específicas en `androidMain` y `desktopMain`
- **Puedes usarla ahora mismo** en tus proyectos
- Es **extensible a iOS y Web** en el futuro

---

**Versión**: 1.0.0-alpha  
**Estado**: ✅ Producción  
**Última actualización**: 2026-05-19

