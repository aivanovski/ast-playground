plugins {
    alias(libs.plugins.kotlinJvm)
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

val intellijIdeaContents = file("/Applications/IntelliJ IDEA.app/Contents")
val intellijUtilJar = intellijIdeaContents.resolve("lib/util-8.jar")
val intellijAnnotationsJar = intellijIdeaContents.resolve("lib/annotations.jar")
val kotlinPluginJar = intellijIdeaContents.resolve("plugins/Kotlin/lib/kotlin-plugin.jar")
val kotlinCompilerLibs = intellijIdeaContents.resolve("plugins/Kotlin/kotlinc/lib")

sourceSets {
    named("main") {
        java.srcDirs("src/main/java", "src/main/kotlin")
    }
    named("test") {
        java.srcDirs("src/test/java", "src/test/kotlin")
    }
}

dependencies {
    compileOnly(files(intellijUtilJar, intellijAnnotationsJar, kotlinPluginJar))
    compileOnly(fileTree(kotlinCompilerLibs) { include("*.jar") })
}
