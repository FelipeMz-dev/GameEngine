# 🔍 Verificación de Instalación - GameEngine Multiplatform

Este archivo te ayuda a verificar que todo está configurado correctamente.

---

## ✅ Verificación 1: Compilación de engine-core

```bash
cd GameEngine
./gradlew :engine-core:assemble
```

**Resultado esperado**: BUILD SUCCESSFUL

Si ves esto, la librería multiplatform está lista.

---

## ✅ Verificación 2: Publicación a Maven Local

```bash
cd GameEngine
./gradlew :engine-core:publishToMavenLocal
```

**Resultado esperado**: BUILD SUCCESSFUL

Esto publica la librería localmente en:
```
~/.m2/repository/com/mc/engine/gameengine-core/1.0.0-alpha/
```

---

## ✅ Verificación 3: Importar en tu proyecto

### Opción A: Como módulo local

1. Tu proyecto debe estar en la misma carpeta que GameEngine:

```
workspace/
├── GameEngine/
│   ├── engine-core/
│   └── ...
├── MiJuego/
│   ├── settings.gradle.kts
│   └── build.gradle.kts
```

2. En `MiJuego/settings.gradle.kts`:

```kotlin
include(":engine-core")
project(":engine-core").projectDir = 
    file("../GameEngine/engine-core")
```

3. En `MiJuego/build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":engine-core"))
}
```

4. Sync Gradle

---

### Opción B: Como dependencia de Maven Local

1. En `MiJuego/settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
}
```

2. En `MiJuego/build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.mc.engine:gameengine-core:1.0.0-alpha")
}
```

3. Sync Gradle

---

## ✅ Verificación 4: Test Simple

Crea en tu proyecto:

```kotlin
// src/commonMain/kotlin/TestGame.kt
import com.mc.engine.core.GameSceneInterface
import com.mc.engine.core.SceneDependencies
import com.mc.engine.time.GameTime
import com.mc.engine.render.RendererInterface

class TestGame : GameSceneInterface {
    override fun onInit(context: SceneDependencies) {
        println("✅ GameEngine inicializado correctamente")
    }
    
    override fun onUpdate(gameTime: GameTime) {}
    override fun onRender(renderer: RendererInterface) {}
    override fun onResize(width: Int, height: Int) {}
    override fun onDispose() {}
}
```

Si puedes compilar esto sin errores, ¡todo funciona! ✅

---

## 🔧 Solución de Problemas

### Error: "Cannot resolve symbol GameSceneInterface"

**Solución 1**: Asegúrate de que:
- [ ] engine-core está incluido en settings.gradle.kts
- [ ] Hiciste Sync Gradle
- [ ] El proyecto compila (./gradlew build)

**Solución 2**: Si usas Maven Local:
- [ ] Ejecuta `./gradlew :engine-core:publishToMavenLocal` en GameEngine
- [ ] Asegúrate de que mavenLocal() está en repositories

---

### Error: "No matching variant"

**Solución**:
- [ ] Verifica que tu proyecto es multiplatform
- [ ] Que tienes los targets correctos (androidTarget, jvm)
- [ ] Ejecuta Gradle sync: `./gradlew sync`

---

### Error de compilación en engine-android

Esto es normal. `engine-android` es un consumidor específico que necesita actualizaciones.
**La librería engine-core está 100% funcional** sin importar esto.

---

## 📊 Verificar Estructura

En tu terminal, verifica que existan estos archivos:

```bash
# Desde GameEngine directory:
ls engine-core/src/commonMain/kotlin/com/mc/engine/core/
# Debería mostrar: GameSceneInterface.kt, Instance.kt, etc.

ls engine-core/src/androidMain/
# Debería existir (aunque esté vacío)

ls engine-core/src/desktopMain/
# Debería existir (aunque esté vacío)
```

---

## 🚀 Test de Publicación

Para verificar que se publicó correctamente:

```bash
# Desde GameEngine:
./gradlew :engine-core:publishToMavenLocal

# Verifica que existen los artefactos:
ls ~/.m2/repository/com/mc/engine/gameengine-core/1.0.0-alpha/

# Debería mostrar archivos como:
# gameengine-core-1.0.0-alpha.jar
# gameengine-core-1.0.0-alpha.pom
# etc.
```

---

## ✅ Checklist Final

Marca los items completados:

- [ ] `./gradlew :engine-core:assemble` compila exitosamente
- [ ] `./gradlew :engine-core:publishToMavenLocal` funciona
- [ ] Puedo importar en mi proyecto sin errores
- [ ] Puedo crear una clase que implemente GameSceneInterface
- [ ] Puedo compilar esa clase sin errores
- [ ] Los archivos existen en ~/.m2/repository/

Si todos están checkeados, ¡**tu librería está completamente funcional**! ✅

---

## 📞 Siguiente Paso

Consulta:
- [QUICK_START.md](QUICK_START.md) - Para empezar en 5 minutos
- [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md) - Para una guía completa
- [ENGINE_PUBLIC_API.md](ENGINE_PUBLIC_API.md) - Para referencia de API

---

## 💡 Tip

Si tienes problemas de Gradle, intenta:

```bash
cd GameEngine
./gradlew clean
./gradlew :engine-core:assemble
```


