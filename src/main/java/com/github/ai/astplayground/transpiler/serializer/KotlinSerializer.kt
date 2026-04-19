package com.github.ai.astplayground.transpiler.serializer

import com.github.ai.astplayground.transpiler.model.exception.AstSerializationException
import com.github.ai.astplayground.transpiler.parser.model.JCodeBlock
import com.github.ai.astplayground.transpiler.parser.model.JInitializerBlock
import com.github.ai.astplayground.transpiler.parser.model.JMethod
import com.github.ai.astplayground.transpiler.parser.model.Modifier
import com.github.ai.astplayground.transpiler.parser.model.JParameter
import com.github.ai.astplayground.transpiler.parser.model.JTypeReference
import com.github.ai.astplayground.transpiler.parser.model.TypeReferenceKind
import com.github.ai.astplayground.transpiler.parser.model.isPrimitive
import com.github.ai.astplayground.transpiler.parser.model.isPrimitiveBoolean
import com.github.ai.astplayground.transpiler.parser.model.isPrimitiveByte
import com.github.ai.astplayground.transpiler.parser.model.isPrimitiveChar
import com.github.ai.astplayground.transpiler.parser.model.isPrimitiveDouble
import com.github.ai.astplayground.transpiler.parser.model.isPrimitiveFloat
import com.github.ai.astplayground.transpiler.parser.model.isPrimitiveInt
import com.github.ai.astplayground.transpiler.parser.model.isPrimitiveLong
import com.github.ai.astplayground.transpiler.parser.model.Operator
import com.github.ai.astplayground.transpiler.parser.model.isConstructorInvocation
import com.github.ai.astplayground.transpiler.parser.model.isLiteral
import com.github.ai.astplayground.transpiler.transformer.model.KCodeBlock
import com.github.ai.astplayground.transpiler.transformer.model.KExpression
import com.github.ai.astplayground.transpiler.transformer.model.KParameter
import com.github.ai.astplayground.transpiler.transformer.model.KTypeReference
import com.github.ai.astplayground.transpiler.transformer.model.KotlinAstNode
import com.github.ai.astplayground.transpiler.transformer.model.isPrimitiveBoolean
import com.github.ai.astplayground.transpiler.transformer.model.isPrimitiveByte
import com.github.ai.astplayground.transpiler.transformer.model.isPrimitiveChar
import com.github.ai.astplayground.transpiler.transformer.model.isPrimitiveDouble
import com.github.ai.astplayground.transpiler.transformer.model.isPrimitiveFloat
import com.github.ai.astplayground.transpiler.transformer.model.isPrimitiveInt
import com.github.ai.astplayground.transpiler.transformer.model.isPrimitiveLong
import com.github.ai.astplayground.transpiler.transformer.model.isUnit
import org.checkerframework.checker.units.qual.m

class KotlinSerializer : AstSerializer<KotlinAstNode> {

    override fun serialize(nodes: List<KotlinAstNode>): String {
        val content = SourceCodeBuilder()

        for (node in nodes) {
            when (node) {
                is KotlinAstNode.Package -> content.serialize(node)
                is KotlinAstNode.Import -> content.serialize(node)
                is KotlinAstNode.KClass -> content.serialize(node)
                is KotlinAstNode.KMethod -> {}
                is KotlinAstNode.KField -> {}
                is KotlinAstNode.KConstructor -> {}
                else -> throw NotImplementedError("Not implemented for node: $node")
            }
        }

        return content.build()
    }

    private fun SourceCodeBuilder.serialize(node: KotlinAstNode.Package) {
        appendLine("package ${node.name}")
    }

    private fun SourceCodeBuilder.serialize(node: KotlinAstNode.Import) {
        val staticKeyword = if (node.isStatic) "static " else ""
        val importName =
            if (node.isAsterisk && !node.name.endsWith(".*")) "${node.name}.*" else node.name
        appendLine("import $staticKeyword$importName")
    }

    private fun SourceCodeBuilder.serialize(classNode: KotlinAstNode.KClass) {
        appendLine("class ${classNode.name}")

        val hasBody = classNode.nodes.isNotEmpty()

        if (hasBody) {
            appendBlock {
                for (node in classNode.nodes) {
                    when (node) {
                        is KotlinAstNode.KField -> {
                            newLine()
                            serialize(node)
                        }

                        is KotlinAstNode.KConstructor -> {
                            newLine()
                            serialize(node)
                        }

                        is KotlinAstNode.KMethod -> {
                            newLine()
                            serialize(node)
                        }

                        is KotlinAstNode.CompanionObject -> {
                            newLine()
                            serialize(node)
                        }

                        else -> throw NotImplementedError("Not implemented for node: $node")
                    }
                }
//                for (field in classNode.fields) {
//                    newLine()
//                    serialize(field)
//                }
//
//                for (constructor in classNode.constructors) {
//                    newLine()
//                    serialize(constructor)
//                }
//
//                for (method in classNode.methods) {
//                    newLine()
//                    serialize(method)
//                }
//
//                newLine()
//                append("companion object")
//                appendBlock {
//                    for (method in classNode.companionMethods) {
//                        newLine()
//                        serialize(method)
//                    }
//                }
            }
        }
    }

    private fun SourceCodeBuilder.serialize(field: KotlinAstNode.KField) {
        val name = field.name
        val type = formatTypeName(field.type)
        val value = formatFieldValue(field.initializer, field.type)

        append("var $name: $type = $value")
    }

    private fun SourceCodeBuilder.serialize(constructor: KotlinAstNode.KConstructor) {
        val parameters = constructor.parameters
            .map { parameter -> formatParameter(parameter) }
            .joinToString(separator = ", ")

        append("constructor($parameters)")

        when (constructor.body) {
            KCodeBlock.Empty -> append(" {}")
            is KCodeBlock.Expressions -> {
                val expressions = constructor.body.expressions

                appendBlock {
                    for (expression in expressions) {
                        newLine()
                        append(formatExpression(expression))
                    }
                }
            }
        }
    }

    private fun SourceCodeBuilder.serialize(companionObject: KotlinAstNode.CompanionObject) {
        append("companion object")
        appendBlock {
            for (node in companionObject.nodes) {
                when (node) {
                    is KotlinAstNode.KMethod -> {
                        newLine()
                        serialize(node)
                    }

                    else -> throw NotImplementedError("Not implemented for node: $node")
                }
            }
        }
    }

    private fun SourceCodeBuilder.serialize(method: KotlinAstNode.KMethod) {
        val name = method.name
        val isReturnUnit = method.returnType.isUnit()
        val returnType = formatTypeName(method.returnType)

        val parameters = method.parameters
            .map { parameter -> formatParameter(parameter) }
            .joinToString(separator = ", ")

        val returnDeclaration = if (!isReturnUnit) ": $returnType" else ""
        val declaration = "fun $name($parameters)$returnDeclaration"
        append(declaration)

        when (method.body) {
            KCodeBlock.Empty -> append(" {}")
            is KCodeBlock.Expressions -> {
                val expressions = method.body.expressions

                appendBlock {
                    for (expression in expressions) {
                        newLine()
                        append(formatExpression(expression))
                    }
                }
            }
        }
    }

    private fun formatParameter(parameter: KParameter): String {
        val name = parameter.name
        val type = formatTypeName(parameter.type)
        return "$name: $type"
    }

    private fun isTypeNullable(type: JTypeReference): Boolean {
        return !type.isPrimitive()
    }

    private fun formatFieldValue(
        initializer: KCodeBlock,
        type: KTypeReference
    ): String {
        return when (initializer) {
            KCodeBlock.Empty -> getDefaultValue(type)
            is KCodeBlock.Expressions -> formatExpressions(initializer.expressions)
        }
    }

    private fun formatTypeName(
        type: KTypeReference
    ): String {
        val typeArguments = type.typeArguments
            .map { argType -> formatTypeName(argType) }

        return buildString {
            append(type.name)

            if (typeArguments.isNotEmpty()) {
                val types = typeArguments.joinToString(separator = ",")
                append("<${types}>")
            }

            if (type.isNullable) {
                append("?")
            }
        }
    }

    private fun getDefaultValue(type: KTypeReference): String {
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

    private fun formatExpressions(expressions: List<KExpression>): String {
        return expressions.joinToString(separator = "\n") { nested ->
            formatExpression(nested)
        }
    }

    private fun formatExpression(expression: KExpression): String {
        return when (expression) {
            KExpression.Empty -> ""
            is KExpression.Identifier -> "$expression.name"
            is KExpression.TypedIdentifier -> {
//                val type = formatTypeName(expression.identifier)
                // TODO:
                throw NotImplementedError()
            }

            is KExpression.Initializer -> formatExpression(expression.initializer)
            is KExpression.Literal -> formatLiteral(expression)
            is KExpression.Expressions -> formatExpressions(expression.expressions)
            is KExpression.If -> formatIfExpression(expression)
            is KExpression.ForEachLoop -> formatForEachLoop(expression)
            is KExpression.Assignment -> {
                val variable = formatExpression(expression.variable)
                val value = formatExpression(expression.expression)
                "$variable = $value"
            }

            is KExpression.FieldAccess -> {
                "${formatExpression(expression.expression)}.${expression.name}"
            }

            is KExpression.BinaryExpression -> {
                val lhs = formatExpression(expression.lhs)
                val rhs = formatExpression(expression.rhs)
                "$lhs ${formatOperator(expression.operator)} $rhs"
            }

            is KExpression.MethodInvocation -> {
                val method = formatExpression(expression.method)
                val arguments = expression.arguments
                    .joinToString(separator = ", ") { argument -> formatExpression(argument) }
                "$method($arguments)"
            }

            is KExpression.ConstructorInvocation -> formatConstructorInvocation(expression)
            is KExpression.DeclareVariable -> formatVariableDeclaration(expression)
            is KExpression.Return -> "return ${formatExpression(expression.expression)}"
        }
    }

    private fun formatIfExpression(expression: KExpression.If): String {
        val condition = formatExpression(expression.condition)
        val thenBlock = formatBranchExpression(expression.thenExpression)
        val elseBlock = if (expression.elseExpression != KExpression.Empty) {
            " else ${formatBranchExpression(expression.elseExpression)}"
        } else {
            ""
        }

        return "if ($condition) $thenBlock$elseBlock"
    }

    private fun formatBranchExpression(expression: KExpression): String {
        val content = formatExpression(expression)
        return "{\n$content\n}"
    }

//    private fun formatForLoop(expression: JExpression.ForLoop): String {
//        val initializers = expression.initializers.joinToString(separator = "\n") { initializer ->
//            formatJExpression(initializer)
//        }
//        val condition = formatJExpression(expression.condition)
//        val body = formatJExpression(expression.body)
//        val updates = expression.updates.joinToString(separator = "\n") { update ->
//            formatJExpression(update)
//        }
//        val bodyWithUpdates = listOf(body, updates)
//            .filter { it.isNotBlank() }
//            .joinToString(separator = "\n")
//
//        return listOf(initializers, "while ($condition) {\n$bodyWithUpdates\n}")
//            .filter { it.isNotBlank() }
//            .joinToString(separator = "\n")
//    }

    private fun formatForEachLoop(expression: KExpression.ForEachLoop): String {
        val variableType = formatTypeName(expression.variable.type)
        val iterable = formatExpression(expression.iterable)
        val body = formatExpression(expression.body)
        // TODO: check for the type of collection
        return "for (${expression.variable.name} in $iterable!!) {\n$body\n}"
    }

    private fun formatConstructorInvocation(
        expression: KExpression.ConstructorInvocation
    ): String {
        val identifier = formatExpression(expression.identifier)

        val arguments = expression.arguments.map { argument ->
            formatExpression(argument)
        }.joinToString(separator = ",")

        return "$identifier($arguments)"
    }

    private fun formatVariableDeclaration(expression: KExpression.DeclareVariable): String {
//        val isNonNullable =
//            expression.type.isPrimitive()
//                || expression.initializer.isLiteral()
//                || expression.initializer.isConstructorInvocation()

//        val kotlinType =
        val type = formatTypeName(expression.type)
        val value = formatFieldValue(expression.initializer, expression.type)
        return "var ${expression.name}: $type = $value"
    }

    private fun formatOperator(operator: Operator): String {
        return when (operator) {
            Operator.PLUS -> "+"
            Operator.MINUS -> "-"
            Operator.MULTIPLY -> "*"
            Operator.DIVIDE -> "/"
            Operator.LESS_THAN -> "<"
            Operator.GREATER_THAN -> ">"
            Operator.EQUALS -> "=="
            Operator.NOT_EQUALS -> "!="
            Operator.AND -> "&&"
            Operator.OR -> "||"
        }
    }

    private fun formatLiteral(literal: KExpression.Literal): String {
        return when (literal) {
            is KExpression.BooleanLiteral -> literal.value.toString()
            is KExpression.ByteLiteral -> literal.value.toString()
            is KExpression.CharLiteral -> "'${literal.value}'"
            is KExpression.IntLiteral -> literal.value.toString()
            is KExpression.LongLiteral -> literal.value.toString() + "L"
            is KExpression.FloatLiteral -> literal.value.toString() + "F"
            is KExpression.DoubleLiteral -> literal.value.toString()
            is KExpression.StringLiteral -> "\"" + literal.value + "\""
            is KExpression.Null -> "null"
        }
    }

    private fun JMethod.isStatic(): Boolean {
        return Modifier.STATIC in modifiers
    }

    private fun JCodeBlock.isNotEmpty(): Boolean {
        return this != JCodeBlock.Empty
    }
}
