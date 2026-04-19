package com.github.ai.astplayground.astDsl

import com.github.ai.astplayground.transpiler.parser.model.JExpression
import com.github.ai.astplayground.transpiler.parser.model.Operator

object ExpressionFactory {

    infix fun JExpression.FieldAccess.invoke(
        argument: JExpression
    ): JExpression.MethodInvocation {
        return JExpression.MethodInvocation(
            arguments = if (argument == JExpression.Empty) emptyList() else listOf(argument),
            method = this
        )
    }

    infix fun JExpression.or(another: JExpression) = JExpression.BinaryExpression(
        operator = Operator.OR,
        lhs = this,
        rhs = another
    )

    infix fun JExpression.and(another: JExpression) = JExpression.BinaryExpression(
        operator = Operator.AND,
        lhs = this,
        rhs = another
    )

    infix fun JExpression.equal(another: JExpression) = JExpression.BinaryExpression(
        operator = Operator.EQUALS,
        lhs = this,
        rhs = another
    )

    fun identifier(vararg path: String): JExpression {
        val identifier = JExpression.Identifier(path.first())

        // System.out.println => println -> out -> System
        val fields = mutableListOf<JExpression.FieldAccess>()

        var previousField: JExpression = identifier

        for (fieldName in path.drop(1)) {
            val field = JExpression.FieldAccess(
                name = fieldName,
                expression = previousField
            )

            fields.add(field)
            previousField = field
        }

        return if (fields.isNotEmpty()) {
            fields.last()
        } else {
            identifier
        }
    }

    fun typedIdentifier(
        name: String,
        parameterType: String
    ) = JExpression.TypedIdentifier(
        identifier = TypeReferenceFactory.type(name),
        types = listOf(TypeReferenceFactory.type(parameterType))
    )

    fun typedIdentifier(
        name: String
    ) = JExpression.TypedIdentifier(
        identifier = TypeReferenceFactory.type(name),
        types = listOf()
    )

    fun constructor(
        identifier: JExpression,
        arguments: List<JExpression> = emptyList()
    ) = JExpression.ConstructorInvocation(
        identifier = identifier,
        arguments = arguments
    )

    fun string(value: String) = JExpression.StringLiteral(value)
    fun int(value: Int) = JExpression.IntLiteral(value)

    fun String.literal() = JExpression.StringLiteral(this)
    fun Int.literal() = JExpression.IntLiteral(this)
    fun Boolean.literal() = JExpression.BooleanLiteral(this)
}