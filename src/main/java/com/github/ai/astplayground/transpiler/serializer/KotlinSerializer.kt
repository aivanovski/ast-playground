package com.github.ai.astplayground.transpiler.serializer

import com.github.ai.astplayground.transpiler.model.exception.AstSerializationException
import com.github.ai.astplayground.transpiler.parser.model.CodeBlock
import com.github.ai.astplayground.transpiler.parser.model.Constructor
import com.github.ai.astplayground.transpiler.parser.model.Expression
import com.github.ai.astplayground.transpiler.parser.model.Field
import com.github.ai.astplayground.transpiler.parser.model.InitializerBlock
import com.github.ai.astplayground.transpiler.parser.model.Method
import com.github.ai.astplayground.transpiler.parser.model.Modifier
import com.github.ai.astplayground.transpiler.parser.model.Parameter
import com.github.ai.astplayground.transpiler.parser.model.TypeReference
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
import com.github.ai.astplayground.transpiler.serializer.model.KotlinAstNode

class KotlinSerializer : AstSerializer<KotlinAstNode> {

    override fun serialize(nodes: List<KotlinAstNode>): String {
        val content = SourceCodeBuilder()

        for (node in nodes) {
            when (node) {
                is KotlinAstNode.Package -> content.serialize(node)
                is KotlinAstNode.Import -> content.serialize(node)
                is KotlinAstNode.Class -> content.serialize(node)
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

    private fun SourceCodeBuilder.serialize(classNode: KotlinAstNode.Class) {
        appendLine("class ${classNode.name}")

        val hasBody =
            (classNode.fields.isNotEmpty()
                || classNode.constructors.isNotEmpty()
                || classNode.methods.isNotEmpty())

        if (hasBody) {
            val instanceMethods = classNode.methods.filter { method -> !method.isStatic() }
            val staticMethods = classNode.methods.filter { method -> method.isStatic() }

            appendBlock {
                for (field in classNode.fields) {
                    newLine()
                    serialize(field)
                }

                for (constructor in classNode.constructors) {
                    newLine()
                    serialize(constructor)
                }

                for (method in instanceMethods) {
                    newLine()
                    serialize(method)
                }

                if (staticMethods.isNotEmpty()) {
                    newLine()
                    append("companion object")
                    appendBlock {
                        for (method in staticMethods) {
                            newLine()
                            serialize(method)
                        }
                    }
                }
            }
        }
    }

    private fun SourceCodeBuilder.serialize(field: Field) {
        val isNonNullable =
            field.type.isPrimitive()
                || field.initializer.isLiteral()
                || field.initializer.isConstructorInvocation()

        val name = field.name
        val type = formatType(field.type, isNullable = !isNonNullable)
        val value = formatFieldValue(field.initializer, field.type)

        append("var $name: $type = $value")
    }

    private fun SourceCodeBuilder.serialize(constructor: Constructor) {
        val parameters = constructor.parameters
            .map { parameter -> formatParameter(parameter) }
            .joinToString(separator = ", ")

        append("constructor($parameters)")

        if (constructor.body is CodeBlock.Expressions) {
            val expressions = constructor.body.expressions

            appendBlock {
                for (expression in expressions) {
                    newLine()
                    append(formatExpression(expression))
                }
            }
        } else {
            append(" {}")
        }
    }

    private fun SourceCodeBuilder.serialize(method: Method) {
        val name = method.name
        val isReturnUnit = (method.returnType.kind == TypeReferenceKind.VOID)
        val isReturnTypeNullable = !method.returnType.isPrimitive()
        val returnType = formatType(method.returnType, isNullable = isReturnTypeNullable)

        val parameters = method.parameters
            .map { parameter -> formatParameter(parameter) }
            .joinToString(separator = ", ")

        val returnDeclaration = if (!isReturnUnit) ": $returnType" else ""
        val declaration = "fun $name($parameters)$returnDeclaration"
        append(declaration)

        if (method.body is CodeBlock.Expressions) {
            val expressions = method.body.expressions

            appendBlock {
                for (expression in expressions) {
                    newLine()
                    append(formatExpression(expression))
                }
            }
        } else {
            append(" {}")
        }
    }

    private fun formatParameter(parameter: Parameter): String {
        val name = parameter.name
        val isNullable = isTypeNullable(parameter.type)
        val type = formatType(parameter.type, isNullable = isNullable)
        return "$name: $type"
    }

    private fun isTypeNullable(type: TypeReference): Boolean {
        return !type.isPrimitive()
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
        type: TypeReference,
        isNullable: Boolean = true
    ): String {
        val argTypes = type.typeArguments
            .map { argType -> formatType(argType, isNullable = false) }
            .joinToString(separator = ", ")

        val formattedType = when (type.kind) {
            TypeReferenceKind.PRIMITIVE -> type.name.first().uppercase() + type.name.drop(1)
            TypeReferenceKind.VOID -> "Unit"
            else -> {
                if (argTypes.isNotEmpty()) {
                    "${type.name}<$argTypes>"
                } else {
                    type.name
                }
            }
        }

        return if (isNullable) {
            "$formattedType?"
        } else {
            formattedType
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
            is Expression.Null -> "null"
            is Expression.Literal -> formatLiteral(expression)
            is Expression.Identifier -> expression.name
            is Expression.Expressions -> expression.expressions.joinToString(separator = "\n") { nested ->
                formatExpression(nested)
            }

            is Expression.If -> formatIfExpression(expression)
            is Expression.ForLoop -> formatForLoop(expression)
            is Expression.ForEachLoop -> formatForEachLoop(expression)
            is Expression.Assignment -> {
                val variable = formatExpression(expression.variable)
                val value = formatExpression(expression.expression)
                "$variable = $value"
            }

            is Expression.FieldAccess -> "${formatExpression(expression.expression)}.${expression.name}"
            is Expression.BinaryExpression -> {
                val lhs = formatExpression(expression.lhs)
                val rhs = formatExpression(expression.rhs)
                "$lhs ${formatOperator(expression.operator)} $rhs"
            }

            is Expression.MethodInvocation -> {
                val method = formatExpression(expression.method)
                val arguments = expression.arguments
                    .joinToString(separator = ", ") { argument -> formatExpression(argument) }
                "$method($arguments)"
            }

            is Expression.ConstructorInvocation -> formatConstructorInvocation(expression)
            is Expression.DeclareVariable -> formatVariableDeclaration(expression)
            is Expression.Return -> "return ${formatExpression(expression.expression)}"
            else -> throw AstSerializationException("Not implemented expression", expression)
        }
    }

    private fun formatIfExpression(expression: Expression.If): String {
        val condition = formatExpression(expression.condition)
        val thenBlock = formatBranchExpression(expression.thenExpression)
        val elseBlock = if (expression.elseExpression != Expression.Empty) {
            " else ${formatBranchExpression(expression.elseExpression)}"
        } else {
            ""
        }

        return "if ($condition) $thenBlock$elseBlock"
    }

    private fun formatBranchExpression(expression: Expression): String {
        val content = formatExpression(expression)
        return "{\n$content\n}"
    }

    private fun formatForLoop(expression: Expression.ForLoop): String {
        val initializers = expression.initializers.joinToString(separator = "\n") { initializer ->
            formatExpression(initializer)
        }
        val condition = formatExpression(expression.condition)
        val body = formatExpression(expression.body)
        val updates = expression.updates.joinToString(separator = "\n") { update ->
            formatExpression(update)
        }
        val bodyWithUpdates = listOf(body, updates)
            .filter { it.isNotBlank() }
            .joinToString(separator = "\n")

        return listOf(initializers, "while ($condition) {\n$bodyWithUpdates\n}")
            .filter { it.isNotBlank() }
            .joinToString(separator = "\n")
    }

    private fun formatForEachLoop(expression: Expression.ForEachLoop): String {
        val isNullable = !expression.variable.type.isPrimitive()
        val variableType = formatType(expression.variable.type, isNullable = isNullable)
        val iterable = formatExpression(expression.iterable)
        val body = formatExpression(expression.body)
        // TODO: check for the type of collection
        return "for (${expression.variable.name} in ($iterable ?: emptyList())) {\n$body\n}"
    }

    private fun formatConstructorInvocation(
        expression: Expression.ConstructorInvocation
    ): String {
        val identifier = formatExpression(expression.identifier)

        val arguments = expression.arguments.map { argument ->
            formatExpression(argument)
        }.joinToString(separator = ",")

        return "$identifier($arguments)"
    }

    private fun formatVariableDeclaration(expression: Expression.DeclareVariable): String {
        val isNonNullable =
            expression.type.isPrimitive()
                || expression.initializer.isLiteral()
                || expression.initializer.isConstructorInvocation()

        val type = formatType(expression.type, isNullable = !isNonNullable)
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

    private fun formatLiteral(literal: Expression.Literal): String {
        return when (literal) {
            is Expression.BooleanLiteral -> literal.value.toString()
            is Expression.ByteLiteral -> literal.value.toString()
            is Expression.CharLiteral -> "${literal.value.code}.toChar()"
            is Expression.IntLiteral -> literal.value.toString()
            is Expression.LongLiteral -> literal.value.toString() + "L"
            is Expression.FloatLiteral -> literal.value.toString() + "F"
            is Expression.DoubleLiteral -> literal.value.toString()
            is Expression.StringLiteral -> "\"" + literal.value + "\""
            else -> literal.toString()
        }
    }

    private fun Method.isStatic(): Boolean {
        return Modifier.STATIC in modifiers
    }

    private fun InitializerBlock.isLiteral(): Boolean {
        return this is InitializerBlock.ExpressionBlock
            && expression is Expression.Literal
    }

    private fun InitializerBlock.isConstructorInvocation(): Boolean {
        return this is InitializerBlock.ExpressionBlock
            && expression is Expression.ConstructorInvocation
    }

    private fun CodeBlock.isNotEmpty(): Boolean {
        return this != CodeBlock.Empty
    }
}
