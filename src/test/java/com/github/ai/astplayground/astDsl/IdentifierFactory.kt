package com.github.ai.astplayground.astDsl

import com.github.ai.astplayground.transpiler.model.Expression

object IdentifierFactory {

    fun String.asIdentifier(): Expression.Identifier {
        return Expression.Identifier(
            name = this
        )
    }

    infix fun String.field(
        fieldName: String
    ): Expression.FieldAccess {
        val identifierName = this
        val identifier = Expression.Identifier(identifierName)

        return Expression.FieldAccess(
            name = fieldName,
            expression = identifier
        )
    }

    infix fun String.invokeConstructor(
        arguments: List<Expression>
    ): Expression.ConstructorInvocation {
        return Expression.ConstructorInvocation(
            identifier = Expression.Identifier(this),
            arguments = arguments
        )
    }

    infix fun String.method(
        methodName: String
    ): Expression.FieldAccess {
        val identifierName = this
        val identifier = Expression.Identifier(identifierName)

        return Expression.FieldAccess(
            name = methodName,
            expression = identifier
        )
    }

    infix fun Expression.FieldAccess.method(
        methodName: String
    ): Expression.FieldAccess {
        return Expression.FieldAccess(
            name = methodName,
            expression = this
        )
    }
}