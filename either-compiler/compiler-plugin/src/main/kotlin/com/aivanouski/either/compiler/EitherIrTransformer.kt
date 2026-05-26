package com.aivanouski.either.compiler

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.name.FqName

@OptIn(UnsafeDuringIrConstructionAPI::class)
class EitherIrTransformer(
    private val pluginContext: IrPluginContext,
) : IrElementTransformerVoidWithContext() {

    var detectedEitherCalls: Int = 0
        private set

    // override fun visitFunctionNew(declaration: IrFunction): IrStatement {
    //     return super.visitFunctionNew(declaration)
    // }

    override fun visitCall(expression: IrCall): IrExpression {
        expression.transformChildren(this, null)

        if (!expression.isEitherCall()) {
            return expression
        }

        val function = expression.symbol.owner
        val argument = expression.arguments.getOrNull(0)



        // function.valueParameters

        detectedEitherCalls += 1
        pluginContext.messageCollector.report(
            CompilerMessageSeverity.WARNING,
            "[either-plugin] detected either{} call #$detectedEitherCalls in ${currentFile.fileEntry.name}",
        )
        pluginContext.messageCollector.report(
            CompilerMessageSeverity.WARNING,
            "epression=$expression"
        )

        if (argument is IrFunctionExpression) {
            pluginContext.messageCollector.report(
                CompilerMessageSeverity.WARNING,
                "Argument is a function =${argument.function}"
            )
        }

        return expression
    }

    private fun IrCall.isEitherCall(): Boolean {
        val function = symbol.owner
        return function.fqNameWhenAvailable == EITHER_FUNCTION_FQ_NAME
    }
}

private val EITHER_FUNCTION_FQ_NAME = FqName("com.aivanouski.playground.core.either")
