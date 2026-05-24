plugins {
    id("com.android.library")
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

group = "com.mc.engine"
version = "1.0.0-alpha"

android {
    namespace = "com.mc.engine.android"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    sourceSets {
        getByName("main") {
            java.exclude(
                "com/mc/engine/assets/ImageLoaderImpl.kt",
                "com/mc/engine/compose/DrawScopeExtensions.kt",
                "com/mc/engine/compose/ModifierExtensions.kt",
                "com/mc/engine/compose/Remember.kt",
                "com/mc/engine/compose/RenderCommand.kt",
                "com/mc/engine/compose/RenderDepth.kt",
                "com/mc/engine/compose/RendererImpl.kt",
                "com/mc/engine/compose/SpritePreview.kt",
                "com/mc/engine/compose/adapters/ComposeSystemAdapters.kt",
                "com/mc/engine/graphics/compose/ComposeAdapters.kt",
                "com/mc/engine/graphics/compose/GpuImageAdapters.kt",
                "com/mc/engine/input/keyboard/KeyboardEvent.kt",
                "com/mc/engine/input/keyboard/KeyboardListener.kt",
                "com/mc/engine/input/keyboard/KeyboardManager.kt",
                "com/mc/engine/input/keyboard/KeyboardProcessor.kt"
            )
        }
    }
}

dependencies {
    // GameEngine Core Multiplatform
    implementation(project(":engine-core"))

    // Android and Compose dependencies
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.material3)
}


publishing {
    publications {
        register<MavenPublication>("release") {
            afterEvaluate {
                from(components["release"])
            }
            groupId = "com.mc.engine"
            artifactId = "gameengine-android"
            version = "1.0.0-alpha"
        }
    }
}
