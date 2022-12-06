pluginManagement {
    val springBootVersion: String by settings
    val kotlinVersion: String by settings
    val springDependencyManagementVersion: String by settings

    plugins {
        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version springDependencyManagementVersion

        kotlin("jvm") version kotlinVersion
        kotlin("plugin.spring") version kotlinVersion
    }
}

rootProject.name = "kotlin-practice"
include("practice01-grammar")
include("practice02-reactive")