import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    application
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenCentral()
    jcenter()
}

application {
    mainClass = "org.catinthedark.alyoep.desktop.DesktopLauncher"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}


sourceSets {
    main {
        resources {
            setSrcDirs(listOf("../game/assets"))
        }
    }
}

val gdxVersion = "1.12.1"
val gdxControllersVersion = "2.2.3"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.badlogicgames.gdx:gdx-lwjgl3-glfw-awt-macos:$gdxVersion")
    implementation("com.badlogicgames.gdx-controllers:gdx-controllers-desktop:$gdxControllersVersion")
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-desktop")

    implementation(project(":game"))
}
