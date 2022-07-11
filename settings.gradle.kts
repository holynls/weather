pluginManagement {
    val kotlinVersion: String by settings
    val springBootVersion: String by settings
    val jibPluginVersion: String by settings
    val kotlinJvmPluginVersion: String by settings
    val ktLintPluginVersion: String by settings
    val detektPluginVersion: String by settings

    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.spring.io/release")
        maven("https://repo.spring.io/milestone/")
        maven("https://jitpack.io")
    }

    plugins {
        jacoco
        kotlin("jvm") version kotlinJvmPluginVersion
        id("com.google.cloud.tools.jib") version jibPluginVersion
        id("org.jetbrains.kotlin.kapt") version kotlinVersion
        id("io.gitlab.arturbosch.detekt") version detektPluginVersion
        id("org.jlleitschuh.gradle.ktlint") version ktLintPluginVersion
        id("org.jetbrains.kotlin.plugin.jpa") version kotlinVersion
        id("org.springframework.boot") version springBootVersion
        id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
    }
}

rootProject.name = "weather-api"

include(":domain")
include(":interfaces")
