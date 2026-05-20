# GameEngine Multiplatform Migration Guide

## Overview

El GameEngine ha sido refactorizado para soportar múltiples plataformas. Aunque el módulo `engine` aún contiene algunas dependencias de Android/Compose en el código existente, se han creado tipos agnósticos de plataforma que permiten la expansión futura a otras plataformas.

## Cambios principales

### 1. Tipos gráficos agnósticos (`engine/src/main/java/com/mc/engine/graphics/`)

Se han creado tipos agnósticos que no dependen de Android/Compose:

- **GpuColor** - Reemplaza `androidx.compose.ui.graphics.Color`
  - Rango [0.0, 1.0] para compatibilidad universal
  - Conversión a/desde ARGB
  - Conversión a/desde Compose mediante adaptadores

- **GpuSize** - Reemplaza `androidx.compose.ui.geometry.Size`
  - Dimensiones flotantes
  - Soporta unspecified

- **GpuOffset** - Reemplaza `androidx.compose.ui.geometry.Offset`
  - Desplazamiento 2D

- **GpuRect** - Reemplaza `androidx.compose.ui.geometry.Rect`
  - Rectángulos con métodos útiles

**Ubicación**: `engine/src/main/java/com/mc/engine/graphics/`

**Uso**: Para nuevos código, usa estos tipos en lugar de tipos de Compose.

```kotlin
import com.mc.engine.graphics.GpuColor
import com.mc.engine.graphics.GpuSize

val color = GpuColor.White
val size = GpuSize(100f, 50f)
```

**Adaptadores Compose**: En `engine/src/main/java/com/mc/engine/graphics/compose/ComposeAdapters.kt`

```kotlin
val gpuColor = GpuColor.Red
val composeColor = gpuColor.toCompose()
```

### 2. Interfaces agnósticas para sistemas de plataforma

#### AudioSystem (`engine/src/main/java/com/mc/engine/audio/AudioSystem.kt`)

Interfaz agnóstica que reemplaza el acoplamiento directo a Android:

```kotlin
interface AudioSystem : AudioPlayer {
    fun loadSound(id: AudioId, resourceId: Any)
    fun loadMusic(id: AudioId, resourceId: Any)
}
```

**Implementación Android**: `AudioManager` ahora implementa `AudioSystem`

**Uso en Compose**:
```kotlin
@Composable
fun MyGame() {
    val audioSystem = rememberAudioSystem()
    audioSystem.playSound(AudioId("jump"))
}
```

**Uso agnóstico**:
```kotlin
// Tu código específico de plataforma puede implementar AudioSystem
class DesktopAudioSystem : AudioSystem {
    override fun loadSound(id: AudioId, resourceId: Any) { ... }
    override fun playSound(id: AudioId, volume: Float, rate: Float, loop: Boolean) { ... }
}
```

#### SensorSystem (`engine/src/main/java/com/mc/engine/input/sensor/SensorSystem.kt`)

Interfaz agnóstica para entrada de sensores:

```kotlin
interface SensorSystem {
    fun start()
    fun stop()
    fun getAcceleration(): Vec2
    fun getRotation(): Vec2
}
```

**Implementación Android**: `SensorInputAdapter` ahora implementa `SensorSystem`

**Uso**:
```kotlin
@Composable
fun MyGame() {
    val sensorSystem = rememberSensorSystem(processor)
    sensorSystem.start()
}
```

### 3. Estilos de render agnósticos (`engine/src/main/java/com/mc/engine/render/`)

Se han creado tipos agnósticos para render:

- **RenderStyle** - Reemplaza `DrawStyle` de Compose
- **RenderBlendMode** - Reemplaza `BlendMode` de Compose
- **ContentScaleMode** - Reemplaza `ContentScale` de Compose
- **TextOverflow** - Reemplaza `TextOverflow` de Compose
- **TextRenderStyle** - Reemplaza `TextStyle` de Compose

**Ubicación**: `engine/src/main/java/com/mc/engine/render/RendererBase.kt`

**Adaptadores**: `engine/src/main/java/com/mc/engine/render/RenderAdapters.kt`

### 4. Vec2 refactorizado

`Vec2.kt` ha sido actualizado para remover dependencias de Compose:

- Removidos imports de `androidx.compose.ui.geometry`
- Agregadas conversiones a tipos agnósticos:
  - `Vec2.toGpuSize()` → `GpuSize`
  - `Vec2.toGpuOffset()` → `GpuOffset`
  - `GpuSize.toVec2()` → `Vec2`
  - `GpuOffset.toVec2()` → `Vec2`

### 5. Remember.kt actualizado

Se han agregado nuevas funciones Compose para trabajar con interfaces agnósticas:

- **rememberAudioSystem()** - Crea AudioSystem agnóstica
- **rememberSensorSystem()** - Crea SensorSystem agnóstica

Las funciones antiguas siguen disponibles pero están deprecadas:
- `rememberAudioManager()` - Deprecada, usar `rememberAudioSystem()`

## Próximos pasos para multiplatforma

### Para expandir a otras plataformas:

1. **Crear módulo específico de plataforma**
   ```
   engine-desktop/
   engine-ios/
   engine-web/
   ```

2. **Implementar interfaces agnósticas**
   ```kotlin
   class DesktopAudioSystem : AudioSystem { ... }
   class DesktopSensorSystem : SensorSystem { ... }
   class DesktopRenderer : RendererBase { ... }
   ```

3. **Usar tipos agnósticos**
   - Usar `GpuColor` en lugar de `Color`
   - Usar `GpuSize` en lugar de `Size`
   - Usar `RenderStyle` en lugar de `DrawStyle`

4. **Envolver tipos específicos cuando sea necesario**
   - Usar adaptadores (similar a `ComposeAdapters.kt`)
   - Mantener conversiones bidireccionales

### Arquitectura recomendada

```
engine (módulo agnóstico)
├── audio/
│   ├── AudioSystem.kt (interface)
│   └── AudioListener.kt
├── graphics/
│   ├── Color.kt (GpuColor)
│   ├── Size.kt (GpuSize)
│   └── compose/
│       └── ComposeAdapters.kt
├── input/
│   └── sensor/
│       ├── SensorSystem.kt (interface)
│       └── SensorProcessor.kt
└── render/
    ├── RendererBase.kt (interface)
    ├── RenderAdapters.kt
    └── Renderer.kt (aún con tipos Compose)

engine-compose (módulo Android + Compose)
├── src/main/
│   ├── androidMain/
│   │   ├── audio/AudioManager.kt (implementa AudioSystem)
│   │   ├── input/SensorInputAdapter.kt (implementa SensorSystem)
│   │   └── adapters/ComposeSystemAdapters.kt
│   └── compose/
│       ├── Remember.kt (funciones @Composable)
│       └── RendererImpl.kt

engine-desktop (módulo Desktop/JVM)
├── audio/DesktopAudioSystem.kt
├── input/DesktopSensorSystem.kt
└── render/DesktopRenderer.kt
```

## Breaking Changes

Ninguno en términos de compilación. Las interfaces antiguas siguen disponibles:
- `AudioManager` sigue funcionando igual en Android
- `SensorInputAdapter` sigue disponible

Se recomienda migrar gradualmente a las nuevas interfaces agnósticas.

## Depuración de problemas

### Falta de tipos Gpu*

Si obtienes error de "cannot find symbol GpuColor", verifica:
```kotlin
import com.mc.engine.graphics.GpuColor
```

### Conversiones Compose

Para convertir en ambas direcciones:
```kotlin
import com.mc.engine.graphics.compose.toCompose
import com.mc.engine.graphics.compose.toGpu

val gpuColor = GpuColor.Red
val composeColor = gpuColor.toCompose()
val backToGpu = composeColor.toGpu()
```

### Renderer aún depende de Compose

Renderer.kt todavía usa tipos de Compose (`Color`, `Size`, `DrawStyle`, etc.).
Para resolver en futuro:
1. Crear `RendererBase` agnóstica ✓ (ya hecho)
2. Hacer que Renderer.kt implemente ambas interfaces
3. Crear adaptadores específicos de plataforma

Alternativa: Refactorizar Renderer.kt a usar tipos agnósticos y crear `RendererComposeImpl` como implementación.

