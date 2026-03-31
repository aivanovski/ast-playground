package com.github.ai.astplayground.astDsl

import com.github.ai.astplayground.transpiler.model.Expression
import com.github.ai.astplayground.transpiler.model.Operator

object ExpressionFactory {

    infix fun Expression.FieldAccess.invoke(
        argument: Expression
    ): Expression.MethodInvocation {
        return Expression.MethodInvocation(
            arguments = if (argument == Expression.Empty) emptyList() else listOf(argument),
            method = this
        )
    }

    infix fun Expression.or(another: Expression) = Expression.BinaryExpression(
        operator = Operator.OR,
        lhs = this,
        rhs = another
    )

    infix fun Expression.and(another: Expression) = Expression.BinaryExpression(
        operator = Operator.AND,
        lhs = this,
        rhs = another
    )

    infix fun Expression.equal(another: Expression) = Expression.BinaryExpression(
        operator = Operator.EQUALS,
        lhs = this,
        rhs = another
    )

    fun identifier(vararg path: String): Expression {
        val identifier = Expression.Identifier(path.first())

        // System.out.println => println -> out -> System
        val fields = mutableListOf<Expression.FieldAccess>()

        var previousField: Expression = identifier

        for (fieldName in path.drop(1)) {
            val field = Expression.FieldAccess(
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
    ) = Expression.TypedIdentifier(
        identifier = TypeReferenceFactory.type(name),
        types = listOf(TypeReferenceFactory.type(parameterType))
    )

    fun typedIdentifier(
        name: String
    ) = Expression.TypedIdentifier(
        identifier = TypeReferenceFactory.type(name),
        types = listOf()
    )

    fun constructor(
        identifier: Expression,
        arguments: List<Expression> = emptyList()
    ) = Expression.ConstructorInvocation(
        identifier = identifier,
        arguments = arguments
    )

    fun string(value: String) = Expression.StringLiteral(value)
    fun int(value: Int) = Expression.IntLiteral(value)

    fun String.literal() = Expression.StringLiteral(this)
    fun Int.literal() = Expression.IntLiteral(this)
    fun Boolean.literal() = Expression.BooleanLiteral(this)
}