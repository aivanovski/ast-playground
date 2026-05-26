package com.aivanouski.either.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class EitherGradlePlugin : KotlinCompilerPluginSupportPlugin {
    override fun apply(target: Project) = Unit

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true

    override fun applyToCompilation(
        kotlinCompilation: KotlinCompilation<*>,
    ): Provider<List<SubpluginOption>> = kotlinCompilation.target.project.provider { emptyList() }

    override fun getCompilerPluginId(): String = "com.aivanouski.either.compiler"

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        "com.aivanouski.either",
        "compiler-plugin",
        "0.1.0",
    )
}
