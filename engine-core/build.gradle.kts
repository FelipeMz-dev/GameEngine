plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("maven-publish")
}

version = "1.0.0-alpha"
group = "com.mc.engine"

kotlin {
    // JVM/Desktop target
    jvm("desktop") {
        compilations.all {
            compilerOptions.configure {
                jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.dyn4j)
        }
        
               
        
        val desktopMain by getting {
            dependencies {
                implementation(libs.androidx.compose.ui.jvmstubs)
                implementation(libs.androidx.compose.foundation.jvmstubs)
            }
        }
    }
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.mc.engine"
            artifactId = "gameengine-core"
            version = "1.0.0-alpha"
        }
    }
}



