package com.github.ai.astplayground.transpiler

import com.github.ai.astplayground.extension.COMPANION_OBJECT
import com.github.ai.astplayground.extension.INDENT
import com.github.ai.astplayground.extension.WHITESPACE
import com.github.ai.astplayground.extension.addPrefixIfNeed
import com.github.ai.astplayground.extension.addSuffixIfNeed
import com.github.ai.astplayground.extension.addSuffixWhenEndsWith
import com.github.ai.astplayground.extension.addSuffixWhenNotContains
import com.github.ai.astplayground.extension.getCurrentLine
import com.github.ai.astplayground.extension.getIndentationLevel
import com.github.ai.astplayground.extension.getName
import com.github.ai.astplayground.model.VariableType
import com.github.javaparser.JavaParser
import com.github.javaparser.ParserConfiguration
import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.PackageDeclaration
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.ConstructorDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.Parameter
import com.github.javaparser.ast.body.VariableDeclarator
import com.github.javaparser.ast.expr.AssignExpr
import com.github.javaparser.ast.expr.BinaryExpr
import com.github.javaparser.ast.expr.BooleanLiteralExpr
import com.github.javaparser.ast.expr.CharLiteralExpr
import com.github.javaparser.ast.expr.DoubleLiteralExpr
import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.expr.FieldAccessExpr
import com.github.javaparser.ast.expr.IntegerLiteralExpr
import com.github.javaparser.ast.expr.LongLiteralExpr
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.expr.NullLiteralExpr
import com.github.javaparser.ast.expr.ObjectCreationExpr
import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.expr.StringLiteralExpr
import com.github.javaparser.ast.expr.VariableDeclarationExpr
import com.github.javaparser.ast.stmt.BlockStmt
import com.github.javaparser.ast.stmt.ExpressionStmt
import com.github.javaparser.ast.stmt.ForEachStmt
import com.github.javaparser.ast.stmt.IfStmt
import com.github.javaparser.ast.stmt.ReturnStmt
import com.github.javaparser.ast.stmt.Statement
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.type.PrimitiveType
import com.github.javaparser.ast.type.PrimitiveType.Primitive
import com.github.javaparser.ast.type.ReferenceType
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import com.github.javaparser.resolution.TypeSolver
import com.github.javaparser.resolution.types.ResolvedReferenceType
import com.github.javaparser.symbolsolver.JavaSymbolSolver
import com.github.javaparser.symbolsolver.reflectionmodel.ReflectionMethodDeclaration
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver
import java.nio.file.Paths
import kotlin.jvm.optionals.getOrNull


class DeepJavaToKotlinTranspiler : VoidVisitorAdapter<Void?>(), SourceTranspiler {

    private val output = StringBuilder()

    override fun transpile(input: String): String {
        val typeSolver: TypeSolver = CombinedTypeSolver(
            ReflectionTypeSolver(),  // For JRE types
            JavaParserTypeSolver(Paths.get("src")) // Project's src folder
        )

        val symbolSolver = JavaSymbolSolver(typeSolver)

        val config = ParserConfiguration()
            .setSymbolResolver(symbolSolver)

        val parseResult = JavaParser(config).parse(input)
        if (!parseResult.isSuccessful) {
            throw IllegalStateException("Cannot parse code")
        }

        val transformer = StaticMembersTransformer()

        val node = transformer.transform(parseResult.result.get())
        node.accept(this, null)


        return output.toString()
    }

    override fun visit(n: PackageDeclaration, arg: Void?) {
        if (output.isNotEmpty()) {
            output.appendLine()
        }
        output.append("package ${n.nameAsString}")
    }

    override fun visit(n: ImportDeclaration, arg: Void?) {
        if (output.isNotEmpty()) {
            output.appendLine()
        }
        output.append("import ${n.nameAsString}")
    }

    override fun visit(n: ClassOrInterfaceDeclaration, arg: Void?) {
        val indent = getCurrentIndentation()
        val modifiers = n.modifiers

        if (output.isNotEmpty()) {
            output.appendLine()
        }

        output.append(indent)
        for (modifier in modifiers) {
            visitModifier(modifier)

            if (modifier.keyword != Modifier.Keyword.PUBLIC) {
                output.append(" ")
            }
        }

        when {
            n.nameAsString == COMPANION_OBJECT -> output.append("companion object {")
            else -> output.append("class " + n.nameAsString + " {")
        }

        super.visit(n, arg)

        output.appendLine()
        output.append(indent)
        output.append("}")
    }

    override fun visit(n: Modifier, arg: Void?) {
    }

    override fun visit(n: FieldDeclaration, arg: Void?) {
        val name = n.getVariable(0).nameAsString
        val modifiers = n.modifiers

        output.appendLine()
        output.append(INDENT)

        for (modifier in modifiers) {
            visitModifier(modifier)
            if (modifier.keyword != Modifier.Keyword.PUBLIC) {
                output.append(" ")
            }
        }

        output.append("var $name: ")

        super.visit(n, arg)
    }

    override fun visit(n: MethodDeclaration, arg: Void?) {
        val indent = getCurrentIndentation()
        val type = n.type
        val body = n.body.getOrNull()
        val parameters = n.parameters
        val modifiers = n.modifiers

        output.appendLine()
        output.append(indent)

        for (modifier in modifiers) {
            visitModifier(modifier)
            if (modifier.keyword != Modifier.Keyword.PUBLIC) {
                output.append(" ")
            }
        }

        output.append("fun " + n.nameAsString + "(")
        visitParameters(parameters)
        output.append(")")

        when (type) {
            is PrimitiveType -> {
                output.append(": ")
                visit(type, arg)
            }

            is ClassOrInterfaceType -> {
                output.append(": ")
                visit(type, arg)
            }
        }

        if (body != null) {
            visit(body, arg)
        } else {
            // TODO: add {}
        }
    }

    override fun visit(n: ConstructorDeclaration, arg: Void?) {
        val indent = getCurrentIndentation()
        val parameters = n.parameters
        val body = n.body
        val typeName = n.nameAsString

        output.appendLine()
        output.append(indent)

        output.append("constructor(")
        visitParameters(parameters)
        output.append(")")

        visit(body, arg)
    }

    override fun visit(n: Parameter, arg: Void?) {
        val type = n.type
        val name = n.nameAsString

        output.append("$name: ")

        when (type) {
            is PrimitiveType -> visit(type, arg)
            is ClassOrInterfaceType -> visit(type, arg)
            else -> throw NotImplementedError()
        }
    }

    override fun visit(n: BlockStmt, arg: Void?) {
        val indent = getCurrentIndentation()
        output.append(" {")

        for (statement in n.statements) {
            output.appendLine()
            output.append(indent.plus(INDENT))

            when (statement) {
                is ExpressionStmt -> visitExpression(statement.expression)
                is ReturnStmt -> visit(statement, arg)
                is ForEachStmt -> visit(statement, arg)
                is IfStmt -> visit(statement, arg)
                else -> throw NotImplementedError("Not implemented for statement: ${statement::class} $statement") }
        }

        output.appendLine()
        output.append(indent).append("}")
    }

    override fun visit(n: IfStmt, arg: Void?) {
        val condition = n.condition
        val thenBody: Statement? = n.thenStmt

        output.append("if (")
        visitExpression(condition)
        output.append(")")

        if (thenBody != null) {
            visitStatement(thenBody)
        }
    }

    override fun visit(n: ForEachStmt, arg: Void?) {
        val variable = n.variable
        val iterable = n.iterable
        val body = n.body

        output.append("for (${variable.getVariable(0).nameAsString} in ")
        visitExpression(iterable)
        output.append(")")

        visitStatement(body)
    }

    private fun visitStatement(statement: Statement) {
        when (statement) {
            is BlockStmt -> visit(statement, null)
            is ReturnStmt -> visit(statement, null)
            else -> throw NotImplementedError("Not implemented: ${statement::class} $statement")
        }
    }

    override fun visit(n: ReturnStmt, arg: Void?) {
        val expression = n.expression.getOrNull()

        if (output.last().toString() != WHITESPACE) {
            output.append(WHITESPACE)
        }
        output.append("return")

        if (expression != null) {
            output.append(WHITESPACE)
            visitExpression(expression)
        }
    }

    private fun visitExpression(expression: Expression) {
        when (expression) {
            is BooleanLiteralExpr -> visit(expression, null)
            is CharLiteralExpr -> visit(expression, null)
            is IntegerLiteralExpr -> visit(expression, null)
            is LongLiteralExpr -> visit(expression, null)
            is DoubleLiteralExpr -> visit(expression, null)
            is BinaryExpr -> visit(expression, null)
            is StringLiteralExpr -> visit(expression, null)
            is NullLiteralExpr -> visit(expression, null)
            is FieldAccessExpr -> visit(expression, null)
            is NameExpr -> visit(expression, null)
            is MethodCallExpr -> visit(expression, null)
            is ObjectCreationExpr -> visit(expression, null)
            is AssignExpr -> visit(expression, null)
            is VariableDeclarationExpr -> visit(expression, null)
            else -> throw NotImplementedError("Not implemented for expression: ${expression::class} $expression")
        }
    }

    override fun visit(n: AssignExpr, arg: Void?) {
        val target = n.target
        val value = n.value

        val operator = when (n.operator) {
            AssignExpr.Operator.ASSIGN -> "="
            else -> throw NotImplementedError("Not implemented operator: ${n::class} $n")
        }

        visitExpression(target)
        output.append(" $operator ")
        visitExpression(value)
    }

    override fun visit(n: ObjectCreationExpr, arg: Void?) {
        val type = n.type
        val arguments = n.arguments

        val fixedType = type.toString()
            .replace("<>", "")

        output.append(fixedType).append("(")

        for ((index, argument) in arguments.withIndex()) {
            visitExpression(argument)
            if (index != arguments.lastIndex) {
                output.append(", ")
            }
        }

        output.append(")")
    }

    override fun visit(n: NullLiteralExpr, arg: Void?) {
        output.append("null")
    }

    override fun visit(n: PrimitiveType, arg: Void?) {
        val type = n.type.toVariableType()
        output.append(type.getName())
    }

    override fun visit(n: BooleanLiteralExpr, arg: Void?) {
        output.append(n.toString())
    }

    override fun visit(n: CharLiteralExpr, arg: Void?) {
        output.append(n.toString())
    }

    override fun visit(n: IntegerLiteralExpr, arg: Void?) {
        val parent = n.parentNode.getOrNull()
        if (parent is VariableDeclarator) {
            val type = parent.type

            when {
                type is PrimitiveType && type.type == Primitive.BYTE -> {
                    output.append(
                        n.toString()
                            .addPrefixIfNeed("0x")
                    )
                }

                type is PrimitiveType && type.type == Primitive.LONG -> {
                    output.append(
                        n.toString()
                            .addSuffixIfNeed("L")
                    )
                }

                type is PrimitiveType && type.type == Primitive.FLOAT -> {
                    output.append(
                        n.toString()
                            .addSuffixIfNeed("F")
                    )
                }

                type is PrimitiveType && type.type == Primitive.DOUBLE -> {
                    output.append(
                        n.toString()
                            .addSuffixIfNeed(".0")
                    )
                }

                else -> {
                    output.append(n.toString())
                }
            }
        } else {
            output.append(n.toString())
        }
    }

    override fun visit(n: LongLiteralExpr, arg: Void?) {
        val value = n.toString()
            .replace("l", "L")
            .addSuffixIfNeed("L")

        output.append(value)
    }

    override fun visit(n: DoubleLiteralExpr, arg: Void?) {
        val parent = n.parentNode.getOrNull()

        if (parent is VariableDeclarator) {
            val parentType = parent.type

            when {
                parentType is PrimitiveType && parentType.type == Primitive.FLOAT -> {
                    output.append(n.toString().toKotlinFloat())
                }

                parentType is PrimitiveType && parentType.type == Primitive.DOUBLE -> {
                    output.append(n.toString().toKotlinDouble())
                }

                else -> {
                    output.append(n.toString())
                }
            }
        } else {
            val value = n.toString()

            when {
                value.contains("f", ignoreCase = true) -> {
                    output.append(value.toKotlinFloat())
                }

                value.contains("d", ignoreCase = true) -> {
                    output.append(value.toKotlinDouble())
                }

                else -> {
                    output.append(value)
                }
            }
        }
    }

    override fun visit(n: StringLiteralExpr, arg: Void?) {
        output.append(n.toString())
    }

    override fun visit(n: ClassOrInterfaceType, arg: Void?) {
        output.append("$n?")
    }

    override fun visit(n: BinaryExpr, arg: Void?) {
        val left = n.left
        val right = n.right
        val operator = n.operator
//        visit(n, arg)
        output.append(n.toString())
        // TODO: implement traversing

//        visit(n.left, arg)

    }

    override fun visit(n: VariableDeclarationExpr, arg: Void?) {
        val name = n.getVariable(0).nameAsString
        val modifiers = n.modifiers

        output.append("var $name: ")

        super.visit(n, arg)
    }

    override fun visit(n: VariableDeclarator, arg: Void?) {
        when (val type = n.type) {
            is PrimitiveType -> {
                val initializer = n.initializer.getOrNull()

                visit(type, arg)
                output.append(" = ")

                if (initializer != null) {
                    visitExpression(initializer)
                } else {
                    output.append(type.type.getDefaultValue())
                }
            }

            is ClassOrInterfaceType -> {
                val initializer = n.initializer.getOrNull()

                visit(type, arg)
                output.append(" = ")

                if (initializer != null) {
                    visitExpression(initializer)
                } else {
                    output.append("null")
                }
            }

            else -> {
                super.visit(n, arg)
            }
        }
    }

    private fun Expression.toExpressionType(): VariableType? {
        return when (this) {
            is BooleanLiteralExpr -> VariableType.BOOLEAN
            is CharLiteralExpr -> VariableType.CHAR
            is IntegerLiteralExpr -> VariableType.INT
            is LongLiteralExpr -> VariableType.LONG
            is DoubleLiteralExpr -> VariableType.DOUBLE
            else -> null
        }
    }

    private fun Primitive.getDefaultValue(): String {
        return when (this) {
            Primitive.BOOLEAN -> "false"
            Primitive.BYTE -> "0x0"
            Primitive.CHAR -> "0.toChar()"
            Primitive.SHORT -> "0.toShort()"
            Primitive.INT -> "0"
            Primitive.LONG -> "0L"
            Primitive.FLOAT -> "0F"
            Primitive.DOUBLE -> "0.0"
        }
    }

    private fun Primitive.toVariableType(): VariableType {
        return when (this) {
            Primitive.BOOLEAN -> VariableType.BOOLEAN
            Primitive.BYTE -> VariableType.BYTE
            Primitive.CHAR -> VariableType.CHAR
            Primitive.SHORT -> VariableType.SHORT
            Primitive.INT -> VariableType.INT
            Primitive.LONG -> VariableType.LONG
            Primitive.FLOAT -> VariableType.FLOAT
            Primitive.DOUBLE -> VariableType.DOUBLE
        }
    }

    private fun getCurrentIndentation(): String {
        val indentLevel = output.getCurrentLine()
            ?.getIndentationLevel()
            ?: 0

        return when {
            output.isEmpty() -> ""
            output.last() == '{' -> INDENT.repeat(indentLevel + 1)
            else -> INDENT.repeat(indentLevel)
        }
    }

    private fun isSafeCall(n: MethodCallExpr): Boolean {
        val scopeType = n.scope.get().calculateResolvedType()
        val methodType = n.resolve()
        val scopePath = n.scope.get().toString()

        // TODO: requires type resolution

        return when {
            methodType.isStatic || scopePath.startsWith("System.") -> true
            else -> false
        }
    }

    override fun visit(n: MethodCallExpr, arg: Void?) {
        val scope = n.scope.getOrNull()
        val arguments = n.arguments

        if (scope != null) {
            visitExpression(scope)

            if (!isSafeCall(n)) {
                output.append("?.")
            } else {
                output.append(".")
            }
        }

        output.append(n.nameAsString).append("(")

        for ((index, argument) in arguments.withIndex()) {
            visitExpression(argument)

            if (index != arguments.lastIndex) {
                output.append(", ")
            }
        }

        output.append(")")
    }

    override fun visit(n: FieldAccessExpr, arg: Void?) {
        output.append(n.toString())
    }

    override fun visit(n: NameExpr?, arg: Void?) {
        output.append(n.toString())
    }

    override fun visit(n: SimpleName, arg: Void?) {
        super.visit(n, arg)
    }

    private fun visitModifier(modifier: Modifier) {
        when (modifier.keyword) {
            Modifier.Keyword.PUBLIC -> output.append("")
            Modifier.Keyword.PRIVATE -> output.append("private")
            Modifier.Keyword.PROTECTED -> output.append("protected")
            else -> throw NotImplementedError("modifier: $modifier")
        }
    }

    private fun visitParameters(parameters: NodeList<Parameter>) {
        for ((index, parameter) in parameters.withIndex()) {
            visit(parameter, null)
            if (index != parameters.lastIndex) {
                output.append(", ")
            }
        }
    }

    private fun String.toKotlinFloat(): String {
        return this
            .replace("f", "F")
            .addSuffixIfNeed("F")
    }

    private fun String.toKotlinDouble(): String {
        return this
            .replace("d", "")
            .replace("D", "")
            .addSuffixWhenNotContains(suffix = ".0", notContains = ".")
            .addSuffixWhenEndsWith(suffix = "0", endsWith = ".")
    }
}