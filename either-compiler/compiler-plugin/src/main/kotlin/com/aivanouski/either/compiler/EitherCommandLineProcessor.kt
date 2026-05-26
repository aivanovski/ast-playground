package com.aivanouski.either.compiler

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration

@OptIn(ExperimentalCompilerApi::class)
class EitherCommandLineProcessor : CommandLineProcessor {
    override val pluginId: String = EITHER_COMPILER_PLUGIN_ID

    override val pluginOptions: Collection<AbstractCliOption> = emptyList()

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration,
    ) {
        error("Unexpected compiler option ${option.optionName} for $pluginId")
    }
}
