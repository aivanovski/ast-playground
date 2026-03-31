package com.github.ai.astplayground.transpiler.serializer

import com.github.ai.astplayground.transpiler.model.Expression
import com.github.ai.astplayground.transpiler.model.Field
import com.github.ai.astplayground.transpiler.model.InitializerBlock
import com.github.ai.astplayground.transpiler.model.JavaAstNode
import com.github.ai.astplayground.transpiler.model.TypeReference
import com.github.ai.astplayground.transpiler.model.TypeReferenceKind
import com.github.ai.astplayground.transpiler.model.isPrimitive
import com.github.ai.astplayground.transpiler.model.isPrimitiveBoolean
import com.github.ai.astplayground.transpiler.model.isPrimitiveByte
import com.github.ai.astplayground.transpiler.model.isPrimitiveChar
import com.github.ai.astplayground.transpiler.model.isPrimitiveDouble
import com.github.ai.astplayground.transpiler.model.isPrimitiveFloat
import com.github.ai.astplayground.transpiler.model.isPrimitiveInt
import com.github.ai.astplayground.transpiler.model.isPrimitiveLong
import org.checkerframework.checker.initialization.qual.Initialized

class KotlinSerializer : AstSerializer {

    override fun serialize(nodes: List<JavaAstNode>): String {
        val content = SourceCodeBuilder()

        for (node in nodes) {
            when (node) {
                is JavaAstNode.Package -> content.serialize(node)
                is JavaAstNode.Class -> content.serialize(node)

                else -> throw NotImplementedError("Unhandled node: $node")
            }
        }

        return content.build()
    }

    private fun SourceCodeBuilder.serialize(node: JavaAstNode.Package) {
        append("package ${node.name}")
    }

    private fun SourceCodeBuilder.serialize(node: JavaAstNode.Class) {
        append("class ${node.name}")

        val hasBody =
            (node.fields.isNotEmpty() || node.constructors.isNotEmpty() || node.methods.isNotEmpty())

        if (hasBody) {
            appendBlock {
                for (field in node.fields) {
                    serialize(field)
                }
            }
        }
    }

    private fun SourceCodeBuilder.serialize(field: Field) {
        val isNonNullable =
            field.type.isPrimitive() || (field.initializer is InitializerBlock.ExpressionBlock &&
                field.initializer.expression is Expression.Literal)

        val name = field.name
        val type = if (isNonNullable) formatType(field.type) else formatType(field.type) + "?"
        val value = formatFieldValue(field.initializer, field.type)

        append("var $name: $type = $value")
    }

    private fun formatFieldValue(
        initializer: InitializerBlock,
        type: TypeReference
    ): String {
        return if (initializer is InitializerBlock.ExpressionBlock) {
            formatExpression(initializer.expression)
        } else {
            getDefaultValue(type)
        }
    }

    private fun formatType(
        type: TypeReference
    ): String {
        return if (type.kind == TypeReferenceKind.PRIMITIVE) {
            type.name.first().uppercase() + type.name.drop(1)
        } else {
            type.name
        }
    }

    private fun getDefaultValue(type: TypeReference): String {
        return when {
            type.isPrimitiveBoolean() -> "false"
            type.isPrimitiveByte() -> "0"
            type.isPrimitiveChar() -> "0.toChar()"
            type.isPrimitiveInt() -> "0"
            type.isPrimitiveLong() -> "0L"
            type.isPrimitiveFloat() -> "0F"
            type.isPrimitiveDouble() -> "0.0"
            else -> "null"
        }

    }

    private fun formatExpression(expression: Expression): String {
        return when (expression) {
            is Expression.Literal -> formatLiteral(expression)
            else -> throw NotImplementedError("Not implemented expression: $expression")
        }
    }

    private fun formatLiteral(literal: Expression.Literal): String {
        return when (literal) {
            is Expression.BooleanLiteral -> literal.value.toString()
            is Expression.ByteLiteral -> literal.value.toString()
            is Expression.CharLiteral -> {
                "${literal.value.code}.toChar()"
            }
            is Expression.IntLiteral -> literal.value.toString()
            is Expression.LongLiteral -> literal.value.toString() + "L"
            is Expression.FloatLiteral -> literal.value.toString() + "F"
            is Expression.DoubleLiteral -> literal.value.toString()
            else -> literal.toString()
        }
    }
}