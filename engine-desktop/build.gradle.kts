plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("maven-publish")
}

group = "com.mc.engine"
version = "1.0.0-alpha"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    withSourcesJar()
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies {
    implementation(project(":engine-core"))
}


publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = "com.mc.engine"
            artifactId = "gameengine-desktop"
            version = "1.0.0-alpha"
        }
    }
}
