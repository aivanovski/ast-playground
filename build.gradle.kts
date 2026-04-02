import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.shadowJar)
    alias(libs.plugins.kotlinPowerAssert)
}

group = "com.github.ai.astplayground"
version = libs.versions.appVersion.get()

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

sourceSets {
    named("main") {
        java {
            exclude("org/jetbrains/kotlin/nj2k/**")
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
powerAssert {
    functions = listOf("io.kotest.matchers.shouldBe")
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("ast-playground")
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "com.github.ai.astplayground.MainKt"))
        }
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.kotest.runner)
    testImplementation(libs.mockk)

    implementation(libs.koin)
    implementation(libs.javaparserSymbolSolver)
}
