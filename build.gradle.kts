group = "org.catinthedark"
version = "1.0-SNAPSHOT"

tasks.wrapper {
    val gradleVersion = "7.5.1"
    distributionUrl = "https://services.gradle.org/distributions/gradle-$gradleVersion-all.zip"
}

plugins {
    kotlin("jvm") version "1.7.20" apply false
}
