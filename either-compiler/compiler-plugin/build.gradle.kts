group = "com.aivanouski.either"
version = "0.1.0"

plugins {
    kotlin("jvm") version "2.2.0"
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable:2.2.0")
}
