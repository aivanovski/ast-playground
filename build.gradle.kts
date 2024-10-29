import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.io.BufferedWriter
import java.io.FileInputStream
import java.io.FileWriter
import java.util.Properties
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    id("com.github.johnrengelman.shadow") version "4.0.4"
}

val appVersion = "0.1.0"

group = "com.github.ai.astplayground"
version = appVersion

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
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
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.5.2")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.5.2")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.5.2")
    testImplementation("io.mockk:mockk:1.12.3")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.23")
    implementation("io.insert-koin:koin-core:3.1.5")
    implementation("com.github.javaparser:javaparser-symbol-solver-core:3.26.2")
}