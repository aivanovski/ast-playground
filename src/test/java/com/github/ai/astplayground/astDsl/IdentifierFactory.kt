package com.github.ai.astplayground.astDsl

import com.github.ai.astplayground.transpiler.parser.model.JExpression
import com.github.ai.astplayground.transpiler.parser.model.Operator

object IdentifierFactory {

    infix fun JExpression.Identifier.plus(another: JExpression): JExpression.BinaryExpression {
        return JExpression.BinaryExpression(
            operator = Operator.PLUS,
            lhs = this,
            rhs = another
        )
    }

    fun String.asIdentifier(): JExpression.Identifier {
        return JExpression.Identifier(
            name = this
        )
    }

    infix fun String.field(
        fieldName: String
    ): JExpression.FieldAccess {
        val identifierName = this
        val identifier = JExpression.Identifier(identifierName)

        return JExpression.FieldAccess(
            name = fieldName,
            expression = identifier
        )
    }

    infix fun String.invokeConstructor(
        arguments: List<JExpression>
    ): JExpression.ConstructorInvocation {
        return JExpression.ConstructorInvocation(
            identifier = JExpression.Identifier(this),
            arguments = arguments
        )
    }

    infix fun String.method(
        methodName: String
    ): JExpression.FieldAccess {
        val identifierName = this
        val identifier = JExpression.Identifier(identifierName)

        return JExpression.FieldAccess(
            name = methodName,
            expression = identifier
        )
    }

    infix fun JExpression.FieldAccess.method(
        methodName: String
    ): JExpression.FieldAccess {
        return JExpression.FieldAccess(
            name = methodName,
            expression = this
        )
    }
}