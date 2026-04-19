package com.github.ai.astplayground.transpiler.parser.model

sealed interface JavaAstNode {

    data class Package(
        val name: String
    ) : JavaAstNode

    data class Import(
        val name: String,
        val isStatic: Boolean,
        val isAsterisk: Boolean,
    ) : JavaAstNode

    sealed interface TypeDeclaration : JavaAstNode {
        val name: String
        val modifiers: Set<Modifier>
    }

    data class JClass(
        override val name: String,
        override val modifiers: Set<Modifier>,
        val fields: List<JField>,
        val constructors: List<JConstructor>,
        val methods: List<JMethod>
    ) : TypeDeclaration
}

data class JConstructor(
    val modifiers: Set<Modifier>,
    val parameters: List<JParameter>,
    val body: JCodeBlock
)

data class JMethod(
    val name: String,
    val modifiers: Set<Modifier>,
    val returnType: JTypeReference,
    val parameters: List<JParameter>,
    val body: JCodeBlock
)

data class JField(
    val name: String,
    val modifiers: Set<Modifier>,
    val type: JTypeReference,
    val initializer: JInitializerBlock,
)

data class JVariable(
    val name: String,
    val type: JTypeReference,
    val initializer: JInitializerBlock,
)

sealed interface JCodeBlock {

    data object Empty : JCodeBlock

    data class Expressions(
        val expressions: List<JExpression>
    ) : JCodeBlock
}

sealed interface JInitializerBlock {
    data object Empty : JInitializerBlock
    data class ExpressionBlock(
        val expression: JExpression
    ) : JInitializerBlock
}

sealed interface JExpression {
    data object Empty : JExpression

    // Return
    data class Return(
        val expression: JExpression
    ) : JExpression

    // Conditions
    data class If(
        val condition: JExpression,
        val thenExpression: JExpression,
        val elseExpression: JExpression
    ) : JExpression

    // Loops
    data class ForLoop(
        val initializers: List<JExpression>,
        val condition: JExpression,
        val updates: List<JExpression>,
        val body: JExpression
    ) : JExpression

    data class ForEachLoop(
        val variable: DeclareVariable,
        val iterable: JExpression,
        val body: JExpression
    ) : JExpression

    // Literals
    sealed interface Literal : JExpression
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
        val variable: JExpression,
        val expression: JExpression
    ) : JExpression

    // Expressions
    data class Expressions(
        val expressions: List<JExpression>
    ) : JExpression

    data class BinaryExpression(
        val operator: Operator,
        val lhs: JExpression,
        val rhs: JExpression
    ) : JExpression

    // Methods
    data class MethodInvocation(
        val arguments: List<JExpression>,
        val method: JExpression
    ) : JExpression

    // Field access
    data class FieldAccess(
        val name: String,
        val expression: JExpression
    ) : JExpression

    // Identifiers
    data class Identifier(
        val name: String
    ) : JExpression

    data class TypedIdentifier(
        val identifier: JTypeReference,
        val types: List<JTypeReference>
    ) : JExpression

    // Constructor
    data class ConstructorInvocation(
        val identifier: JExpression,
        val arguments: List<JExpression>,
    ) : JExpression

    // Variables
    data class DeclareVariable(
        val name: String,
        val type: JTypeReference,
        val initializer: JInitializerBlock
    ) : JExpression
}

data class JParameter(
    val name: String,
    val type: JTypeReference,
    val isVarArgs: Boolean,
)

data class JTypeReference(
    val name: String,
    val kind: TypeReferenceKind,
    val typeArguments: List<JTypeReference> = emptyList()
)

enum class TypeReferenceKind {
    PRIMITIVE,
    DECLARED,
    VOID,
}

enum class Modifier {
    PUBLIC,
    PROTECTED,
    PRIVATE,
    STATIC,
    FINAL,
    ABSTRACT,
}

enum class Operator {
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    LESS_THAN,
    GREATER_THAN,
    EQUALS,
    NOT_EQUALS,
    AND,
    OR
}
