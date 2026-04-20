package com.github.ai.astplayground.transpiler.transformer.model

import com.github.ai.astplayground.transpiler.parser.model.Modifier
import com.github.ai.astplayground.transpiler.parser.model.Operator

sealed class KotlinAstNode(
    open val nodes: List<KotlinAstNode>
) {

    data class Package(
        val name: String
    ) : KotlinAstNode(nodes = emptyList())

    data class Import(
        val name: String,
        val isStatic: Boolean,
        val isAsterisk: Boolean,
    ) : KotlinAstNode(nodes = emptyList())

    sealed class TypeDeclaration(
        open val name: String,
        open val modifiers: Set<Modifier>,
        override val nodes: List<KotlinAstNode>
    ) : KotlinAstNode(nodes = nodes)

    data class KClass(
        override val name: String,
        override val modifiers: Set<Modifier>,
        override val nodes: List<KotlinAstNode>
    ) : TypeDeclaration(name = name, modifiers = modifiers, nodes = nodes)

    data class CompanionObject(
        override val nodes: List<KotlinAstNode>
    ) : KotlinAstNode(nodes)

    data class KField(
        val name: String,
        val modifiers: Set<Modifier>,
        val type: KTypeReference,
        val initializer: KCodeBlock,
    ) : KotlinAstNode(nodes = listOf(initializer))

    data class KMethod(
        val name: String,
        val modifiers: Set<Modifier>,
        val returnType: KTypeReference,
        val parameters: List<KParameter>,
        val body: KCodeBlock
    ) : KotlinAstNode(nodes = listOf(body))

    data class KConstructor(
        val modifiers: Set<Modifier>,
        val parameters: List<KParameter>,
        val body: KCodeBlock
    ) : KotlinAstNode(nodes = listOf(body))

    // Wrapper for Expressions
    data class KExpressionNode(
        val expression: KExpression
    ) : KotlinAstNode(nodes = emptyList())

    // Code blocks
    sealed class KCodeBlock(
        override val nodes: List<KExpressionNode>
    ) : KotlinAstNode(nodes = nodes)

    data object EmptyCodeBlock : KCodeBlock(nodes = emptyList())

    data class ExpressionsBlock(
        val expressions: List<KExpressionNode>
    ) : KCodeBlock(nodes = expressions)
}

data class KTypeReference(
    val isNullable: Boolean,
    val name: String,
    val typeArguments: List<KTypeReference> = emptyList()
)

data class KParameter(
    val name: String,
    val type: KTypeReference,
    val isVarArgs: Boolean,
)

//sealed interface KCodeBlock {
//
//    data object Empty : KCodeBlock
//
//    data class Expressions(
//        val expressions: List<KExpression>
//    ) : KCodeBlock
//}

sealed interface KExpression {
    data object Empty : KExpression

    // Variable initializer
    data class Initializer(
        val initializer: KExpression
    ) : KExpression

    // Return
    data class Return(
        val expression: KExpression
    ) : KExpression

    // Conditions
    data class If(
        val condition: KExpression,
        val thenExpression: KExpression,
        val elseExpression: KExpression
    ) : KExpression

    // Loops
    data class ForEachLoop(
        val variable: DeclareVariable,
        val iterable: KExpression,
        val body: KExpression
    ) : KExpression

    // Literals
    sealed interface Literal : KExpression
    data class BooleanLiteral(val value: Boolean) : Literal
    data class ByteLiteral(val value: Byte) : Literal
    data class CharLiteral(val value: Char) : Literal
    data class IntLiteral(val value: Int) : Literal
    data class LongLiteral(val value: Long) : Literal
    data class FloatLiteral(val value: Float) : Literal
    data class DoubleLiteral(val value: Double) : Literal
    data class StringLiteral(val value: String) : Literal
    data object Null : Literal

    // Assignment
    data class Assignment(
        val variable: KExpression,
        val expression: KExpression
    ) : KExpression

    // Expressions
    data class Expressions(
        val expressions: List<KExpression>
    ) : KExpression

    data class BinaryExpression(
        val operator: Operator,
        val lhs: KExpression,
        val rhs: KExpression
    ) : KExpression

    // Methods
    data class MethodInvocation(
        val arguments: List<KExpression>,
        val method: KExpression
    ) : KExpression

    // Field access
    data class FieldAccess(
        val name: String,
        val expression: KExpression
    ) : KExpression

    // Identifiers
    data class Identifier(
        val name: String,
        val isUnsafeCall: Boolean
    ) : KExpression

    data class TypedIdentifier(
        val identifier: KTypeReference,
        val types: List<KTypeReference>
    ) : KExpression

    // Constructor
    data class ConstructorInvocation(
        val identifier: KExpression,
        val arguments: List<KExpression>,
    ) : KExpression

    // Variables
    data class DeclareVariable(
        val name: String,
        val type: KTypeReference,
        val initializer: KotlinAstNode.KCodeBlock
    ) : KExpression
}