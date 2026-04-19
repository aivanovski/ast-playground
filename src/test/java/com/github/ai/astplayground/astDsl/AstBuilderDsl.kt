package com.github.ai.astplayground.astDsl

import com.github.ai.astplayground.astDsl.ExpressionFactory.literal
import com.github.ai.astplayground.transpiler.parser.model.JCodeBlock
import com.github.ai.astplayground.transpiler.parser.model.JConstructor
import com.github.ai.astplayground.transpiler.parser.model.JExpression
import com.github.ai.astplayground.transpiler.parser.model.JField
import com.github.ai.astplayground.transpiler.parser.model.JInitializerBlock
import com.github.ai.astplayground.transpiler.parser.model.JavaAstNode
import com.github.ai.astplayground.transpiler.parser.model.JavaAstNode.TypeDeclaration
import com.github.ai.astplayground.transpiler.parser.model.JMethod
import com.github.ai.astplayground.transpiler.parser.model.Modifier
import com.github.ai.astplayground.transpiler.parser.model.JParameter
import com.github.ai.astplayground.transpiler.parser.model.JTypeReference
import com.github.ai.astplayground.transpiler.parser.model.TypeReferenceKind
import com.github.ai.astplayground.transpiler.parser.model.JVariable

internal object AstBuilderDsl {
    fun javaAst(content: AstBuilder.() -> Unit): List<JavaAstNode> {
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
    private val fields: MutableList<JField> = mutableListOf(),
    private val constructors: MutableList<JConstructor> = mutableListOf(),
    private val methods: MutableList<JMethod> = mutableListOf()
) {

    fun constructor(
        vararg parameters: JParameter,
        body: CodeBlockBuilder.() -> Unit = {}
    ) {
        val codeBlock = CodeBlockBuilder()
            .apply {
                body.invoke(this)
            }
            .buildBlock()

        constructors.add(
            JConstructor(
                modifiers = modifiers,
                parameters = parameters.toList(),
                body = codeBlock
            )
        )
    }

    fun void_method(
        name: String,
        vararg parameters: JParameter,
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
        vararg parameters: JParameter,
        returns: JTypeReference,
        modifiers: Set<Modifier> = emptySet(),
        body: CodeBlockBuilder.() -> Unit = {}
    ) {
        val codeBlock = CodeBlockBuilder()
            .apply {
                body.invoke(this)
            }
            .buildBlock()

        methods.add(
            JMethod(
                name = name,
                modifiers = modifiers,
                returnType = returns,
                parameters = parameters.toList(),
                body = codeBlock
            )
        )
    }

    fun field(
        field: JField
    ) {
        fields.add(field)
    }

    fun buildTypeNode(): TypeDeclaration {
        return JavaAstNode.JClass(
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
    val expressions: MutableList<JExpression> = mutableListOf()
) {

    fun foreach(
        variable: JVariable,
        iterable: JExpression,
        block: CodeBlockBuilder.() -> Unit = {}
    ) {
        expressions.add(
            JExpression.ForEachLoop(
                variable = JExpression.DeclareVariable(
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

    fun variable(name: String, type: JTypeReference, initializer: JInitializerBlock) {
        expressions.add(
            JExpression.DeclareVariable(
                name = name,
                type = type,
                initializer = initializer
            )
        )
    }

    fun assign(variable: JExpression, expression: String) {
        expressions.add(
            JExpression.Assignment(
                variable = variable,
                expression = ExpressionFactory.identifier(expression)
            )
        )
    }

    fun assign(variable: JExpression, expression: JExpression) {
        expressions.add(
            JExpression.Assignment(
                variable = variable,
                expression = expression
            )
        )
    }

    fun assign(vararg fields: String, expression: JExpression) {
        val identifier = ExpressionFactory.identifier(*fields)

        expressions.add(
            JExpression.Assignment(
                variable = identifier,
                expression = expression
            )
        )
    }

    fun `return`(expression: JExpression) {
        expressions.add(JExpression.Return(expression))
    }

    fun `if`(
        condition: JExpression,
        block: CodeBlockBuilder.() -> Unit = {}
    ) {
        expressions.add(
            JExpression.If(
                condition = condition,
                thenExpression = CodeBlockBuilder()
                    .apply {
                        block.invoke(this)
                    }
                    .buildExpression(),
                elseExpression = JExpression.Empty
            )
        )
    }

    fun call(expression: JExpression) {
        expressions.add(expression)
    }

    fun buildBlock(): JCodeBlock {
        return if (expressions.isNotEmpty()) {
            JCodeBlock.Expressions(
                expressions = expressions
            )
        } else {
            JCodeBlock.Empty
        }
    }

    fun buildExpression(): JExpression {
        return when {
            expressions.size > 1 -> JExpression.Expressions(expressions)
            expressions.size == 1 -> expressions.first()
            else -> JExpression.Empty
        }
    }
}

class MethodInvocationBuilder(
    private val blockBuilder: CodeBlockBuilder,
    identifier: JExpression.Identifier
) {

    private var expression: JExpression = identifier

    fun fieldAccess(name: String): MethodInvocationBuilder {
        expression = JExpression.FieldAccess(
            name = name,
            expression = expression
        )

        return this
    }

    fun invoke(
        method: String,
        vararg arguments: JExpression
    ) {
        blockBuilder.expressions.add(
            JExpression.MethodInvocation(
                arguments = arguments.toList(),
                method = JExpression.FieldAccess(
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
        type: JTypeReference,
        initializer: JInitializerBlock = JInitializerBlock.Empty
    ) = JField(
        name = name,
        modifiers = emptySet(),
        type = type,
        initializer = initializer
    )

    fun string(
        name: String,
        value: String? = null
    ) = JField(
        name = name,
        modifiers = emptySet(),
        type = NonPrimitiveTypes.STRING,
        initializer = value?.let { InitializerFactory.stringValue(it) }
            ?: JInitializerBlock.Empty
    )

    fun boolean(
        name: String,
        value: Boolean? = null
    ) = JField(
        name = name,
        modifiers = emptySet(),
        type = PrimitiveTypes.BOOLEAN,
        initializer = value?.let { InitializerFactory.booleanValue(it) }
            ?: JInitializerBlock.Empty
    )

    fun byte(
        name: String,
        value: Byte? = null
    ) = JField(
        name = name,
        modifiers = emptySet(),
        type = PrimitiveTypes.BYTE,
        initializer = value?.let { InitializerFactory.byteValue(it) }
            ?: JInitializerBlock.Empty
    )

    fun char(
        name: String,
        value: Char? = null
    ) = JField(
        name = name,
        modifiers = emptySet(),
        type = PrimitiveTypes.CHAR,
        initializer = value?.let { InitializerFactory.charValue(it) }
            ?: JInitializerBlock.Empty
    )

    fun short(name: String) = JField(
        name = name,
        modifiers = emptySet(),
        type = PrimitiveTypes.SHORT,
        initializer = JInitializerBlock.Empty
    )

    fun int(
        name: String,
        value: Int? = null
    ) = JField(
        name = name,
        modifiers = emptySet(),
        type = PrimitiveTypes.INT,
        initializer = value?.let { InitializerFactory.intValue(it) }
            ?: JInitializerBlock.Empty
    )

    fun long(
        name: String,
        value: Long? = null
    ) = JField(
        name = name,
        modifiers = emptySet(),
        type = PrimitiveTypes.LONG,
        initializer = value?.let { InitializerFactory.longValue(it) }
            ?: JInitializerBlock.Empty
    )

    fun float(
        name: String,
        value: Float? = null
    ) = JField(
        name = name,
        modifiers = emptySet(),
        type = PrimitiveTypes.FLOAT,
        initializer = value?.let { InitializerFactory.floatValue(it) }
            ?: JInitializerBlock.Empty
    )

    fun double(
        name: String,
        value: Double? = null
    ) = JField(
        name = name,
        modifiers = emptySet(),
        type = PrimitiveTypes.DOUBLE,
        initializer = value?.let { InitializerFactory.doubleValue(it) }
            ?: JInitializerBlock.Empty
    )
}

object InitializerFactory {

    fun String.iliteral() = JInitializerBlock.ExpressionBlock(
        expression = this.literal()
    )

    fun Int.iliteral() = JInitializerBlock.ExpressionBlock(
        expression = this.literal()
    )

    fun initializer(producer: () -> JExpression) = JInitializerBlock.ExpressionBlock(
        expression = producer.invoke()
    )

    fun constructor(
        identifier: JExpression,
        arguments: List<JExpression> = emptyList()
    ) = JInitializerBlock.ExpressionBlock(
        expression = JExpression.ConstructorInvocation(
            identifier = identifier,
            arguments = arguments
        )
    )

    fun expression(expression: JExpression) = JInitializerBlock.ExpressionBlock(
        expression = expression
    )

    fun stringValue(value: String) = JInitializerBlock.ExpressionBlock(
        expression = JExpression.StringLiteral(value)
    )

    fun booleanValue(value: Boolean) = JInitializerBlock.ExpressionBlock(
        expression = JExpression.BooleanLiteral(value)
    )

    fun byteValue(value: Byte) = JInitializerBlock.ExpressionBlock(
        expression = JExpression.ByteLiteral(value)
    )

    fun charValue(value: Char) = JInitializerBlock.ExpressionBlock(
        expression = JExpression.CharLiteral(value)
    )

    fun intValue(value: Int) = JInitializerBlock.ExpressionBlock(
        expression = JExpression.IntLiteral(value)
    )

    fun longValue(value: Long) = JInitializerBlock.ExpressionBlock(
        expression = JExpression.LongLiteral(value)
    )

    fun floatValue(value: Float) = JInitializerBlock.ExpressionBlock(
        expression = JExpression.FloatLiteral(value)
    )

    fun doubleValue(value: Double) = JInitializerBlock.ExpressionBlock(
        expression = JExpression.DoubleLiteral(value)
    )
}

object ParametersFactory {

    fun string(name: String) = JParameter(
        name = name,
        type = NonPrimitiveTypes.STRING,
        isVarArgs = false
    )

    fun variable(name: String, type: JTypeReference) =
        JParameter(
            name = name,
            type = type,
            isVarArgs = false
        )

    fun boolean(name: String) =
        JParameter(
            name = name,
            type = PrimitiveTypes.BOOLEAN,
            isVarArgs = false
        )

    fun byte(name: String) =
        JParameter(
            name = name,
            type = PrimitiveTypes.BYTE,
            isVarArgs = false
        )

    fun char(name: String) =
        JParameter(
            name = name,
            type = PrimitiveTypes.CHAR,
            isVarArgs = false
        )

    fun short(name: String) =
        JParameter(
            name = name,
            type = PrimitiveTypes.SHORT,
            isVarArgs = false
        )

    fun int(name: String) =
        JParameter(
            name = name,
            type = PrimitiveTypes.INT,
            isVarArgs = false
        )

    fun long(name: String) =
        JParameter(
            name = name,
            type = PrimitiveTypes.LONG,
            isVarArgs = false
        )

    fun float(name: String) =
        JParameter(
            name = name,
            type = PrimitiveTypes.FLOAT,
            isVarArgs = false
        )

    fun double(name: String) =
        JParameter(
            name = name,
            type = PrimitiveTypes.DOUBLE,
            isVarArgs = false
        )
}

object PrimitiveTypes {

    val BOOLEAN = JTypeReference(
        name = "boolean",
        kind = TypeReferenceKind.PRIMITIVE,
        typeArguments = emptyList()
    )

    val BYTE = JTypeReference(
        name = "byte",
        kind = TypeReferenceKind.PRIMITIVE,
        typeArguments = emptyList()
    )

    val CHAR = JTypeReference(
        name = "char",
        kind = TypeReferenceKind.PRIMITIVE,
        typeArguments = emptyList()
    )

    val SHORT = JTypeReference(
        name = "short",
        kind = TypeReferenceKind.PRIMITIVE,
        typeArguments = emptyList()
    )

    val INT = JTypeReference(
        name = "int",
        kind = TypeReferenceKind.PRIMITIVE,
        typeArguments = emptyList()
    )

    val LONG = JTypeReference(
        name = "long",
        kind = TypeReferenceKind.PRIMITIVE,
        typeArguments = emptyList()
    )

    val FLOAT = JTypeReference(
        name = "float",
        kind = TypeReferenceKind.PRIMITIVE,
        typeArguments = emptyList()
    )

    val DOUBLE = JTypeReference(
        name = "double",
        kind = TypeReferenceKind.PRIMITIVE,
        typeArguments = emptyList()
    )
}

object NonPrimitiveTypes {
    val VOID = JTypeReference(
        name = "void",
        kind = TypeReferenceKind.VOID,
        typeArguments = emptyList()
    )

    val STRING = JTypeReference(
        name = "String",
        kind = TypeReferenceKind.DECLARED,
        typeArguments = emptyList()
    )
}

object VariableFactory {

    fun String.asVariableOf(type: JTypeReference) = JVariable(
        name = this,
        type = type,
        initializer = JInitializerBlock.Empty
    )

    fun String.asVariableOf(typeName: String) = JVariable(
        name = this,
        type = JTypeReference(
            name = typeName,
            kind = TypeReferenceKind.DECLARED
        ),
        initializer = JInitializerBlock.Empty
    )

    fun String.asIntVariable() = intVariable(name = this)

    fun intVariable(
        name: String,
        initializer: JInitializerBlock = JInitializerBlock.Empty
    ) = JVariable(
        name = name,
        type = PrimitiveTypes.INT,
        initializer = initializer
    )
}

object Modifiers {
    private val STATIC = setOf(Modifier.STATIC)

    fun static() = setOf(Modifier.STATIC)
}
