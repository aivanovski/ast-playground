group = "com.aivanouski.either"
version = "0.1.0"

plugins {
    kotlin("jvm") version "2.2.0"
    `java-gradle-plugin`
}

kotlin {
    jvmToolchain(21)
}

gradlePlugin {
    plugins {
        create("eitherCompilerPlugin") {
            id = "com.aivanouski.either.compiler"
            implementationClass = "com.aivanouski.either.gradle.EitherGradlePlugin"
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin-api:2.2.0")
}
