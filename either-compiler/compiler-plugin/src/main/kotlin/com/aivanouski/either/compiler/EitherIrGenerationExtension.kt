package com.aivanouski.either.compiler

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

class EitherIrGenerationExtension : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        pluginContext.messageCollector.report(
            CompilerMessageSeverity.WARNING,
            "[either-plugin] IR generation started for module ${moduleFragment.name}",
        )

        val transformer = EitherIrTransformer(pluginContext)
        moduleFragment.transform(transformer, null)

        pluginContext.messageCollector.report(
            CompilerMessageSeverity.WARNING,
            "[either-plugin] IR generation finished, either calls found: ${transformer.detectedEitherCalls}",
        )
    }
}
