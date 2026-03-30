package com.github.ai.astplayground.assertionDsl

import com.github.ai.astplayground.assertionDsl.ExpressionFactory.literal
import com.github.ai.astplayground.transpiler.model.CodeBlock
import com.github.ai.astplayground.transpiler.model.Constructor
import com.github.ai.astplayground.transpiler.model.Expression
import com.github.ai.astplayground.transpiler.model.Field
import com.github.ai.astplayground.transpiler.model.InitializerBlock
import com.github.ai.astplayground.transpiler.model.JavaAstNode
import com.github.ai.astplayground.transpiler.model.JavaAstNode.TypeDeclaration
import com.github.ai.astplayground.transpiler.model.Method
import com.github.ai.astplayground.transpiler.model.Modifier
import com.github.ai.astplayground.transpiler.model.Parameter
import com.github.ai.astplayground.transpiler.model.TypeReference
import com.github.ai.astplayground.transpiler.model.TypeReferenceKind
import com.github.ai.astplayground.transpiler.model.Variable

internal object AstBuilderDsl {
    fun buildAst(content: AstBuilder.() -> Unit): List<JavaAstNode> {
        val builder = AstBuilder()
            .apply {
                content.invoke(this)
            }

        return builder.nodes
    }
}

class AstBuilder(
    val nodes: MutableList<JavaAstNode> = mutableListOf<JavaAstNode>()
) {

    fun `package`(name: String) {
        nodes.add(JavaAstNode.Package(name))
    }

    fun import(
        name: String,
        isStatic: Boolean = false,
        isAsterisk: Boolean = false
    ) {
        nodes.add(
            JavaAstNode.Import(
                name = name,
                isStatic = isStatic,
                isAsterisk = isAsterisk
            )
        )
    }

    fun `class`(
        name: String,
        content: TypeBuilder.() -> Unit = {}
    ) {
        val builder = TypeBuilder(
            name = name,
            modifiers = emptySet(),
        )
            .apply {
                content.invoke(this)
            }

        nodes.add(builder.buildTypeNode())
    }
}

class TypeBuilder(
    private val name: String,
    private val modifiers: Set<Modifier>,
    private val fields: MutableList<Field> = mutableListOf(),
    private val constructors: MutableList<Constructor> = mutableListOf(),
    private val methods: MutableList<Method> = mutableListOf()
) {

    fun constructor(
        vararg parameters: Parameter,
        body: CodeBlockBuilder.() -> Unit = {}
    ) {
        val codeBlock = CodeBlockBuilder()
            .apply {
                body.invoke(this)
            }
            .buildBlock()

        constructors.add(
            Constructor(
                modifiers = modifiers,
                parameters = parameters.toList(),
                body = codeBlock
            )
        )
    }

    fun void_method(
        name: String,
        vararg parameters: Parameter,
        modifiers: Set<Modifier> = emptySet(),
        body: CodeBlockBuilder.() -> Unit = {}
    ) = method(
        name = name,
        parameters = parameters,
        returns = TypeReferenceFactory.void(),
        modifiers = modifiers,
        body = body
    )

    fun method(
        name: String,
        vararg parameters: Parameter,
        returns: TypeReference,
        modifiers: Set<Modifier> = emptySet(),
        body: CodeBlockBuilder.() -> Unit = {}
    ) {
        val codeBlock = CodeBlockBuilder()
            .apply {
                body.invoke(this)
            }
            .buildBlock()

        methods.add(
            Method(
                name = name,
                modifiers = modifiers,
                returnType = returns,
                parameters = parameters.toList(),
                body = codeBlock
            )
        )
    }

    fun field(
        field: Field
    ) {
        fields.add(field)
    }

    fun buildTypeNode(): TypeDeclaration {
        return JavaAstNode.Class(
            name = name,
            modifiers = modifiers,
            fields = fields,
            constructors = constructors,
            methods = methods
        )
    }
}

//class MethodBuilder(
//    val name: String,
//    val modifiers: Set<Modifier>,
//    val returnType: TypeReference,
//    val parameters: List<Parameter>,
////    val body: CodeBlock
//) {
//
//    infix fun `return`(type: TypeReference) {
//    }
//
//    fun buildMethod() {
//
//    }
//}

class CodeBlockBuilder(
    val expressions: MutableList<Expression> = mutableListOf()
) {

    fun foreach(
        variable: Variable,
        iterable: Expression,
        block: CodeBlockBuilder.() -> Unit = {}
    ) {
        expressions.add(
            Expression.ForEachLoop(
                variable = Expression.DeclareVariable(
                    name = variable.name,
                    type = variable.type,
                    initializer = variable.initializer
                ),
                iterable = iterable,
                body = CodeBlockBuilder()
                    .apply {
                        block.invoke(this)
                    }
                    .buildExpression()
            )
        )
    }

    fun variable(name: String, type: TypeReference, initializer: InitializerBlock) {
        expressions.add(
            Expression.DeclareVariable(
                name = name,
                type = type,
                initializer = initializer
            )
        )
    }

    fun assign(variable: Expression, expression: String) {
        expressions.add(
            Expression.Assignment(
                variable = variable,
                expression = ExpressionFactory.identifier(expression)
            )
        )
    }

    fun assign(variable: Expression, expression: Expression) {
        expressions.add(
            Expression.Assignment(
                variable = variable,
                expression = expression
            )
        )
    }

    fun assign(vararg fields: String, expression: Expression) {
        val identifier = ExpressionFactory.identifier(*fields)

        expressions.add(
            Expression.Assignment(
                variable = identifier,
                expression = expression
            )
        )
    }

    fun `return`(expression: Expression) {
        expressions.add(Expression.Return(expression))
    }

    fun `if`(
        condition: Expression,
        block: CodeBlockBuilder.() -> Unit = {}
    ) {
        expressions.add(
            Expression.If(
                condition = condition,
                thenExpression = CodeBlockBuilder()
                    .apply {
                        block.invoke(this)
                    }
                    .buildExpression(),
                elseExpression = Expression.Empty
            )
        )
    }

    fun call(expression: Expression) {
        expressions.add(expression)
    }

    fun buildBlock(): CodeBlock {
        return if (expressions.isNotEmpty()) {
            CodeBlock.Expressions(
                expressions = expressions
            )
        } else {
            CodeBlock.Empty
        }
    }

    fun buildExpression(): Expression {
        return when {
            expressions.size > 1 -> Expression.Expressions(expressions)
            expressions.size == 1 -> expressions.first()
            else -> Expression.Empty
        }
    }
}

class MethodInvocationBuilder(
    private val blockBuilder: CodeBlockBuilder,
    identifier: Expression.Identifier
) {

    private var expression: Expression = identifier

    fun fieldAccess(name: String): MethodInvocationBuilder {
        expression = Expression.FieldAccess(
            name = name,
            expression = expression
        )

        return this
    }

    fun invoke(
        method: String,
        vararg arguments: Expression
    ) {
        blockBuilder.expressions.add(
            Expression.MethodInvocation(
                arguments = arguments.toList(),
                method = Expression.FieldAccess(
                    name = method,
                    expression = expression
                )
            )
        )
    }
}

object FieldFactory {

    fun variable(
        name: String,
        type: TypeReference,
        initializer: InitializerBlock = InitializerBlock.Empty
    ) = Field(
        name = name,
        modifiers = emptySet(),
        type = type,
        initializer = initializer
    )

    fun string(
        name: String,
        value: String? = null
    ) = Field(
        name = name,
        modifiers = emptySet(),
        type = NonPrimitiveTypes.STRING,
        initializer = value?.let { InitializerFactory.stringValue(it) }
            ?: InitializerBlock.Empty
    )

    fun boolean(
        name: String,
        value: Boolean? = null
    ) = Field(
        name = name,
        modifiers = emptySet(),
        type = PrimitiveTypes.BOOLEAN,
        initializer = value?.let { InitializerFactory.booleanValue(it) }
            ?: InitializerBlock.Empty
    )

    fun byte(
        name: String,
        value: Byte? = null
    ) = Field(
        name = name,
        modifiers = emptySet(),
        type = PrimitiveTypes.BYTE,
        initializer = value?.let { InitializerFactory.byteValue(it) }
            ?: InitializerBlock.Empty
    )

    fun char(
        name: String,
        value: Char? = null
    ) = Field(
        name = name,
        modifiers = emptySet(),
        type = PrimitiveTypes.CHAR,
        initializer = value?.let { InitializerFactory.charValue(it) }
            ?: InitializerBlock.Empty
    )

    fun short(name: String) = Field(
        name = name,
        modifiers = emptySet(),
        type = PrimitiveTypes.SHORT,
        initializer = InitializerBlock.Empty
    )

    fun int(
        name: String,
        value: Int? = null
    ) = Field(
        name = name,
        modifiers = emptySet(),
        type = PrimitiveTypes.INT,
        initializer = value?.let { InitializerFactory.intValue(it) }
            ?: InitializerBlock.Empty
    )

    fun long(
        name: String,
        value: Long? = null
    ) = Field(
        name = name,
        modifiers = emptySet(),
        type = PrimitiveTypes.LONG,
        initializer = value?.let { InitializerFactory.longValue(it) }
            ?: InitializerBlock.Empty
    )

    fun float(
        name: String,
        value: Float? = null
    ) = Field(
        name = name,
        modifiers = emptySet(),
        type = PrimitiveTypes.FLOAT,
        initializer = value?.let { InitializerFactory.floatValue(it) }
            ?: InitializerBlock.Empty
    )

    fun double(
        name: String,
        value: Double? = null
    ) = Field(
        name = name,
        modifiers = emptySet(),
        type = PrimitiveTypes.DOUBLE,
        initializer = value?.let { InitializerFactory.doubleValue(it) }
            ?: InitializerBlock.Empty
    )
}

object InitializerFactory {

    fun String.iliteral() = InitializerBlock.ExpressionBlock(
        expression = this.literal()
    )

    fun Int.iliteral() = InitializerBlock.ExpressionBlock(
        expression = this.literal()
    )

    fun initializer(producer: () -> Expression) = InitializerBlock.ExpressionBlock(
        expression = producer.invoke()
    )

    fun constructor(
        identifier: Expression,
        arguments: List<Expression> = emptyList()
    ) = InitializerBlock.ExpressionBlock(
        expression = Expression.ConstructorInvocation(
            identifier = identifier,
            arguments = arguments
        )
    )

    fun expression(expression: Expression) = InitializerBlock.ExpressionBlock(
        expression = expression
    )

    fun stringValue(value: String) = InitializerBlock.ExpressionBlock(
        expression = Expression.StringLiteral(value)
    )

    fun booleanValue(value: Boolean) = InitializerBlock.ExpressionBlock(
        expression = Expression.BooleanLiteral(value)
    )

    fun byteValue(value: Byte) = InitializerBlock.ExpressionBlock(
        expression = Expression.ByteLiteral(value)
    )

    fun charValue(value: Char) = InitializerBlock.ExpressionBlock(
        expression = Expression.CharLiteral(value)
    )

    fun intValue(value: Int) = InitializerBlock.ExpressionBlock(
        expression = Expression.IntLiteral(value)
    )

    fun longValue(value: Long) = InitializerBlock.ExpressionBlock(
        expression = Expression.LongLiteral(value)
    )

    fun floatValue(value: Float) = InitializerBlock.ExpressionBlock(
        expression = Expression.FloatLiteral(value)
    )

    fun doubleValue(value: Double) = InitializerBlock.ExpressionBlock(
        expression = Expression.DoubleLiteral(value)
    )
}

object ParametersFactory {

    fun string(name: String) = Parameter(
        name = name,
        type = NonPrimitiveTypes.STRING,
        isVarArgs = false
    )

    fun variable(name: String, type: TypeReference) =
        Parameter(
            name = name,
            type = type,
            isVarArgs = false
        )

    fun boolean(name: String) =
        Parameter(
            name = name,
            type = PrimitiveTypes.BOOLEAN,
            isVarArgs = false
        )

    fun byte(name: String) =
        Parameter(
            name = name,
            type = PrimitiveTypes.BYTE,
            isVarArgs = false
        )

    fun char(name: String) =
        Parameter(
            name = name,
            type = PrimitiveTypes.CHAR,
            isVarArgs = false
        )

    fun short(name: String) =
        Parameter(
            name = name,
            type = PrimitiveTypes.SHORT,
            isVarArgs = false
        )

    fun int(name: String) =
        Parameter(
            name = name,
            type = PrimitiveTypes.INT,
            isVarArgs = false
        )

    fun long(name: String) =
        Parameter(
            name = name,
            type = PrimitiveTypes.LONG,
            isVarArgs = false
        )

    fun float(name: String) =
        Parameter(
            name = name,
            type = PrimitiveTypes.FLOAT,
            isVarArgs = false
        )

    fun double(name: String) =
        Parameter(
            name = name,
            type = PrimitiveTypes.DOUBLE,
            isVarArgs = false
        )
}

object PrimitiveTypes {

    val BOOLEAN = TypeReference(
        name = "boolean",
        kind = TypeReferenceKind.PRIMITIVE,
        typeArguments = emptyList()
    )

    val BYTE = TypeReference(
        name = "byte",
        kind = TypeReferenceKind.PRIMITIVE,
        typeArguments = emptyList()
    )

    val CHAR = TypeReference(
        name = "char",
        kind = TypeReferenceKind.PRIMITIVE,
        typeArguments = emptyList()
    )

    val SHORT = TypeReference(
        name = "short",
        kind = TypeReferenceKind.PRIMITIVE,
        typeArguments = emptyList()
    )

    val INT = TypeReference(
        name = "int",
        kind = TypeReferenceKind.PRIMITIVE,
        typeArguments = emptyList()
    )

    val LONG = TypeReference(
        name = "long",
        kind = TypeReferenceKind.PRIMITIVE,
        typeArguments = emptyList()
    )

    val FLOAT = TypeReference(
        name = "float",
        kind = TypeReferenceKind.PRIMITIVE,
        typeArguments = emptyList()
    )

    val DOUBLE = TypeReference(
        name = "double",
        kind = TypeReferenceKind.PRIMITIVE,
        typeArguments = emptyList()
    )
}

object NonPrimitiveTypes {
    val VOID = TypeReference(
        name = "void",
        kind = TypeReferenceKind.VOID,
        typeArguments = emptyList()
    )

    val STRING = TypeReference(
        name = "String",
        kind = TypeReferenceKind.DECLARED,
        typeArguments = emptyList()
    )
}

object VariableFactory {

    fun String.asVariableOf(typeName: String) = Variable(
        name = this,
        type = TypeReference(
            name = typeName,
            kind = TypeReferenceKind.DECLARED
        ),
        initializer = InitializerBlock.Empty
    )

    fun String.asIntVariable() = intVariable(name = this)

    fun intVariable(
        name: String,
        initializer: InitializerBlock = InitializerBlock.Empty
    ) = Variable(
        name = name,
        type = PrimitiveTypes.INT,
        initializer = initializer
    )
}

object Modifiers {
    private val STATIC = setOf(Modifier.STATIC)

    fun static() = setOf(Modifier.STATIC)
}
