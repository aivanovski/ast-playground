package com.github.ai.astplayground.transpiler

import com.github.ai.astplayground.transpiler.model.CodeBlock
import com.github.ai.astplayground.transpiler.model.Constructor
import com.github.ai.astplayground.transpiler.model.Expression
import com.github.ai.astplayground.transpiler.model.Field
import com.github.ai.astplayground.transpiler.model.InitializerBlock
import com.github.ai.astplayground.transpiler.model.JavaAstNode
import com.github.ai.astplayground.transpiler.model.Method
import com.github.ai.astplayground.transpiler.model.Modifier
import com.github.ai.astplayground.transpiler.model.Parameter
import com.github.ai.astplayground.transpiler.model.TypeReference
import com.github.ai.astplayground.transpiler.model.TypeReferenceKind
import com.github.ai.astplayground.transpiler.model.exception.InvalidAstTreeNodeException
import com.github.ai.astplayground.transpiler.model.isPrimitiveByte
import com.github.ai.astplayground.transpiler.model.isPrimitiveChar
import com.github.ai.astplayground.transpiler.model.isPrimitiveDouble
import com.github.ai.astplayground.transpiler.model.isPrimitiveFloat
import com.github.ai.astplayground.transpiler.model.isPrimitiveInt
import com.github.ai.astplayground.transpiler.model.isPrimitiveLong
import com.sun.source.tree.AnnotatedTypeTree
import com.sun.source.tree.BlockTree
import com.sun.source.tree.ClassTree
import com.sun.source.tree.CompilationUnitTree
import com.sun.source.tree.ExpressionStatementTree
import com.sun.source.tree.ExpressionTree
import com.sun.source.tree.IdentifierTree
import com.sun.source.tree.ImportTree
import com.sun.source.tree.LiteralTree
import com.sun.source.tree.MemberSelectTree
import com.sun.source.tree.MethodInvocationTree
import com.sun.source.tree.MethodTree
import com.sun.source.tree.NewClassTree
import com.sun.source.tree.ParameterizedTypeTree
import com.sun.source.tree.PrimitiveTypeTree
import com.sun.source.tree.StatementTree
import com.sun.source.tree.Tree
import com.sun.source.tree.VariableTree
import com.sun.source.util.JavacTask
import java.net.URI
import javax.lang.model.element.Modifier as JDKModifier
import javax.lang.model.type.TypeKind as JDKTypeKind
import javax.tools.Diagnostic
import javax.tools.DiagnosticCollector
import javax.tools.JavaFileObject
import javax.tools.SimpleJavaFileObject
import javax.tools.ToolProvider

class AstParser {

    fun parseToAst(input: String): List<JavaAstNode> {
        return readJdkAst(input).toAstNode()
    }

    private fun readJdkAst(input: String): CompilationUnitTree {
        val compiler = ToolProvider.getSystemJavaCompiler()
            ?: throw IllegalStateException("JDK compiler is not available")

        val diagnostics = DiagnosticCollector<JavaFileObject>()
        val fileObject = StringJavaFileObject(input)
        val fileManager = compiler.getStandardFileManager(diagnostics, null, null)

        fileManager.use { manager ->
            val task = compiler.getTask(
                null,
                manager,
                diagnostics,
                listOf("-proc:none"),
                null,
                listOf(fileObject),
            ) as JavacTask

            val compilationUnits = task.parse().toList()
            val errors = diagnostics.diagnostics
                .filter { diagnostic -> diagnostic.kind == Diagnostic.Kind.ERROR }

            if (errors.isNotEmpty()) {
                throw IllegalArgumentException(
                    errors.joinToString(separator = "\n") { diagnostic -> diagnostic.getMessage(null) }
                )
            }

            return compilationUnits.singleOrNull()
                ?: throw IllegalStateException("Expected a single compilation unit")
        }
    }

    private fun CompilationUnitTree.toAstNode(): List<JavaAstNode> {
        val imports = imports.map { importTree -> importTree.toAstNode() }

        val typeDeclarations = typeDecls.mapNotNull { tree ->
            when (tree) {
                is ClassTree -> tree.toTypeDeclaration()
                else -> null
            }
        }

        return buildList {
            if (packageName != null) {
                add(JavaAstNode.Package(name = packageName.toString()))
            }
            addAll(imports)
            addAll(typeDeclarations)
        }
    }

    private fun ImportTree.toAstNode(): JavaAstNode.Import {
        val name = qualifiedIdentifier.toString()
        return JavaAstNode.Import(
            name = name,
            isStatic = isStatic,
            isAsterisk = name.endsWith(".*"),
        )
    }

    private fun ClassTree.toTypeDeclaration(): JavaAstNode.TypeDeclaration {
        return when (kind) {
            Tree.Kind.CLASS -> this.toClassNode()
            else -> throw InvalidAstTreeNodeException("Invalid kind", this)
        }
    }

    private fun ClassTree.toClassNode(): JavaAstNode.Class {
        val fields = mutableListOf<Field>()
        val constructors = mutableListOf<Constructor>()
        val methods = mutableListOf<Method>()
//        val nestedTypes = mutableListOf<JavaClassDeclarationNode>()
//        val rawMembers = mutableListOf<String>()

        for (member in members) {
            when (member) {
                is VariableTree -> fields.add(member.toField())
                is MethodTree -> {
                    if (member.returnType == null) {
                        constructors.add(member.toConstructor())
                    } else {
                        methods.add(member.toMethod())
                    }
                }
                // TODO:
//                is ClassTree -> nestedTypes.add(member.toJavaAst())
//                else -> rawMembers.add(member.toString())
            }
        }

        return JavaAstNode.Class(
            name = simpleName.toString(),
            modifiers = modifiers.flags.toModifiers(),
            fields = fields,
            constructors = constructors,
            methods = methods
        )
    }

    private fun MethodTree.toConstructor(): Constructor {
        return Constructor(
            modifiers = modifiers.flags.toModifiers(),
            parameters = parameters.toParameters(),
            body = body?.toCodeBlock() ?: CodeBlock.Empty,
        )
    }

    private fun MethodTree.toMethod(): Method {
        val methodReturnType = returnType
            ?: throw IllegalStateException("Expected method return type to be present")

        return Method(
            name = name.toString(),
            modifiers = modifiers.flags.toModifiers(),
            returnType = methodReturnType.toTypeReference(),
            parameters = parameters.toParameters(),
            body = body?.toCodeBlock() ?: CodeBlock.Empty
        )
    }

    private fun List<VariableTree>.toParameters(): List<Parameter> {
        return map { parameter -> parameter.toParameter() }
    }

    private fun VariableTree.toField(): Field {
        // TODO: implement initializer from: initializer?.toString(),
        val type = type.toTypeReference()
        val initExpression = initializer

        val body = if (initExpression == null) {
            InitializerBlock.Empty
        } else {
            InitializerBlock.ExpressionBlock(
                expression = convertExpression(initExpression)
            )
        }

        return Field(
            name = name.toString(),
            type = type,
            modifiers = modifiers.flags.toModifiers(),
            initializer = body
        )
    }

    private fun convertLiteral(
        literal: LiteralTree,
        forType: TypeReference?
    ): Expression.Literal {
        val value = literal.value

        val literalByValue = when {
            value is Boolean -> Expression.BooleanLiteral(value)
            value is Char -> Expression.CharLiteral(value)
            value is Int -> Expression.IntLiteral(value)
            value is String -> Expression.StringLiteral(value)
            else -> null
        }

        val typedLiteral = if (forType != null) {
            when {
                forType.isPrimitiveByte() && value is Int -> Expression.ByteLiteral(value.toByte())
                forType.isPrimitiveChar() && value is Int -> Expression.CharLiteral(value.toChar())
                forType.isPrimitiveInt() && value is Int -> Expression.IntLiteral(value)

                forType.isPrimitiveLong() && value is Long -> Expression.LongLiteral(value)
                forType.isPrimitiveLong() && value is Int -> Expression.LongLiteral(value.toLong())

                forType.isPrimitiveFloat() && value is Float -> Expression.FloatLiteral(value)
                forType.isPrimitiveFloat() && value is Int -> Expression.FloatLiteral(value.toFloat())

                forType.isPrimitiveDouble() && value is Double -> Expression.DoubleLiteral(value)
                forType.isPrimitiveDouble() && value is Int -> Expression.DoubleLiteral(value.toDouble())
                else -> null
            }
        } else {
            null
        }

        return typedLiteral
            ?: literalByValue
            ?: throw InvalidAstTreeNodeException("Invalid literal", literal)
    }

    private fun VariableTree.toParameter(): Parameter {
        val modifiers = modifiers.flags.toModifiers()
        val isVarArgs = Modifier.FINAL !in modifiers && type.toString().endsWith("...")

        return Parameter(
            name = name.toString(),
            type = type.toTypeReference(),
            isVarArgs = isVarArgs,
        )
    }

    private fun Tree.toTypeReference(): TypeReference {
        return when (this) {
            is AnnotatedTypeTree -> underlyingType.toTypeReference()
            // TODO:
//            is ArrayTypeTree -> type.toJavaTypeReference().withArrayDimension()
            is ParameterizedTypeTree -> TypeReference(
                name = type.toString(),
                kind = TypeReferenceKind.DECLARED,
                typeArguments = typeArguments.map { typeArgument -> typeArgument.toTypeReference() },
            )

            is PrimitiveTypeTree -> {
                val kind = when (primitiveTypeKind) {
                    JDKTypeKind.VOID -> TypeReferenceKind.VOID
                    else -> TypeReferenceKind.PRIMITIVE
                }

                TypeReference(
                    name = primitiveTypeKind.name.lowercase(),
                    kind = kind
                )
            }

            else -> TypeReference(
                name = toString(),
                kind = TypeReferenceKind.DECLARED,
            )
        }
    }

    private fun BlockTree.toCodeBlock(): CodeBlock {
        val expressions = statements
            .map { statement -> statement.toExpression() }

        return if (expressions.isEmpty()) {
            CodeBlock.Empty
        } else {
            CodeBlock.Expressions(
                expressions = expressions
            )
        }
    }

    private fun StatementTree.toExpression(): Expression {
        return when (this) {
            is ExpressionStatementTree -> convertExpression(expression)
            else -> throw InvalidAstTreeNodeException("Invalid statement", this)
        }
    }

    private fun convertExpressions(
        expressions: List<ExpressionTree>
    ): List<Expression> {
        return expressions.map { expression -> convertExpression(expression) }
    }

    private fun convertExpression(expression: ExpressionTree): Expression {
        return when (expression) {
            is MethodInvocationTree -> {
                Expression.MethodInvocation(
                    arguments = convertExpressions(expression.arguments),
                    method = convertExpression(expression.methodSelect)
                )
            }

            is MemberSelectTree -> {
                Expression.FieldAccess(
                    name = expression.identifier.toString(),
                    expression = convertExpression(expression.expression)
                )
            }

            is LiteralTree -> convertLiteral(expression, forType = null)

            is IdentifierTree -> Expression.Identifier(
                name = expression.toString()
            )

            is NewClassTree -> {
                Expression.ConstructorInvocation(
                    identifier = convertExpression(expression.identifier),
                    arguments = convertExpressions(expression.arguments)
                )
            }

            is ParameterizedTypeTree -> Expression.TypedIdentifier(
                identifier = expression.type.toTypeReference(),
                types = expression.typeArguments.map { it.toTypeReference() }
            )

            else -> throw InvalidAstTreeNodeException("Invalid expression", expression)
        }
    }

    private fun Set<JDKModifier>.toModifiers(): Set<Modifier> {
        return this.map { modifier ->
            when (modifier) {
                JDKModifier.PUBLIC -> Modifier.PUBLIC
                JDKModifier.PROTECTED -> Modifier.PROTECTED
                JDKModifier.PRIVATE -> Modifier.PRIVATE
                JDKModifier.STATIC -> Modifier.STATIC
                JDKModifier.FINAL -> Modifier.FINAL
                JDKModifier.ABSTRACT -> Modifier.ABSTRACT
                else -> throw InvalidAstTreeNodeException("Invalid modifier", modifier)
            }
        }.toSet()
    }

    private class StringJavaFileObject(
        private val sourceCode: String,
    ) : SimpleJavaFileObject(URI.create("string:///Source.java"), JavaFileObject.Kind.SOURCE) {
        override fun getCharContent(ignoreEncodingErrors: Boolean): CharSequence {
            return sourceCode
        }
    }
}
