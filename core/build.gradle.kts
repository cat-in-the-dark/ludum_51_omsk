import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm")
}

repositories {
    mavenCentral()
    jcenter()
}

//project.ext.assetsDir = File("assets")
sourceSets {
    main {
        resources {
            setSrcDirs(listOf("../assets"))
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

val gdxVersion = "1.12.1"
val gdxControllersVersion = "2.2.3"
val slfVersion = "1.7.26"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.badlogicgames.gdx:gdx:$gdxVersion")
    implementation("com.badlogicgames.gdx-controllers:gdx-controllers-core:$gdxControllersVersion")

    implementation("com.badlogicgames.gdx:gdx-freetype:$gdxVersion")

    implementation("org.slf4j:slf4j-simple:$slfVersion")
}
