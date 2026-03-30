package com.github.ai.astplayground.transpiler.model

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

    data class Class(
        override val name: String,
        override val modifiers: Set<Modifier>,
        val fields: List<Field>,
        val constructors: List<Constructor>,
        val methods: List<Method>
    ) : TypeDeclaration
}

data class Constructor(
    val modifiers: Set<Modifier>,
    val parameters: List<Parameter>,
    val body: CodeBlock
)

data class Method(
    val name: String,
    val modifiers: Set<Modifier>,
    val returnType: TypeReference,
    val parameters: List<Parameter>,
    val body: CodeBlock
)

data class Field(
    val name: String,
    val modifiers: Set<Modifier>,
    val type: TypeReference,
    val initializer: InitializerBlock,
)

sealed interface InitializerBlock {
    data object Empty : InitializerBlock
    data class ExpressionBlock(
        val expression: Expression
    ) : InitializerBlock
}

sealed interface Expression {
    data object Empty : Expression

    // Return
    data class Return(
        val expression: Expression
    ) : Expression

    // Literals
    sealed interface Literal : Expression
    data class BooleanLiteral(val value: Boolean) : Literal
    data class ByteLiteral(val value: Byte) : Literal
    data class CharLiteral(val value: Char) : Literal
    data class IntLiteral(val value: Int) : Literal
    data class LongLiteral(val value: Long) : Literal
    data class FloatLiteral(val value: Float) : Literal
    data class DoubleLiteral(val value: Double) : Literal
    data class StringLiteral(val string: String) : Literal

    // Expressions
    data class Expressions(
        val expressions: List<Expression>
    ) : Expression

    data class BinaryExpression(
        val operator: Operator,
        val lhs: Expression,
        val rhs: Expression
    ) : Expression

    // Methods
    data class MethodInvocation(
        val arguments: List<Expression>,
        val method: Expression
    ) : Expression

    // Field access
    data class FieldAccess(
        val name: String,
        val expression: Expression
    ) : Expression

    // Identifiers
    data class Identifier(
        val name: String
    ) : Expression

    data class TypedIdentifier(
        val identifier: TypeReference,
        val types: List<TypeReference>
    ) : Expression

    // Constructor
    data class ConstructorInvocation(
        val identifier: Expression,
        val arguments: List<Expression>,
    ) : Expression

    // Variables
    data class DeclareVariable(
        val name: String,
        val type: TypeReference,
        val initializer: InitializerBlock
    ) : Expression
}

sealed interface CodeBlock {

    data object Empty : CodeBlock

    data class RawStatements(
        val statements: List<String>
    ) : CodeBlock

    data class Expressions(
        val expressions: List<Expression>
    ) : CodeBlock
}

data class Parameter(
    val name: String,
    val type: TypeReference,
    val isVarArgs: Boolean,
)

data class TypeReference(
    val name: String,
    val kind: TypeReferenceKind,
    val typeArguments: List<TypeReference> = emptyList()
)

enum class TypeReferenceKind {
    PRIMITIVE,
    DECLARED,
    VOID,
}

//enum class TypeKind {
//    CLASS,
//    INTERFACE,
//    ENUM,
//    ANNOTATION,
//}

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
    DIVIDE
}
