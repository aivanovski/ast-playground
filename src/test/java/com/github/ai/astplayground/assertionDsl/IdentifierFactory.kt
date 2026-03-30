package com.github.ai.astplayground.assertionDsl

import com.github.ai.astplayground.transpiler.model.Expression

object IdentifierFactory {

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

object ExprFactory {
    infix fun Expression.FieldAccess.invoke(
        argument: Expression
    ): Expression.MethodInvocation {
        return Expression.MethodInvocation(
            arguments = listOf(argument),
            method = this
        )
    }
}